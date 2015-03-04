import java.lang.*;
import java.util.*;
import java.io.*;

import javax.sound.sampled.*;

public class sampleplayerc {
  long l_freq;
  //long index;
  float fw = 1.0f;
  double indexf;
  double freq;
  double cache_freq;
  double vol2;
  double vol;
  double vol_step;
  sampletablec t;
  static int sample_rate = 44100;
  static int interpolation = 2;
  int pan = 0;
  //static double base_freq = 1.0;
  boolean playing;
  static adsr_envelopec envelope = new adsr_envelopec();

  sampleplayerc() {
  }  
  void note(double freq,double vol,sampletablec t){
    this.freq = freq;
    this.vol = vol;

    this.t = t;
  }
  void note_on(int bsize) {
    //index = 0;
    //System.out.println("freq: " + freq + " ts: " + t.size + " sr: " + t.samplerate);
    int nl = pattern_playerc.get_note_length();    
    int a = (int) (nl*envelope.attack);
    a = a - (a % bsize);
    if (a == 0) {
      vol2 = vol;    
      vol_step = 0.0001;
    } else {
      vol2 = vol * 0.001;
      vol_step = vol / a;    
    }
    playing = true;
  }
  void note_off(int bsize) {
    int nl = pattern_playerc.get_note_length();    
    int a = (int) (nl*envelope.release);
    a = a - (a % bsize);
    if (a == 0) {
      vol2 = 0.0;
      //vol_step = -0.01;
    } else {
      vol_step = (-vol2) / a;
    }
    playing = false;    
  }
  long get_long_freq(double freq,double s_rate) {
    double f = (freq * s_rate) / (1000.0 * sample_rate);  
    return ((long) (f * 1024.0 * 1024.0 * 4096.0));
  }
  float get_float_freq(double freq,double s_rate) {
    float f = (float) ((freq * s_rate) / (1000.0 * sample_rate));  
    return f;    
  }
  void play(float buf[],int buf_size) {
    if (interpolation == 0) {
      play0(buf,buf_size);
    }
    if (interpolation == 1) {
      play1(buf,buf_size);
      //linear_play(buf,buf_size);
    }
    if (interpolation == 2) {
      play2(buf,buf_size);
      //quadratic_play(buf,buf_size);
    }
    if (interpolation == 3) {
      cubic_play(buf,buf_size);
    }
    float sustain = envelope.sustain;
    if ((vol2 >= vol) & (vol_step > 0.0)) {
      int nl = pattern_playerc.get_note_length();    
      int a = (int) (nl*envelope.decay);
      a = a - (a % buf_size);
      if (a == 0) {
        vol2 = vol*sustain;
        vol_step = 0.0;
      } else {
        vol2 = vol2 * 0.999;
        vol_step = ((vol*sustain)-vol2)/a;
      }
    }
    if ((playing == true) & (vol_step < 0.0) & (vol2 <= (vol*sustain))) {
      vol_step = 0.0;
    }
    //System.out.println("vol2: " + vol2 + " vol_step: " + vol_step + " buf_size: " + buf_size);
    if (vol2 <= 0.0) {
      vol2 = 0.0;
      vol_step = 0.0;
    }
    vol2 = vol2 + (vol_step*buf_size);
  }
  void play0(float buf[],int buf_size) {
    if (t == null) {return;}
    if ((playing == false) & (vol2 <= 0.0)) {return;}
    float freq2 = get_float_freq(freq,t.samplerate);
    float f_vol = ((float) vol2);
    float f_vol_inc = ((float) vol_step);
    int bs = buf_size;
    int i2 = 0;    
    for (int i = 0;i < bs;i++) {
      i2 = (int) indexf;
      if (i2 >= t.size) {
        indexf = indexf - t.size;
        i2 = (int) indexf;
      }
      float fs = t.sample[i2];
      buf[i] = buf[i] + (fs * f_vol);
      f_vol = f_vol + f_vol_inc;
      indexf = indexf + freq2;
    }
  }
  void linear_play(float buf[],int buf_size) {
    if (t == null) {return;}
    if ((playing == false) & (vol2 <= 0.0)) {return;}
    double freq2 = get_float_freq(freq,t.samplerate);
    l_freq = get_long_freq(freq,t.samplerate);
    float f_vol = ((float) vol2);
    float f_vol_inc = ((float) vol_step);
    int bs = buf_size;
    int i2 = 0;    
    for (int i = 0;i < bs;i++) {
      i2 = (int) indexf;
      while (i2 >= t.size) {
        indexf = indexf - t.size;
        i2 = (int) indexf;
      }
      float s1 = t.sample[(i2)];
      float s2 = t.sample[(i2+1)];
      float f2 = (float) indexf - i2;
      float f1 = 1.0f - f2;
      float fs = (float) ((s2*f2)+(s1*f1));
      buf[i] = buf[i] + (fs * f_vol);
      f_vol = f_vol + f_vol_inc;
      indexf = indexf + freq2;
    }
  }
  void play1(float buf[],int buf_size) {
    if (t == null) {return;}
    if ((playing == false) & (vol2 <= 0.0)) {return;}
    float iw = 1.0f / fw;
    double freq2 = get_float_freq(freq,t.samplerate);
    l_freq = get_long_freq(freq,t.samplerate);
    float f_vol = ((float) vol2);
    float f_vol_inc = ((float) vol_step);
    int bs = buf_size;
    int i2 = 0;    
    for (int i = 0;i < bs;i++) {
      i2 = (int) indexf;
      while (i2 >= t.size) {
        indexf = indexf - t.size;
        i2 = (int) indexf;
      }
      float s1 = t.sample[(i2)];
      float s2 = t.sample[(i2+1)];
      float f2 = (float) indexf - i2;
      f2 = f2 * iw;
      if (f2 > 1.0f) {f2 = 1.0f;}
      float f1 = 1.0f - f2;
      float fs = (float) ((s2*f2)+(s1*f1));
      buf[i] = buf[i] + (fs * f_vol);
      f_vol = f_vol + f_vol_inc;
      indexf = indexf + freq2;
    }
  }
  void quadratic_play(float buf[],int buf_size) {
    if (t == null) {return;}
    if ((playing == false) & (vol2 <= 0.0)) {return;}
    double freq2 = get_float_freq(freq,t.samplerate);
    float f_vol = ((float) vol2 / 2);
    float f_vol_inc = ((float) vol_step / 2);
    int bs = buf_size;
    int i2 = 0;    
    for (int i = 0;i < bs;i++) {
      i2 = (int) indexf;
      while (i2 >= t.size) {
        indexf = indexf - t.size;
        i2 = (int) indexf;
      }
      float p0 = t.sample[(i2)];
      float p1 = t.sample[(i2+1)];
      float p2 = t.sample[(i2+2)];
      float v = (float) indexf - i2;
      float w2 = 2.0f;
      float b = v*v;
      w2 = w2 - b;
      float s = (b*p2);
      b = ((1-v)*(1-v));
      w2 = w2 - b;
      s = s + (b*p0);        
      s = s + (p1 * w2); 
      buf[i] = buf[i] + (s * f_vol);
      f_vol = f_vol + f_vol_inc;
      indexf = indexf + freq2;
    }
  }

