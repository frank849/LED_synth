import java.lang.*;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;

public class instrumentc {
  sampletablec string_table[];
  int string_table_num_octaves = 8;
  int string_table_psize = 20;
  int string_table_seed = 0;
  float bandwidth = 20.0f;
  Random rand;

  instrumentc() {
    rand = new Random();
    alloc_string_tables();
  }
  instrumentc(String filename) throws IOException {
    load_instrument_wave(filename);

  }
  void load_instrument_wave(String filename) throws IOException {
    sampletablec t = new sampletablec(filename);
    int s = t.size;
    int o = 0;
      System.out.println("t size: " + s);
    while ((s & 1) == 0) {
      s = s >> 1;
      o = o + 1;
    }
    string_table = new sampletablec[o+1];
    string_table[o] = t;
    for (int i = o;i > 0;i--) {
      string_table[i-1] = string_table[i];//.down_sample();
    }
  }
  void alloc_string_tables() {
    int num_octaves = string_table_num_octaves;
    int ps = string_table_psize;
    string_table = new sampletablec[num_octaves];
    //string_table[0] = new sampletablec(1 << 14,4000.0);
    //string_table[1] = new sampletablec(1 << 14,8000.0);
    //string_table[2] = new sampletablec(1 << 15,16000.0);
    for (int i = 0;i < num_octaves;i++) {
      //int s = 1 << (((i >> 1)+18+ps)-((num_octaves-1) >> 1));
      int s = 1 << ((i+18+ps-num_octaves)>>1);
      float f = 2000.0f * (1 << i);
      string_table[i] = new sampletablec(s,f);    
      string_table[i].id = i;
    }
    int num_primes = main_app.prime_list.num_primes;
    Vector harmonic_list = new Vector();
    //harmonic_list.add(new Float(1.0));
    main_app.prime_list.gen_harmonics(harmonic_list,1.0f,1.0f,1.0f,num_primes-1,256.0f);

    //float harmonic[] = new float[harmonic_list.size()+1];
    //for (int i = 1;i <= harmonic_list.size();i++) {
    //  Number n = (Number) harmonic_list.get(i-1);
    //  harmonic[i] = n.floatValue();
    //}
    for (int i = 0;i < num_octaves;i++) {
      
      //sampletablec.wave_data_size = string_table[i].size;
      //sampletablec.FTT_data = new float[sampletablec.wave_data_size][2];
      float FFT_data[][] = new float[string_table[i].size >> 1][2];

      float f = string_table[i].size >> (i+1);
      float b = (string_table[i].size >> (i+1))*(bandwidth*0.001f);
      //float f = (float) ((1 << 12) / (1 << ((i+1) >> 1)));
      //float b =  100.0f / (float) (1 << ((i+1) >> 1));
      float svol = (float) Math.exp(Math.log(1.2)*(i+1-num_octaves));
      string_table[i].make_instrument_sample(FFT_data,f,b,harmonic_list,rand);         
      FFT_Transformc.perform_c2r(FFT_data);
      string_table[i].get_instrument_sample(FFT_data);
    }  
    //sampletablec.wave_data_size = 0;
    //sampletablec.FTT_data = null;
  
  }
}

