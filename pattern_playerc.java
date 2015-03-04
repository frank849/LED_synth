import java.lang.*;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;

public class pattern_playerc extends Thread {
  SourceDataLine output_line;
  int volume = 1;
  int bass = 0;
  int pos = 0;
  instrumentc ins;
  boolean paused = true;
  song_playerc song_player;
  static float lowest_harmonic4[];
  static int sample_rate = sampleplayerc.sample_rate;
  pattern_playerc(song_playerc sp) {
    //alloc_string_tables(6);
    song_player = sp;
    ins = sp.ins;
    output_line = createline((int) sampleplayerc.sample_rate);
  }

  

  public SourceDataLine createline(int sample_rate) {
    AudioFormat audioFormat = new AudioFormat((float) sample_rate,16,2,true,true);
    SourceDataLine	line = null;
    DataLine.Info	info = new DataLine.Info(SourceDataLine.class,audioFormat,8192*4);
    try {
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(audioFormat);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    equalizer.sample_rate_changed();
    line.start();
    return line;	  
  }
  static int numcliping = 0;
  static int numclipsamples = 0;
  public static double clip(double f0) {
    double f1 = f0;
      if (f1 > 1.0) {
        f1 = 1.0;
        numcliping++;
        //System.out.println("clipping");
      }
      if (f1 < -1.0) {
        f1 = -1.0;
        numcliping++;
        //System.out.println("clipping");
      }
      numclipsamples++;
      if ((numclipsamples >= 10000)) {
        if (numcliping > 0) {
	  //System.out.println("numcliping: " + numcliping);
        }
        numclipsamples = 0;
        numcliping = 0;
      }
      return f1;
  }
  static int get_note_length() {
    float bpm = main_app.tempo;
    long spm = (long) (sampleplayerc.sample_rate*60.0);
    int nl = (int) (spm/bpm);
    //nl = nl - (nl & 15);
    return nl;
  }
  static public void play_col(song_playerc sp,int x,int bsize) {
    //int n = main_app.notes_per_octave;
    //n = n * main_app.num_octaves;
    for (int y = 0;y < sp.player_array.size();y++) {
      sampleplayerc p = (sampleplayerc) sp.player_array.get(y);
      int a = sp.pattern.get_cell(x,y);
      if (a != 0) { 
          if (p.playing == false) {
            p.note_on(bsize);
          }
          p.pan = a;
      } else {
          p.note_off(bsize);
      }
    }
  }
  public void run() {
    int bsize = 128;
    //int bsize2 = bsize;
    float buf1[] = new float[bsize];
    float buf2[] = new float[bsize];
    float buf3[] = new float[bsize];
    byte abData[] = new byte[bsize*4];
    int x = 0;
    float filter1 = 1.0f;
    float filter1i = 0.0f;
    float filter3 = 0.0f;
    float filter3i = 0.0f;
    float cos45 = (float) Math.cos((Math.PI * 2 * 5000) / sampleplayerc.sample_rate);
    float sin45 = (float) Math.sin((Math.PI * 2 * 5000) / sampleplayerc.sample_rate);
    float am435 = 0;
    int i2 = 0;
    while (true) {
      patternc pattern = song_player.pattern;
      Vector player_array = song_player.player_array;
      float drum_beat = 20.0f;
      int nl = get_note_length();
      x = pos;
      //System.out.println("am: " + am435);
      //am435 = 0;
      play_col(song_player,pos,bsize);
      //for (int i2 = 0;i2 <= (nl / bsize);i2++) {
      //System.out.println(" play_col " + pos);
      i2 = i2 + nl;
      while (i2 >= bsize) {
        i2 = i2 - bsize;
        if (bsize == 0) {continue;}
        for (int i = 0;i < bsize;i++) {
          buf1[i] = 0.0f;
          buf2[i] = 0.0f;
          buf3[i] = 0.0f;
        } 
        for (int y = 0;y < player_array.size();y++) {
          sampleplayerc p = (sampleplayerc) player_array.get(y);
          if (p.pan == 1) {p.play(buf1,bsize);}
          if (p.pan == 2) {p.play(buf2,bsize);}
          if (p.pan == 3) {p.play(buf3,bsize);}
        }
        
        for (int i = 0;i < bsize;i++) {

          //if (drum_beat > 0.0) {
          //  drum_beat = drum_beat - 0.01f;
          //  buf2[i] = buf2[i] + ((float)(Math.sin(drum_beat*drum_beat)*10.0f));
          //}
          filter3 = (filter1*cos45)-(filter1i*sin45);
          filter3i = (filter1i*cos45)+(filter1*sin45);
          filter1 = filter3;
          filter1i = filter3i;
          //System.out.println("filter1 " + filter1);
          //buf2[i] = filter1; 
          buf1[i] = (buf1[i] + buf2[i]);
          buf3[i] = (buf3[i] + buf2[i]);
          //filter3 = (buf1[i]*filter1)-(buf3[i]*filter1i);
          //filter3i = (buf3[i]*filter1)+(buf1[i]*filter1i);
          //buf1[i] = filter3;
          //buf3[i] = filter3i;
        }
        //for (int i = 0;i < bsize;i++) {buf2[i] = buf1[i];}
        equalizer.e1.filter(buf1,bsize);
        //for (int i = 0;i < bsize;i++) {buf1[i] = buf2[i];}

        //for (int i = 0;i < bsize;i++) {buf2[i] = buf3[i];}        
        equalizer.e3.filter(buf3,bsize);
        //for (int i = 0;i < bsize;i++) {buf3[i] = buf2[i];}
        while (paused == true) {
          try {
            sleep(1);
          } catch (java.lang.InterruptedException e) {}
        }        
        if (sample_rate != sampleplayerc.sample_rate) {
          output_line.close();
          sampleplayerc.sample_rate = sample_rate;
          output_line = createline(sampleplayerc.sample_rate);
        }
        //for (int i = 0;i < bsize;i++) {
        //  am435 = am435 + (buf1[i]*buf1[i]);
        //}
	for (int i = 0;i < bsize;i++) {
          double v = (float) (Math.exp(Math.log(10.0)*(volume/20.0f)));
          v = v * 0.001;
          //double v = ((double) volume) * 0.01;
          int ws = ((int) (clip(buf1[i]*v) * 32500.0));          
          abData[(i*4)+3] = (byte) (ws & 255);
          abData[(i*4)+2] = (byte) (ws >> 8);	
          ws = ((int) (clip(buf3[i]*v) * 32500.0));	  
          abData[(i*4)+1] = (byte) (ws & 255);
          abData[(i*4)+0] = (byte) (ws >> 8);        
        }
        int wb = output_line.write(abData, 0, bsize* 4);      
        //System.out.print(wb);
        //System.out.print(" ");

        //while (paused == true) {
        //  try {
        //    sleep(1);
        //  } catch (java.lang.InterruptedException e) {}
        //}
        //int wb = output_line.getBufferSize();
        //wb = wb-output_line.available();
        //while ((paused == true) | (wb >= 12000)){
        //  try {
        //    sleep(1);
        //  } catch (java.lang.InterruptedException e) {}
        //  wb = output_line.getBufferSize();
        //  wb = wb-output_line.available();
        //}
      }
      if (main_app.play_mode != main_app.NOTE_MODE) {      
        main_app.main_panel.update_pos_line(pos,null,0);
        pos = pos + 1;
      }
      if (pos >= pattern.length) {
        pos = pos % pattern.length;
        if (main_app.play_mode == main_app.SONG_MODE) {
          main_app.pattern_list_window.play_next_pattern();
        }
      }
      if (main_app.play_mode != main_app.NOTE_MODE) {      
        main_app.main_panel.update_pos_line(pos,null,1);
        main_app.hex_keyboard_panel.repaint();
      }
      
    }
  }
}

