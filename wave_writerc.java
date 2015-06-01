import java.lang.*;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;

public class wave_writerc {
  DataOutputStream o;  
  boolean stereo = true;
  boolean bits16 = true;
  int written_out = 0;
  int remaining = 0;
  float fade_in;
  float fade_in_a;
  float fade_out;
  float fade_out_a;
  double svol;
  int sample_rate;
  int wave_size;
  wave_writerc(String outfile,boolean st,boolean sh,float fi,float fo) {
    try {
      stereo = st;
      bits16 = sh;
      o = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile,false)));
      int sls = main_app.song_list.size();
      int songlen = 0;
      for (int i = 0;i < sls;i++) {
         song_list_entryc e = (song_list_entryc) main_app.song_list.get(i);
         patternc p = (patternc) main_app.pattern_list.get(e.pattern);
         songlen = songlen + p.get_length();
      }
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
  static int wave_id[] = {1,8,9,8,10,4,13,11,
                          5,12,6,7,3,9,7,8,
                          2,0,14,13,6,15,10,14,
                          10,3,10,12,0,10,2,6};
  static boolean read_wave_id(File f) {
    try {
      if (f.exists() == false){ return true;}      

      DataInputStream i = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
      //for (int i2 = 0;i2 < wave_id_header_size;i2++) {
      //  i.read();
      //}
      check_header(i);
      for (int i2 = 0;i2 < wave_id_size;i2++) {
        int b = i.read();
        if (wave_id[i2] != (b & 127)) {
          i.close();
          return false;
        }
      }
      i.close();

    } catch (Exception e) {
      e.printStackTrace();            
    }
    return true;
  }
  void write_wave_id(boolean bits16) {
    try {
      for (int i = 0;i < wave_id_size;i++) {
        if (bits16 == true) {
          o.write(wave_id[i]);
        } else {
          o.write(wave_id[i] ^ 128);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();            
    }
  }  
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
      write_wave_id(bits16);
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
      o.writeInt(swapbytesofint(sample_rate));
      int b = 1;
      if (stereo == true) {b = b * 2;}
      if (bits16 == true) {b = b * 2;}
      
      o.writeInt(swapbytesofint(sample_rate*b));
      o.writeShort(swapbytesofword(b));
      if (bits16 == true){
        o.writeShort(swapbytesofword(16));
      } else {
        o.writeShort(swapbytesofword(8));
      }
      o.writeBytes("data");
      o.writeInt(swapbytesofint((wave_size*b)+wave_id_size));
      write_wave_id(bits16);
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }  
  static int check_header(DataInputStream i) {
    try {
      int RIFF = i.readInt();
      System.out.println("RIFF: " + RIFF);
      i.readInt();
      int WAVE = i.readInt();
      System.out.println("WAVE: " + WAVE);
      int fmt = i.readInt();
      System.out.println("fmt : " + fmt);
      int a = 0;
      a = swapbytesofint(i.readInt());
      System.out.println(a);
      a = swapbytesofword(i.readShort());
      System.out.println(a);
      a = swapbytesofword(i.readShort());//channels
      System.out.println("channels: " + a);
      i.readInt();
      i.readInt();
      a = swapbytesofword(i.readShort());
      System.out.println(a);
      a = swapbytesofword(i.readShort());//bits
      System.out.println("bits: " + a);
      int data = i.readInt();
      System.out.println("data: " + data);
      i.readInt();
    } catch (Exception e) {
      e.printStackTrace();      
    }
    return 1;
  }
  static double clip(double a) {
    return pattern_playerc.clip(a);
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
          for (int i = 0;i < bsize;i++) {
            buf1[i] = buf1[i] + buf2[i];
            buf3[i] = buf3[i] + buf2[i];
            //if (bass > 0) {
            //  filter1 = filter1+((buf1[i]-filter1)*0.01f);
            //  filter3 = filter3+((buf3[i]-filter3)*0.01f);
            //  buf1[i] = buf1[i] + ((filter1*bass));
            //  buf3[i] = buf3[i] + ((filter3*bass));
            //}
          }
          //for (int i = 0;i < bsize;i++) {buf2[i] = buf1[i];}
          equalizer.e1.filter(buf1,bsize);
          //for (int i = 0;i < bsize;i++) {buf1[i] = buf2[i];}

          //for (int i = 0;i < bsize;i++) {buf2[i] = buf3[i];}        
          equalizer.e3.filter(buf3,bsize);
          //for (int i = 0;i < bsize;i++) {buf3[i] = buf2[i];}
          for (int i = 0;i < bsize;i++) {
            //double v = ((double) volume) * 0.01;
            double v = (float) (Math.exp(Math.log(10.0)*(volume/20.0f)));
            v = v * 0.001;
            int ws1 = ((int) (clip(buf1[i]*v) * 32500.0));          
            int ws2 = ((int) (clip(buf3[i]*v) * 32500.0));          
            write_sample(ws1,ws2);
          }          
          z = z - bsize;
        }
      }
    }
  }

  void write_sample(int s1,int s2) {
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
      if (stereo == true) {
        if (bits16 == true) {
          o.write(s1 & 255);  
          o.write((s1 >> 8) & 255);  
          o.write(s2 & 255);  
          o.write((s2 >> 8) & 255);      
        } else {
          o.write((s1 >> 8)+128);
          o.write((s2 >> 8)+128);
        }
      } else {
        int s3 = (s1+s2) >> 1;
        if (bits16 == true) {
          o.write(s3 & 255);  
          o.write((s3 >> 8) & 255);            
        } else {
          o.write((s3 >> 8)+128);
        }
      }
      remaining = remaining - 1;
      written_out = written_out + 1;
    } catch (Exception e) {
      e.printStackTrace();      
    }
  }
}