  void play2(float buf[],int buf_size) {
    if (t == null) {return;}
    if ((playing == false) & (vol2 <= 0.0)) {return;}
    float w = fw;
    float iw = 1.0f / fw;
    double freq2 = get_float_freq(freq,t.samplerate);
    //l_freq = get_long_freq(freq,t.samplerate);
    float f_vol = ((float) vol2 / 2);
    float f_vol_inc = ((float) vol_step / 2);
    int bs = buf_size;
    int i2 = 0;    
    for (int i = 0;i < bs;i++) {
      i2 = (int) indexf;
      while (i2 >= t.size) {
        indexf = indexf - t.size;
        i2 = (int) indexf;
      }
      float p0 = t.sample[(i2)];
      float p1 = t.sample[(i2+1)];
      float p2 = t.sample[(i2+2)];
      float v = (float) indexf - i2;
      float s = 0;
      //float w2 = fw;
      float w2 = 2.0f;
      if (v < w) {
        float wv = (w-v) * iw;
        float b = (wv*wv);
        w2 = w2 - b;
        s = s + (b*p0);        
        //float v2 = v * iw;
        //float b = (p1 * v2) + (p0 * (1-v2));
        //s = s + ((b+p1)*(w-v));
        //w2 = w2 + v - w;     
      }
      if (v > (1-w)) {
        float wv = (v-(1-w)) * iw;
        float b = (wv*wv);
        w2 = w2 - b;
        s = s + (b*p2);
        //float v2 = (v-(1-w)) * iw;
        //float b = (p2 * v2) + (p1 * (1-v2));
        //s = s + ((b+p1)*(v-(1-w)));
        //w2 = w2 + (1-w) - v;
      }
      s = s + (p1 * w2); 
      //float fs = (float) ((s2*f2)+(s1*f1));
      buf[i] = buf[i] + (s * f_vol);
      f_vol = f_vol + f_vol_inc;
      indexf = indexf + freq2;
    }
  }

