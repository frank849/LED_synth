import java.lang.*;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;

public class wave_writerc {
  DataOutputStream o;  
  boolean stereo = true;
  boolean bits16 = true;
  boolean downsampling2 = false;
  int written_out = 0;
  int remaining = 0;
  float fade_in;
  float fade_in_a;
  float fade_out;
  float fade_out_a;
  double svol;
  int sample_rate;
  int wave_size;
  long d_seed;
  wave_writerc(String outfile,int flags,float fi,float fo) {
    try {
      d_seed = System.currentTimeMillis();
      stereo = (flags & 1) != 0;
      bits16 = (flags & 2) != 0;
      o = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile,false)));
      int sls = main_app.song_list.size();
      int songlen = 0;
      for (int i = 0;i < sls;i++) {
         song_list_entryc e = (song_list_entryc) main_app.song_list.get(i);
         patternc p = (patternc) main_app.pattern_list.get(e.pattern);
         songlen = songlen + p.get_length();
      }
      downsampling2 = (flags & 4) != 0;
      sampleplayerc.sample_rate = pattern_playerc.sample_rate;
      sample_rate = sampleplayerc.sample_rate;
      int l2 = pattern_playerc.get_note_length();
      wave_size = l2*songlen;
      writeheader();
      remaining = wave_size;
      written_out = 0;
      fade_in = fi;
      if ((fi*sample_rate) > 1.0f) {
        fade_in_a = 1.0f / (fi*sample_rate);
      } else {
        fade_in_a = 0.0f;
        svol = 1.0f;
      }
      fade_out = fo;
      if ((fo*sample_rate) > 1.0f) {
        fade_out_a = 1.0f / (fo*sample_rate);
      } else {
        fade_in_a = 0.0f;
      }
    } catch (Exception e) {
      e.printStackTrace();            
    }
  }
  void close() {
    try {
      o.close();
    } catch (Exception e) {
      e.printStackTrace();            
    }
  }
  static int wave_id_header_size = 44;
  static int wave_id_size = 32;
  //static int wave_id[] = {1,8,9,8,10,4,13,11,
  //                        5,12,6,7,3,9,7,8,
  //                        2,0,14,13,6,15,10,14,
  //                        10,3,10,12,0,10,2,6};
  static int wave_id = main_app.ID_NUM;
  static boolean read_wave_id(File f) {
    try {
      if (f.exists() == false){ return true;}      

      DataInputStream i = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
      //for (int i2 = 0;i2 < wave_id_header_size;i2++) {
      //  i.read();
      //}
      int a = check_header(i);
      if (a == 0) {return false;}
      for (int i2 = 0;i2 < wave_id_size;i2++) {
        int b = i.read();
        if (((b ^ (wave_id >> i2)) & 1) == 1) {
          i.close();
          return false;
        }
        for (int i1 = 1;i1 < a;i1++) {i.read();}
      }
      //  if (wave_id[i2] != (b & 127)) {
      //  }
      //}
      i.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();            
    }
    return false;
  }
  //void write_wave_id(boolean bits16) {
  //  try {
  //    for (int i = 0;i < wave_id_size;i++) {
  //      if (bits16 == true) {
  //        o.write(wave_id[i]);
  //      } else {
  //        o.write(wave_id[i] ^ 128);
  //      }
  //    }
  //  } catch (Exception e) {
  //    e.printStackTrace();            
  //  }
  //}  
  static int swapbytesofword(int n) {
    return (((n >> 8) & 255) | ((n & 255) << 8));
  }
  static int swapbytesofint(int n) {
    return (((n >> 24) & 255) << 0) | (((n >> 16) & 255) << 8) |
    (((n >> 8) & 255) << 16) | (((n >> 0) & 255) << 24);
  }
  void writeheaderB() {
    try {
      o.writeBytes("RIFX");
      o.writeInt(36+wave_size);
      o.writeBytes("WAVE");
      o.writeBytes("fmt ");
      o.writeInt(16);
      o.writeShort(1);
      if (stereo == true) {
        o.writeShort(2);
      } else {
        o.writeShort(1);
      }
      o.writeInt(sample_rate);
      int b = 1;
      if (stereo == true) {b = b * 2;}
      if (bits16 == true) {b = b * 2;}
      
      o.writeInt(sample_rate*b);
      o.writeShort(b);
      if (bits16 == true){
        o.writeShort(16);
      } else {
        o.writeShort(8);
      }
      o.writeBytes("data");
      o.writeInt((wave_size*b)+wave_id_size);
      //write_wave_id(bits16);
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }  

  void writeheader() {
    try {
      o.writeBytes("RIFF");
      o.writeInt(swapbytesofint(36+wave_size));
      o.writeBytes("WAVE");
      o.writeBytes("fmt ");
      o.writeInt(swapbytesofint(16));
      o.writeShort(swapbytesofword(1));
      if (stereo == true) {
        o.writeShort(swapbytesofword(2));
      } else {
        o.writeShort(swapbytesofword(1));
      }
      int sample_rate2 = sample_rate;
      if (downsampling2) {sample_rate2 = sample_rate2 >> 1;}

      o.writeInt(swapbytesofint(sample_rate2));
      int b = 1;
      if (stereo == true) {b = b * 2;}
      if (bits16 == true) {b = b * 2;}
      
      o.writeInt(swapbytesofint(sample_rate2*b));
      o.writeShort(swapbytesofword(b));
      if (bits16 == true){
        o.writeShort(swapbytesofword(16));
      } else {
        o.writeShort(swapbytesofword(8));
      }
      o.writeBytes("data");
      o.writeInt(swapbytesofint((wave_size*b)+wave_id_size));
      //write_wave_id(bits16);
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }  
  static String read_fixed_length_string(DataInputStream i,int l) throws Exception {
    byte b[] = new byte[l];
    i.read(b);
    return new String(b,"UTF-8");
  }
  static int check_header(DataInputStream i) {
    try {
      //int RIFF = i.readInt();
      //System.out.println("RIFF: " + RIFF);
      //System.out.println(read_fixed_length_string(i,4));
      String riff = read_fixed_length_string(i,4); 
      if (riff.equals("RIFF") == false) {return 0;}
      i.readInt();
      //System.out.println(read_fixed_length_string(i,4));
      String wave = read_fixed_length_string(i,4); 
      if (wave.equals("WAVE") == false) {return 0;}
      String fmt = read_fixed_length_string(i,4);
      if (fmt.equals("fmt ") == false) {return 0;}
      //if (fmt.equals("fmt ")) {System.out.println("fmt match");}
      //int WAVE = i.readInt();
      //System.out.println("WAVE: " + WAVE);
      //int fmt = i.readInt();
      //System.out.println("fmt : " + fmt);
      int a = 0;
      a = swapbytesofint(i.readInt());
      if (a != 16) {return 0;}
      //System.out.println(a);
      a = swapbytesofword(i.readShort());
      if (a != 1) {return 0;}
      //System.out.println(a);
      int ch = swapbytesofword(i.readShort());//channels
      //System.out.println("channels: " + ch);
      if ((ch != 1) & (ch != 2)) {return 0;}
      i.readInt();
      i.readInt();
      a = swapbytesofword(i.readShort());
      //System.out.println(b);
      int bits = swapbytesofword(i.readShort());//bits
      //System.out.println("bits: " + bits);
      if ((bits != 8) & (bits != 16)) {return 0;} 
      if (a != ((bits >> 3)*ch)) {return 0;}
      //int data = i.readInt();
      //System.out.println("data: " + data);
      //System.out.println(read_fixed_length_string(i,4));
      String data = read_fixed_length_string(i,4);
      if (data.equals("data") == false) {return 0;}
      i.readInt();

      return (bits >> 3);
    } catch (Exception e) {
      e.printStackTrace();      
    }
    return 0;
  }
  static double clip(double a) {
    return pattern_playerc.clip(a);
  }
  int dither(double a) {
    d_seed = (d_seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
    double f = d_seed;
    f = f / ((double) (1L << 48));
    int i = (int) a;
    double d = a-i;
    if ((d > 0) & (f < d)) {
      return i+1;
    } 
    if ((d < 0) & (f < -d)) {
      return i-1;
    } 
    return i;
  }
  void write_song() {
    instrumentc ins = main_app.song_player.ins;
    int volume = main_app.pattern_player.volume;
    int bass = main_app.pattern_player.bass;
    song_playerc sp = new song_playerc(ins);
    int n = main_app.song_list.size();
    int bsize = 16;
    float buf1[] = new float[bsize];
    float buf2[] = new float[bsize];
    float buf3[] = new float[bsize];
    float filter1 = 0.0f;
    float filter3 = 0.0f;
    int z = 0;
    half_band_filter ds_ft = new half_band_filter();
    half_band_filter ds_ft2 = new half_band_filter();

    sp.play_pattern(n-1);
    sp.update_players();
    //for (int x = 0;x < sp.pattern.length;x++) {
      pattern_playerc.play_col(sp,sp.pattern.length-1,bsize);
      int nl = pattern_playerc.get_note_length();
      z = z + nl;
      while (z >= bsize) {
        for (int i = 0;i < bsize;i++) {buf2[i] = 0.0f;}
        for (int y = 0;y < sp.player_array.size();y++) {
          sampleplayerc p = (sampleplayerc) sp.player_array.get(y);
          p.play(buf2,bsize);
        }
        z = z - bsize;
      }
    //}
    for (int t = 0;t < n;t++) {
      sp.play_pattern(t);
      sp.update_players();
      for (int x = 0;x < sp.pattern.length;x++) {
        pattern_playerc.play_col(sp,x,bsize);
        nl = pattern_playerc.get_note_length();
        z = z + nl;
        while (z >= bsize) {
          for (int i = 0;i < bsize;i++) {
            buf1[i] = 0.0f;
            buf2[i] = 0.0f;
            buf3[i] = 0.0f;
          } 
          for (int y = 0;y < sp.player_array.size();y++) {
            sampleplayerc p = (sampleplayerc) sp.player_array.get(y);
            if (p.pan == 1) {p.play(buf1,bsize);}
            if (p.pan == 2) {p.play(buf2,bsize);}
            if (p.pan == 3) {p.play(buf3,bsize);}
          }
          float v = (float) (Math.exp(Math.log(10.0)*(volume/20.0f)));
          v = v * 0.001f;
          double v2 = 127.0;
          if (bits16 == true) {
            v2 = 32767.0;
          }
          if (stereo == true) {
            for (int i = 0;i < bsize;i++) {
              buf1[i] = buf1[i] + buf2[i];
              buf3[i] = buf3[i] + buf2[i];
            }
            equalizer.e1.filter(buf1,bsize);
            equalizer.e3.filter(buf3,bsize);
            if (downsampling2 == true) {
              for (int i = 0;i < bsize;i++) {
                double f1 = clip(ds_ft.filter(buf1[i]*v,i)) * v2;
                double f2 = clip(ds_ft2.filter(buf3[i]*v,i)) * v2;
                if ((i & 1) == 1) {
                  write_stereo_sample(dither(f1),dither(f2));
                }
              }
            } else {
              for (int i = 0;i < bsize;i++) {
                int ws1 = (dither(clip(buf1[i]*v) * v2));          
                int ws2 = (dither(clip(buf3[i]*v) * v2));          
                write_stereo_sample(ws1,ws2);
              }
            }
          } else {
            for (int i = 0;i < bsize;i++) {
              buf2[i] = buf1[i] + buf2[i] + buf3[i];
            }
            equalizer.e2.filter(buf2,bsize);
            if (downsampling2 == true) {
              //System.out.println("downsampling2");
              for (int i = 0;i < bsize;i++) {
                double f = clip(ds_ft.filter(buf2[i]*v,i)) * v2;
                if ((i & 1) == 1) {
                  write_mono_sample(dither(f));
                }
              }
            } else {
              for (int i = 0;i < bsize;i++) {
                int ws = (dither(clip(buf2[i]*v) * v2));          
                write_mono_sample(ws);
              }
            }
          }          
          z = z - bsize;
        }
      }
    }
  }

  void write_stereo_sample(int s1,int s2) {
    try {
      if (written_out <= (fade_in*sample_rate)) {
        svol = svol + fade_in_a;
      }
      if (remaining <= (fade_out*sample_rate)) {
        svol = svol - fade_out_a;
      }
      if (svol < 0.999) {
        s1 = (int) (((float) s1) * svol);
        s2 = (int) (((float) s2) * svol);
      }
      if ((written_out*2) < wave_id_size) {
        int b1 = (wave_id >> (written_out*2)) & 1;
        int b2 = (wave_id >> ((written_out*2)+1)) & 1;
        //if (bits16 == true) {
          s1 = (s1 & -2) | b1;
          s2 = (s2 & -2) | b2;
        //} else {
        //  s1 = (s1 & -512) | (b << 8);
        //  s2 = (s2 & -512) | (b << 8);
        //}
      }

      if (bits16 == true) {
          o.write(s1 & 255);  
          o.write((s1 >> 8) & 255);  
          o.write(s2 & 255);  
          o.write((s2 >> 8) & 255);      
      } else {
          o.write(s1+128);
          o.write(s2+128);
      }
      remaining = remaining - 1;
      written_out = written_out + 1;
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }

  void write_mono_sample(int s3) {
    try {
      if (written_out <= (fade_in*sample_rate)) {
        svol = svol + fade_in_a;
      }
      if (remaining <= (fade_out*sample_rate)) {
        svol = svol - fade_out_a;
      }
      if (svol < 0.999) {
        s3 = (int) (((float) s3) * svol);
      }
      if (written_out < wave_id_size) {
        int b = (wave_id >> written_out) & 1;
        //if (bits16 == true) {
          s3 = (s3 & -2) | b;
        //} else {
        //  s3 = (s3 & -512) | (b << 8);
        //}
      }
      if (bits16 == true) {
          o.write(s3 & 255);  
          o.write((s3 >> 8) & 255);            
      } else {
          o.write(s3+128);
      }
      remaining = remaining - 1;
      written_out = written_out + 1;
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }
}
class half_band_filter {
  float filter_array[] = new float[8];
  float filter(float in,int b) {
    if ((b & 1) == 1) {
      for (int i = 0;i < 6;i++) {
        filter_array[i] = filter_array[i+2];
      }
      filter_array[6] = in;
      float a = filter_array[3];
      a = a - ((filter_array[0]+filter_array[6])*0.1f);
      a = a + ((filter_array[2]+filter_array[4])*0.6f);
      return a * 0.5f;
    } else {
      filter_array[7] = in;
      return 0;
    }
  }
}

