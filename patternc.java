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

public class patternc {
  int length;
  int id;
  String name;
  String image = null;
  byte scale[];
  int alloc_length;
  byte data[][];
  static int black_notes_per_octave = 5;
  patternc(int l) {
    length = l;    
    alloc_length = l;
    int notes_per_octave = main_app.notes_per_octave;
    //int n2 = notes_per_octave*main_app.num_octaves;
    int n2 = main_app.number_of_keys;
    data = new byte[l][n2+1];  
    scale = new byte[(notes_per_octave>>3)+1];
    setup_scale(notes_per_octave);
  }
  patternc(int l,String n) {
    length = l;    
    alloc_length = l;
    name = n;
    int notes_per_octave = main_app.notes_per_octave;
    //int n2 = notes_per_octave*main_app.num_octaves;
    int n2 = main_app.number_of_keys;
    data = new byte[l][n2+1];
    scale = new byte[(notes_per_octave>>3)+1];
    setup_scale(notes_per_octave);
  }
  void copy_scale(patternc p) {
    int notes_per_octave = main_app.notes_per_octave;
    for (int i = 0;i < ((notes_per_octave>>3)+1);i++) {
      scale[i] = p.scale[i];
    }
  }
  void setup_scale(int et) {
    int bn = black_notes_per_octave;
    for (long j = 0;j < bn;j++) {
      int i = (int) ((j*et)/bn);
      set_scale(i,1);
    }
  }
  void setup_scale_old(int et) {
      int i = 1;
      //int f23 = main_app.get_3fifth();
      int f23 = (int) (scalec.generator + 0.5);

      while (get_scale(i) == 0) {
        set_scale(i,1);
	i = (i + f23) % et;
        if (get_scale((i+1)%et) == 1) {break;}
        if (get_scale((i+et-1)%et) == 1) {break;}      
      }  
  }
  int get_scale(int i) {
    return (scale[i >> 3] >> (i & 7))&1;
  }
  void set_scale(int i,int b) {
    int a = scale[i >> 3];
    if (b == 1) {
      scale[i >> 3] = (byte) (a | (1 << (i & 7)));
    } else {
      scale[i >> 3] = (byte) (a & ~(1 << (i & 7)));
    }
  }

  String write_to_string() {
    StringWriter strbuf = new StringWriter();
    //int n2 = main_app.notes_per_octave*main_app.num_octaves;
    int n2 = main_app.number_of_keys;
    PrintWriter p = new PrintWriter(strbuf);
    p.println("pattern422195607171492");
    p.println(name);
    p.println(length);
    p.println(n2+1);
    for (int y = 0;y <= n2;y++) {
      for (int x = 0;x < length;x++) {
        int a = get_cell(x,y);
        if (a == 0) {p.print(".");}
        if (a == 1) {p.print("l");}
        if (a == 2) {p.print("c");}
        if (a == 3) {p.print("r");}
      }
      p.println();
    }
    return strbuf.toString();
  }
  static patternc read_pattern_from_string(String str){
    BufferedReader r = new BufferedReader(new StringReader(str));
    //int n2 = main_app.notes_per_octave*main_app.num_octaves;
    int n2 = main_app.number_of_keys;
    patternc pat = null;
    try {
      String id = r.readLine();
      if (id != null) {
        if (id.equals("pattern422195607171492")) {
          String name = r.readLine();
          int width  = Integer.valueOf(r.readLine()).intValue();
          int height = Integer.valueOf(r.readLine()).intValue();
          pat = new patternc(width,name);
          int y2 = 0;
          for (int y = 0;y < height;y++) {
            String data = r.readLine();
            int e = 1;
            if (y2 > n2) {break;}
            for (int x = 0;x < width;x++) {
              char ch = data.charAt(x);
              if (ch == 'l') {pat.set_cell(x,y2,1);e=0;}
              if (ch == 'c') {pat.set_cell(x,y2,2);e=0;}
              if (ch == 'r') {pat.set_cell(x,y2,3);e=0;}
            }
            if ((e == 0) | (y2 > 0) | ((height-y) > n2)) {
              y2 = y2 + 1;
            }
          }
        }
      }
    } catch(IOException e){
      e.printStackTrace();
    }

    return pat;
  }
  int get_length() {
    return length;
  }
  
  void double_length() {
    //int n2 = main_app.notes_per_octave*main_app.num_octaves;
    int n2 = main_app.number_of_keys;
    byte new_data[][] = new byte[length*2][n2+1];
    for (int y = 0;y <= n2;y++) {
      for (int x = 0;x < length;x++) {
        new_data[x*2][y] = data[x][y];
        new_data[(x*2)+1][y] = data[x][y];
      }
    }  
    length = length*2;
    data = new_data;
  }
  void triple_length() {
    //int n2 = main_app.notes_per_octave*main_app.num_octaves;
    int n2 = main_app.number_of_keys; 
    byte new_data[][] = new byte[length*3][n2+1];
    for (int y = 0;y <= n2;y++) {
      for (int x = 0;x < length;x++) {
        new_data[x*3][y] = data[x][y];
        new_data[(x*3)+1][y] = data[x][y];
        new_data[(x*3)+2][y] = data[x][y];
      }
    }  
    length = length*3;
    data = new_data;
  }

