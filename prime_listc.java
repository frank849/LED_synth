import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.event.*; 
import javax.swing.*;   
import javax.swing.table.*;
      
import java.awt.*;
import java.awt.event.*;                       
import java.awt.datatransfer.*;

import javax.imageio.*;
import java.awt.image.*;
import java.util.zip.*;

public class prime_listc {
  float prime_factor[];
  short prime_factor_DB[];
  float prime_factor_bw[];
  int num_primes;
  int num_alloc_primes;
  prime_listc(int n) {
    num_primes = n;
    int n2 = n;
    if (n == 0) {n2 = 1;}
    num_alloc_primes = n2;
    prime_factor = new float[n2];
    prime_factor_DB = new short[n2];
    prime_factor_bw = new float[n2];
    //int nl = main_app.primes_table75.length;
    for (int i = 0;i < n;i++) {
      //prime_factor[i] = (float) get_prime_integer_factor(i);
      int p = main_app.primes_table75[i];
      write_prime_factor(i,p);
      //System.out.println(prime_factor[i]);
    }
  }
  boolean is_prime_factor_integer(int i) {
    int p = (int) prime_factor[i];
    if ((prime_factor[i]-p) == 0.0) {return true;}
    return false;
  }
  void read_list(DataInputStream infile) throws IOException {
    int m = infile.readUnsignedShort();
    for (int i = 0;i < num_primes;i++) {
        if ((m & 1) == 1) {
          int p = infile.readUnsignedByte();
          if (p == 255) {
            prime_factor[i] = infile.readFloat();
          } else {
            prime_factor[i] = get_prime_integer_factor(p);
          }
        } else { 
          prime_factor[i] = infile.readFloat();
        }      
        prime_factor_DB[i] = (short) (infile.readUnsignedByte()-256);
        if ((m & 2) == 2) {
          int p = infile.readUnsignedByte();
          if (p == 255) {
            prime_factor_bw[i] = infile.readFloat();
          } else {
            prime_factor_bw[i] = get_prime_integer_factor(p);
          }
        } else { 
          prime_factor_bw[i] = infile.readFloat();     
        }
    }
  }
  void write_list(DataOutputStream outfile) throws IOException {
    outfile.writeShort(0);
    for (int i = 0;i < num_primes;i++) {
      outfile.writeFloat(prime_factor[i]);
      outfile.writeByte(prime_factor_DB[i]);
      outfile.writeFloat(prime_factor_bw[i]);      
    }    
  }
  void write_list532(DataOutputStream outfile) throws IOException {
    outfile.writeShort(3);
    int t[] = {5,3,2};
    for (int i = 0;i < t.length;i++) {
      outfile.writeByte(t[i]);
      outfile.writeByte(prime_factor_DB[i]);
      outfile.writeByte(t[i]);
    }
    for (int i = t.length;i < num_primes;i++) {
      outfile.writeByte(255);
      outfile.writeFloat(prime_factor[i]);
      outfile.writeByte(prime_factor_DB[i]);
      outfile.writeByte(255);
      outfile.writeFloat(prime_factor_bw[i]);      
    }    
    
  }
  void write_prime_factor(int i, float p) {
      prime_factor[i] = p;
      prime_factor_DB[i] = (short) (((Math.log(p)*-40) / Math.log(10))-0.5);
      prime_factor_bw[i] = p;
  }
  void add_prime() {
    if (num_primes >= num_alloc_primes) {
      num_alloc_primes = num_alloc_primes * 2;
      float new_array[] = new float[num_alloc_primes];
      short new_array_DB[] = new short[num_alloc_primes];
      float new_array_bw[] = new float[num_alloc_primes];
      for (int i = 0;i < num_primes;i++) {
        new_array[i] = prime_factor[i];
        new_array_DB[i] = prime_factor_DB[i];
        new_array_bw[i] = prime_factor_bw[i];
      }
      prime_factor = new_array;
      prime_factor_DB = new_array_DB;
      prime_factor_bw = new_array_bw;
    }
    //float p = (float) get_prime_integer_factor(num_primes);
    float p = main_app.primes_table75[num_primes];
    int np = num_primes;
    write_prime_factor(np,p);
    num_primes = num_primes + 1;
  }
  void gen_harmonics(Vector harmonic_list,float freq,float vol,float bw,int index,float limit) {
    harmonic_list.add(new Harmonic(freq,vol,bw));
    while (index >= 0) {
      float f = freq * prime_factor[index];
      float db = prime_factor_DB[index];
      float v = (float) (vol * Math.exp(Math.log(10) * (db / 40.0)));
      float bw2 = bw * prime_factor_bw[index];
      if (f <= limit) {
        gen_harmonics(harmonic_list,f,v,bw2,index,limit);
      }
      index = index - 1;
    }
  }
  float get_harmonic_freq(int h) {
    float freq = 1.0f;
    if (h == 0) {return 0.0f;}
    for (int i = 0;i < 4;i++) {
      int f = get_prime_integer_factor(i);
      while ((h % f) == 0) {
        h = h / f;
        freq = freq * prime_factor[i];
      }
    }
    if (h == 1) {return freq;}
    int i = get_prime_index(h);
    if (i >= num_primes) {return 0.0f;}
    return freq * prime_factor[i];
  }
  static int get_prime_integer_factor(int i) {    
    if (i == 0) {return 2;}
    if (i == 1) {return 3;}
    int i2 = (i / 2);
    int i3 = (i % 2);    
    return ((i2*6)+(i3*2)-1);
  }
  static int get_prime_index(int p) {
    int a = ((p + 3) / 6);
    if (p == 2) {return 0;}
    if (p == 3) {return 1;}
    if ((p % 6) == 1) {return (a*2)+1;}
    if ((p % 6) == 5) {return (a*2);}
    return -1;
  }

}

