import java.lang.*;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;

public class song_playerc {
  Vector player_array;
  patternc pattern;
  int pattern_mode = 0;
  scalec scale;
  double base_freq = 1.0;
  instrumentc ins;
  static int highest_note_freq = 16000;
  song_playerc(instrumentc i) {
    ins = i;
    player_array = new Vector();
    int et = main_app.notes_per_octave;
    scale = new scalec(et);
    scale.init();
    create_players();
  }
  
  void play_pattern(int index) {
        song_list_entryc en = (song_list_entryc) main_app.song_list.get(index);
        patternc p = (patternc) main_app.pattern_list.get(en.pattern);
        if (p == null) {
          p = new patternc(4,en.pattern);
          main_app.pattern_list.put(en.pattern,p);
        }
        pattern = p;
        pattern_mode = en.mode;

        scalec s = (scalec) main_app.tuning_map.get(en.tuning);
        if (s == null) {
          s = new scalec(main_app.notes_per_octave);
          s.init();
          main_app.tuning_map.put(en.tuning,s);
        }
        scale = s;    
        double f = Math.exp(Math.log(2) * (en.cents / (1200.0*65536.0)));
        base_freq = f;

  }
  void create_players() {
    //int n = main_app.notes_per_octave;
    //n = n * main_app.num_octaves;
    int n = main_app.number_of_keys;
    player_array = new Vector();
    alloc_players(n);
    update_players();    
  }
  void alloc_players(int s) {
    for (int i = player_array.size();i < s;i++) {
      player_array.add(new sampleplayerc());
    }
  }
  void update_players_note(int n) {
    int npo = main_app.notes_per_octave;
    //int n2 = npo * main_app.num_octaves;
    int n2 = main_app.number_of_keys;
    int n3 = (npo-n);
    n3 = (n3 + pattern_mode)%npo;
    if (n3 < 0) {n3 = n3 + npo;}
    //if (n3 == npo) {n3 = 0;}
    for (int i = n3;i < player_array.size();i = i + npo) {
      sampleplayerc p = (sampleplayerc) player_array.get(i);
      p.freq = get_note_freq(n2-i);
      p.vol = 0.01; // Math.sqrt(Math.sqrt(Math.sqrt(p.freq/200)));      
      p.fw = get_filter_width(p.freq);
      p.t = get_string_table(p.freq);
    }
  }
  void update_players() {
    System.out.println("update_players");
    int npo = main_app.notes_per_octave;
    //int n = npo * main_app.num_octaves;
    int n = main_app.number_of_keys;
    for (int y = 0;y < player_array.size();y++) {
      sampleplayerc p = (sampleplayerc) player_array.get(y);
      //double f = ((double)(n-i))/npo;
      //p.freq = Math.exp(Math.log(2) * f) * main_app.base_freq;
      //if (pattern.get_cell(x,y) != 0) {
        p.freq = get_note_freq(n-y);
        p.vol = 0.01; // Math.sqrt(Math.sqrt(Math.sqrt(p.freq/200)));
        p.fw = get_filter_width(p.freq);
        p.t = get_string_table(p.freq);
      //}
    }
  }
  public void play_col(int x,int bsize) {
    int n = main_app.number_of_keys;
    for (int y = 0;y < player_array.size();y++) {
      sampleplayerc p = (sampleplayerc) player_array.get(y);
      int a = pattern.get_cell(x,y);
      if (a != 0) { 
          if (p.playing == false) {
            p.note_on(bsize);          
          }
          p.pan = a;
          p.freq = get_note_freq(n-y);
          p.vol = 0.01; // Math.sqrt(Math.sqrt(Math.sqrt(p.freq/200)));
          p.fw = get_filter_width(p.freq);
          p.t = get_string_table(p.freq);
      } else {
          p.note_off(bsize);
      }
    }
  }

  float get_filter_width(double freq) {
    double o = (Math.log(highest_note_freq/freq)/Math.log(2.0));    
    int i = (int) o;
    if (i >= ins.string_table_num_octaves) {
      i = ins.string_table_num_octaves-1;
    }
    return (float) Math.exp(Math.log(2)*-(o-i));
  }
  sampletablec get_string_table(double freq) {
    //int num_octaves = ins.string_table_num_octaves;
    int num_octaves = ins.string_table.length;
    if (freq > highest_note_freq) {return null;}
    int o = (int) (Math.log(highest_note_freq/freq)/Math.log(2.0));        
    if (o >= num_octaves) {
      o = num_octaves-1;
    }
    if (o < 0) {o = 0;}
    //for (int i = 10;i < 20;i++) {
      //System.out.print(ins.string_table[o].sample[i]);
    //}
    //System.out.print(ins.string_table[o].size);
    //System.out.print(" ");
    return ins.string_table[o];
  }
  double get_note_freq(int n) {
    //double octave_cents = (double) main_app.octave_cents;
    n = n + pattern_mode;
    int et =  main_app.notes_per_octave;
    int o = n/et;
    int n2 = (n%et);
    if (n2 < 0) {n2 = n2 + et;}
    double key = scale.get(n);
    //cents = cents + (octave_cents*o);
    double frac = scalec.get_interval_fraction();
    double ed = scalec.equal_divisions;
    double freq = Math.exp(Math.log(frac) * (key/ed));
    freq = freq * base_freq;
    freq = freq * main_app.base_freq;
    return freq;
  }
}