  void set_length(int l) {
    if (l > alloc_length) {
      //int n2 = main_app.notes_per_octave*main_app.num_octaves;
      int n2 = main_app.number_of_keys;
      byte new_data[][] = new byte[l][n2+1];
      for (int y = 0;y <= n2;y++) {
        for (int x = 0;x < alloc_length;x++) {
          new_data[x][y] = data[x][y];
        }
      }
      alloc_length = l;
      data = new_data;
    }
    length = l;
  }
  boolean is_empty() {
    for (int x = 0;x < length;x++) {
      if (is_colunm_empty(x) == false) {return false;}
    }
    return true;
  }
  boolean is_colunm_empty(int x){
    //int n2 = main_app.notes_per_octave*main_app.num_octaves;
    int n2 = main_app.number_of_keys;
    for (int y = 0;y <= n2;y++) {
      if (data[x][y] != 0) {return false;}
    }
    return true;
  }
  void copy(patternc p,int start,int end) {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;
    for (int x = start;x < end;x++) {
      for (int y = 0;y <= n;y++) {
        int a = p.get_cell(x,y);
        set_cell(x,y,a);
      }
    }
  }

  void flip_bits53() {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;

    for (int y = 0;y <= n;y++) {
      for (int x = 0;x < length;x++) {
        if ((data[x][y] & 2) == 2) {
          data[x][y] = (byte) (data[x][y] ^ 1);
        }
      }
    }
  }
  void shift_left() {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;
    byte mask = 3;    
    if (main_app.pan_note == 1) {mask = 1;flip_bits53();}
    if (main_app.pan_note == 3) {mask = 2;flip_bits53();}
    byte mask2 = (byte) (mask ^ 3);
    for (int y = 0;y <= n;y++) {
      byte t = data[0][y];
      for (int x = 0;x < (length-1);x++) {
        byte b = (byte) (data[x][y] & mask2); 
        data[x][y] = (byte) (b | (data[x+1][y] & mask));        
      }
      byte b = (byte) (data[length-1][y] & mask2); 
      data[length-1][y] = (byte) (b | (t & mask));              
    }    
    if (main_app.pan_note == 1) {flip_bits53();}
    if (main_app.pan_note == 3) {flip_bits53();}
  }
  void shift_right() {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;

    byte mask = 3;    
    if (main_app.pan_note == 1) {mask = 1;flip_bits53();}
    if (main_app.pan_note == 3) {mask = 2;flip_bits53();}
    byte mask2 = (byte) (mask ^ 3);
    for (int y = 0;y <= n;y++) {
      byte t = data[length-1][y];
      for (int x = length-1;x > 0;x--) {
        byte b = (byte) (data[x][y] & mask2); 
        data[x][y] = (byte) (b | (data[x-1][y] & mask));        
      }
      byte b = (byte) (data[0][y] & mask2); 
      data[0][y] = (byte) (b | (t & mask));              
    }    
    
    if (main_app.pan_note == 1) {flip_bits53();}
    if (main_app.pan_note == 3) {flip_bits53();}
  }
  void transpose_up() {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;

    byte mask = 3;    
    if (main_app.pan_note == 1) {mask = 1;flip_bits53();}
    if (main_app.pan_note == 3) {mask = 2;flip_bits53();}
    byte mask2 = (byte) (mask ^ 3);
    for (int a = 0;a < length;a++) {
      if (data[a][0] != 0) {return;}
    }
    for (int y = 0;y < n;y++) {
      if (main_app.pan_note == 2) {
        for (int x = 0;x < length;x++) {
          data[x][y] = data[x][y+1];
        }      
      } else {
        for (int x = 0;x < length;x++) {
          byte b = (byte) (data[x][y] & mask2); 
          data[x][y] = (byte) (b | (data[x][y+1] & mask));        
        }
      }
    }
    for (int a = 0;a < length;a++) {
      data[a][n] = 0;
    }
    if (main_app.pan_note == 1) {flip_bits53();}
    if (main_app.pan_note == 3) {flip_bits53();}
  }
  void transpose_down() {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;

    int mask = 3;    
    if (main_app.pan_note == 1) {mask = 1;flip_bits53();}
    if (main_app.pan_note == 3) {mask = 2;flip_bits53();}
    byte mask2 = (byte) (mask ^ 3);
    for (int a = 0;a < length;a++) {
      if (data[a][n] != 0) {return;}
    }
    for (int y = n;y > 0;y--) {
      if (main_app.pan_note == 2) {
        for (int x = 0;x < length;x++) {
          data[x][y] = data[x][y-1];
        }      
      } else {
        for (int x = 0;x < length;x++) {
          byte b = (byte) (data[x][y] & mask2); 
          data[x][y] = (byte) (b | (data[x][y-1] & mask));
        }
      }
    } 
    for (int a = 0;a < length;a++) {
      data[a][0] = 0;
    }   
    if (main_app.pan_note == 1) {flip_bits53();}
    if (main_app.pan_note == 3) {flip_bits53();}
  }
  void set_cell(int x,int y,int a) {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;

    if (y > n) {return;}
    if (y < 0) {return;}
    int x2 = x%length;
    if (x2 < 0) {x2 = x2+length;}
    data[x2][y] = (byte) a;
  }
  int get_cell(int x,int y) {
    //int n = main_app.notes_per_octave*main_app.num_octaves;
    int n = main_app.number_of_keys;
    if (y > n) {return 0;}
    if (y < 0) {return 0;}
    int x2 = x%length;
    if (x2 < 0) {x2 = x2+length;}
    return data[x2][y];
  }
}