  void cubic_play(float buf[],int buf_size) {
    if (t == null) {return;}
    if ((playing == false) & (vol2 <= 0.0)) {return;}
    
    double freq2 = get_float_freq(freq,t.samplerate);
    l_freq = get_long_freq(freq,t.samplerate);
    float f_vol = ((float) vol2);
    float f_vol_inc = ((float) vol_step);
    int bs = buf_size;
    int i2 = 0;    
    for (int i = 0;i < bs;i++) {
        i2 = (int) indexf;
        while (i2 >= t.size) {
          indexf = indexf - t.size;
          i2 = (int) indexf;
        }
        float v = (float) indexf - i2;
        float p0 = t.sample[i2+0];
        float p1 = t.sample[i2+1];
        float p2 = t.sample[i2+2];
        float p3 = t.sample[i2+3];
        float f0 = p1;
        float f1 = p2;
        float d0 = (p2 - p0) * 0.5f;
        float d1 = (p3 - p1) * 0.5f;    

        float a = (f0*2) - (f1*2) + d0 + d1;
        float b = (f1*3) - (f0*3) - (d0*2) - d1;
        float c = d0;
        float d = f0;
        float s = (((((a*v)+b)*v)+c)*v)+d;
        buf[i] = buf[i] + (s * f_vol);
        f_vol = f_vol + f_vol_inc;
        indexf = indexf + freq2;
    }
  }

  void play3(float buf[],int buf_size) {
    if (t == null) {return;}
    if ((playing == false) & (vol2 <= 0.0)) {return;}
    
    double freq2 = get_float_freq(freq,t.samplerate);
    l_freq = get_long_freq(freq,t.samplerate);
    float f_vol = ((float) vol2);
    float f_vol_inc = ((float) vol_step);
    int bs = buf_size;
    int i2 = 0;    
    if (fw < 0.5) {
      float w = fw * 2;
      float iw = 1.0f / w;
      for (int i = 0;i < bs;i++) {
        i2 = (int) indexf;
        while (i2 >= t.size) {
          indexf = indexf - t.size;
          i2 = (int) indexf;
        }
        float f0 = t.sample[i2+1];
        float f1 = t.sample[i2+2];
        float v = (float) indexf - i2;
        float s = f1;
        if (v < w) {
          float v2 = v * iw;
          float a = (f0*2) - (f1*2);
          float b = (f1*3) - (f0*3);
          s = (((a*v2)+b)*v2*v2)+f0;
        } 
        buf[i] = buf[i] + (s * f_vol);
        f_vol = f_vol + f_vol_inc;
        indexf = indexf + freq2;
      }
    } else {
      float w = (fw * 2)-1;

      for (int i = 0;i < bs;i++) {
        i2 = (int) indexf;
        while (i2 >= t.size) {
          indexf = indexf - t.size;
          i2 = (int) indexf;
        }
        float v = (float) indexf - i2;
        float p0 = t.sample[i2+0];
        float p1 = t.sample[i2+1];
        float p2 = t.sample[i2+2];
        float p3 = t.sample[i2+3];
        float f0 = p1;
        float f1 = p2;
        float d0 = (p2 - p0) * 0.5f;
        float d1 = (p3 - p1) * 0.5f;    
        d0 = d0 * 1.5708f * w;
        d1 = d1 * 1.5708f * w;
        f0 = f0 + (p0 * w * 0.25f);
        f0 = f0 + (p2 * w * 0.25f);
        f1 = f1 + (p1 * w * 0.25f);
        f1 = f1 + (p3 * w * 0.25f);
        float a = (f0*2) - (f1*2) + d0 + d1;
        float b = (f1*3) - (f0*3) - (d0*2) - d1;
        float c = d0;
        float d = f0;
        float s = (((((a*v)+b)*v)+c)*v)+d;
        buf[i] = buf[i] + (s * f_vol);
        f_vol = f_vol + f_vol_inc;
        indexf = indexf + freq2;
      }
    }
  }
} 
