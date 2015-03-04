import java.lang.*;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;



public class sampletablec {
  byte sample[]; 
  int size;
  double samplerate;
  //static int wave_data_size = 0;
  //static float FTT_data[][];
  int id;
  static float filter45[];// = {0,4,0};
  static float filter45_sum;
  static int filter45_size = 0;
  int bits;
  int channels;

  sampletablec(int size,double samplerate) {
    if (filter45_size == 0) {init_filter45();}    
    this.size = size;
    this.samplerate = samplerate;
    sample = new byte[size+3];
  }  
  sampletablec(String filename) throws IOException {
    if (filter45_size == 0) {init_filter45();}    
    DataInputStream i = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
    read_header(i);
    read_wave_data(i);
    i.close();

  }
  static void init_filter45() {
    float w = 0.035f;
    int s = 5;
    filter45_size = s;
    filter45 = new float[s];
    for (int i = 0;i < s;i++) {
      float a = (i*2)+1;
      float b = (float)Math.exp(-a*a*w)/a;
      if ((i & 1) == 1) {b = -b;}
      filter45[i] = b;
      filter45_sum = filter45_sum + (b*2);
    }
  }
  static int swapbytesofword(int n) {
    return wave_writerc.swapbytesofword(n);
  }
  static int swapbytesofint(int n) {
    return wave_writerc.swapbytesofint(n);
  }

  int read_header(DataInputStream i) throws IOException {
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
      channels = a;
      samplerate = swapbytesofint(i.readInt());
      i.readInt();
      a = swapbytesofword(i.readShort());
      System.out.println(a);
      a = swapbytesofword(i.readShort());//bits
      if ((a != 8) & (a != 16)) {throw new IOException("invalid number of bits");}
      System.out.println("bits: " + a);
      bits = a;
      int data = 0; 
      while (true) {
        data = i.readInt();
        System.out.println("data: " + data);
        size = swapbytesofint(i.readInt());
        System.out.println("size: " + size);
        if (data == 1684108385) {break;}
        for (int j = 0;j < size;j++) {
          i.readByte();
        }
      }
      size = (size / (channels * (bits >> 3)));
      //if (bits == 8) {
        sample = new byte[size+3];
      //} else {
      //  sample16 = new short[size*2];
      //}
      //System.out.println("f size: " + size);
    return 1;
  }
  int read_wave_data(DataInputStream in) throws IOException {
    //System.out.println("aa");
    if (bits == 8) {
      for (int i = 0;i < size;i++) {
        int s = 0;
        for (int ch = 0;ch < channels;ch++) {
          s = s + (in.readUnsignedByte()-128);
        }
        sample[i] = (byte) (s / channels);
      }
    } else {
      for (int i = 0;i < size;i++) {
        float s = 0;
        for (int ch = 0;ch < channels;ch++) {
          for (int j = 16;j < bits;j = j + 8) {
            in.readUnsignedByte();
          }
          s = s + in.readUnsignedByte();
          s = s + (in.readByte() << 8);
        } 
        //sample16[i] = (short) (s / channels);
        sample[i] = (byte) dither(s / (channels*256.0f));
      }
    }
    sample[size+0] = sample[0];
    sample[size+1] = sample[1];
    sample[size+2] = sample[2];
    //sample[size+3] = sample[3];
    //System.out.println("ab");
    return 1;
  }
  static short dither(double s) {
    double sf = Math.floor(s);
    double e = s - sf;
    if (e > Math.random()) {  
      return (short) (sf+1);
    } else {
      return (short) (sf);
    }
  }
  sampletablec down_sample() { 
    sampletablec t = new sampletablec((size+1) >> 1,samplerate / 2);
    System.out.println("s size: " + size);
    for (int i = 0;i < size;i = i + 2) {
      float sum = sample[i] * filter45_sum;
      for (int j = 1;j < (filter45_size*2);j = j + 2) {
        float a = 0.0f;
        int os = 0;
        while ((i-j+os) < 0) {os = os + size;}
        a = a + sample[i-j+os];
        os = 0;
        while ((i+j+os) >= size) {os = os - size;}
        a = a + sample[i+j+os];
        sum = sum + (a * filter45[j >> 1]);
      }
      t.sample[(i>>1)] = (byte) dither(sum / (filter45_sum*2));
    }
    int ts = t.size;
    t.sample[ts+0] = t.sample[0];
    t.sample[ts+1] = t.sample[1];
    t.sample[ts+2] = t.sample[2];

    return t;
  }
  void output_to_file(String filename) {
    try {
      DataOutputStream outfile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename,true)));
      for (int i = 0;i < size;i++) {
        byte b = sample[i];
        outfile.writeByte(b);
      }
      outfile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  void make_instrument_sample(float FFT_data[][],float freq,float bandwidth2,Vector harmonic_list,Random rand) {
    int num_harmonics = harmonic_list.size();
    int FFT_data_size = FFT_data.length;
    for (int i = 0;i < num_harmonics;i++) {
      Harmonic h = (Harmonic) harmonic_list.get(i);
      float freq2 = freq * h.freq;
      float bandwidth = h.bandwidth * bandwidth2;
      //System.out.println("bandwidth " + bandwidth);
      //System.out.println("h.vol " + h.vol);
      if (freq2 >= (FFT_data_size+bandwidth)) {continue;}
      if (freq2 <= bandwidth) {continue;}
      int start_freq = ((int) (freq2-bandwidth+1.0));
      int end_freq = ((int) (freq2+bandwidth));
      float e_vol = 0;
      for (int i2 = start_freq;i2 <= end_freq;i2++) {        
        float e = (((float) i2)-freq2);
        if (e < 0) {e = -e;} 
        e = 1.0f - (e / bandwidth);   
        e_vol = e_vol + (e * e);
      }
      for (int i2 = start_freq;i2 <= end_freq;i2++) {        
        float e = (((float) i2)-freq2);
        if (e < 0) {e = -e;} 
        e = 1.0f - (e / bandwidth);   
        if (e < 0) {e = 0.0f;}
        //e = (e * 1.0f); 
        float am = h.vol / ((float) Math.sqrt(e_vol));
        float p = (float) (rand.nextFloat()*Math.PI*2.0);
        if (i2 < FFT_data_size) {
          FFT_data[i2][0] = ((float) (Math.cos(p) * e * am));
          FFT_data[i2][1] = ((float) (Math.sin(p) * e * am));
        }
      }
      if (start_freq > end_freq) {
        int i2 = (int) (freq2 + 0.5);
        float p = (float) (rand.nextFloat()*Math.PI*2.0);
        if (i2 < FFT_data_size) {
          FFT_data[i2][0] = ((float) (Math.cos(p) * h.vol));
          FFT_data[i2][1] = ((float) (Math.sin(p) * h.vol));
        }
      }
    }
    //System.out.println("FFT_data34 " + FFT_data[FFT_data_size-100][0]);
  }
    
  void get_instrument_sample(float FFT_data[][]) {
    //int clipping = 0;
    double b = 0;
    for (int i = 0;i < size;i++) {
      float a = FFT_data[i >> 1][i & 1];
      if (a < 0) {a = -a;}
      if (a > b) {b = a;}
    }
    for (int i = 0;i < size;i++) {
      double s = (FFT_data[i >> 1][i & 1] / b) * 126.0;
      //if (s > 127.0) {s = 127.0;clipping++;}
      //if (s < -127.0) {s = -127.0;clipping++;}
      sample[i] = (byte) s;
    }
    //System.out.println("clipping: " + clipping);
    for (int i = 0;i < 3;i++) {
    //sample[size] = sample[0]; 
      sample[size+i] = sample[i]; 
    }
  }
}



