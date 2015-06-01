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


public class main_panelc extends JPanel implements KeyListener,MouseListener, 
MouseMotionListener, WindowListener {
  static Color dark_red = new Color(168,0,0);
  static Color dark_blue = new Color(0,0,168);
  
  static Color purple = new Color(255,0,255);
  static Color black = Color.black;
  static Color gray = new Color(180,180,180);
  static Color white = Color.white;
  static Color yellow = new Color(255,230,0);
  static Color green = Color.green;
  static Color red = Color.red;
  static Color blue = Color.blue;
  static String filename;
  static String dirname;
  static int row_offset = 0;
  static int col_offset = 0;
  static int row34 = 0;
  static int col34 = 0;
  static byte scale34[] = {0,1,0,1,0,1,0,0,1,0,1,0};
  static float harmonic_low[];// = {1.0,2.0,3.0,4.0};
  static int harmonic_offset[];// = {0,12,19,24};
  static Vector harmonic_color = new Vector();// = {red,yellow,green,blue};
  static int sel_harmonic = 0;

  static int song_pos = -1;
  //static int num_harmonic_colors = 4;
  //static int last_pos = 0;
  main_panelc() {
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    harmonic_color.add(purple);
    //harmonic_color.add(yellow);
    //harmonic_color.add(green);
    add_harmonic_color(yellow);
    dirname = System.getProperty("user.dir");
  }
  static double get_centsf(float f) {
    return (((Math.log(f) / Math.log(2)) * 1200)+0.5);
  }
  static void add_harmonic_color(Color c) {
    //num_harmonic_colors = num_harmonic_colors + 1;
    harmonic_color.add(c);
    update_harmonics();
  }
  static void update_harmonics() {
    int n = harmonic_color.size();
    harmonic_low = new float[n];
    harmonic_offset = new int[n];
    update_low_harmonics();
    update_harmonic_offsets();
  }
  static void update_low_harmonics() {
    float limit = 2.0f;
    int num_primes = main_app.prime_list.num_primes;
    Vector harmonic_list = new Vector();    
    int num_harmonic_colors = harmonic_color.size();
    while (harmonic_list.size() < num_harmonic_colors) {
      harmonic_list.clear();
      main_app.prime_list.gen_harmonics(harmonic_list,1.0f,1.0f,1.0f,num_primes-1,limit);    
      limit = limit * 2.0f;
    }
    for (int i = 0;i < num_harmonic_colors;i++) {
      harmonic_low[i] = 1000000.0f;
    }
    for (int i = 0;i < harmonic_list.size();i++) {
      int i2 = num_harmonic_colors-1;
      Harmonic n = (Harmonic) harmonic_list.get(i);
      float h = n.freq;
      while (i2 >= 0) {
        if (h > harmonic_low[i2]) {break;}
        if (i2 < (num_harmonic_colors-1)) {
          harmonic_low[i2+1] = harmonic_low[i2];
        }
        harmonic_low[i2] = h;
        i2 = i2 - 1;
      }
    }
  }
  static void update_harmonic_offsets() {
    for (int i = 0;i < harmonic_color.size();i++) {
      float f = harmonic_low[i];
      double oc = main_app.song_player.scale.interval_size / 65536.0;
      double notes_oct = main_app.notes_per_octave;
      int nsteps = (int) (((get_centsf(f) / oc) * notes_oct)+0.5);
      harmonic_offset[i] = nsteps;
    }
  }
  static int get_scale34(int i) {
    return main_app.song_player.pattern.get_scale(i);
    //return scale34[i];
  }
  static void set_scale34(int i,int b) {
    main_app.song_player.pattern.set_scale(i,b);
    //scale34[i] = (byte) b;
  }

  static void setup_scale34(int et) {
      scale34 = new byte[et];
      int i = 1;
      double f = scalec.generator;
      f = (f * et) / scalec.equal_divisions;
      int f23 = (int) (f + 0.5);
      
      //int f23 = main_app.get_3fifth();
      while (scale34[i] == 0) {
        scale34[i] = (byte) 1;
	i = (i + f23) % et;
        if (scale34[(i+1)%et] == 1) {break;}
        if (scale34[(i+et-1)%et] == 1) {break;}      
      }  
  }
  void update_pos_line(int p,Graphics g,int c) {
      if (g == null) {
        g = this.getGraphics();
      }
      int w = this.getWidth();
      int h = this.getHeight();
      int s = main_app.sqsize;
      int npo = main_app.notes_per_octave;
      //int o = main_app.octave_offset;
      //int o2 = o*npo;
      int num_notes24 = main_app.number_of_keys;
      int x = p-col_offset;
      if (((num_notes24-row_offset)*s) < h) {
        h = ((num_notes24-row_offset)*s);
      }
      if (c == 1) {
	g.setColor(blue);
      } else {g.setColor(black);}
        //int a = ((num_notes24-o2+1)*s);
        //if (a < h) {
        //  g.drawLine(x*s,0,x*s,a);
        //} else { 
      g.drawLine(x*s,0,x*s,h);
        //}
        //x = x + 1;
      

      //g.drawLine(p*s,0,p*s,h);  
      //last_pos = p;
  }
  public int getWidth23() {
    int s = main_app.sqsize;
    int l = main_app.song_player.pattern.get_length();
    //l = l - col_offset;
    return s*l;
  }
  public int getHeight23() {
    int s = main_app.sqsize;
    int npo = main_app.notes_per_octave;
    int num_notes24 = main_app.number_of_keys;
    //num_notes24 = num_notes24 - row_offset;
    return s*num_notes24;
  }
  public void update_size() {
    int w = this.getWidth23();
    int h = this.getHeight23();
    setPreferredSize(new Dimension(w,h));
    revalidate();
    //JScrollBar hscroll = main_app.main_panel_scroller.getHorizontalScrollBar();
    //JScrollBar vscroll = main_app.main_panel_scroller.getVerticalScrollBar();
    //hscroll.setBlockIncrement(300);
    //hscroll.setUnitIncrement(300);
    //vscroll.setBlockIncrement(300);
    //vscroll.setUnitIncrement(300);
  
  }

  protected void paintComponent(Graphics g) {           
    int w = this.getWidth();
    int h = this.getHeight();
    int s = main_app.sqsize;
    
    int npo = main_app.notes_per_octave;
    int f23 = (int) (scalec.generator + 0.5);
    int x = 0;
    int y = 0;
    //int o = main_app.octave_offset;
    //int o2 = o*npo;
    int num_notes24 = main_app.number_of_keys;
    g.setColor(white);
    g.fillRect(0,0,w,h);
    while ((y*s) < h) {
      int y2 = y + row_offset;
      y2 = y2 - harmonic_offset[sel_harmonic];
      if (y2 > num_notes24) {break;}
      if (song_pos == -1) {
        int i43 = ((y2-1+npo)%npo);
        if (i43 < 0) {i43 = i43 + npo;}
        if (get_scale34(npo-i43-1) == 1) {
          g.setColor(gray);
          g.fillRect(0,y*s,w,s);	
        }
      }

      x = 0;
      while ((x*s) < w) {
        patternc p = main_app.song_player.pattern;
        int x2 = x+col_offset;
        int y3 = y2;
        if (song_pos >= 0) {
          int spos = song_pos;
          int sz = main_app.song_list.size();
          song_list_entryc en = (song_list_entryc) main_app.song_list.get(spos);
          p = (patternc) main_app.pattern_list.get(en.pattern);
          while (x2 >= p.length) {
            spos = (spos + 1)%sz;
            x2 = x2 - p.length;
            y3 = y3 - en.mode;
            int dc = en.cents;
            en = (song_list_entryc) main_app.song_list.get(spos);
            dc = en.cents-dc;
            y3 = y3 + main_app.get_num_steps_from_cents(dc);
            y3 = y3 + en.mode;
            p = (patternc) main_app.pattern_list.get(en.pattern);
          }
          if (x2 == 0) {
            int i43 = ((y3-1+npo)%npo);
            if (i43 < 0) {i43 = i43 + npo;}
            if (p.get_scale(npo-i43-1) == 1) {
              g.setColor(gray);
              g.fillRect(x*s,y*s,s*p.length,s);
            }
          }
        }
        for (int hm = harmonic_color.size();hm > 0;hm--) {
          int o = harmonic_offset[hm-1];
          int a = p.get_cell(x2,y3+o);

        //if ((a == 0) & ((((y+11)*7)%12)<5)) {}
    	  if (a > 0) {
            g.setColor((Color) harmonic_color.get(hm-1));
            //g.setColor(Color.red);
            g.fillRect(x*s,y*s,s,s);	
          }

          if (a == 1) {
            g.setColor(black);
            g.fillOval((x*s)+(s>>2),(y*s)+(s>>2),s>>1,s>>1);
          }
        //if (a == 2) {
        //  g.setColor(purple);
        //  g.fillOval((x*s)+(s>>2),(y*s)+(s>>2),s>>1,s>>1);
        //}
          if (a == 3) {
            g.setColor(white);
            g.fillOval((x*s)+(s>>2),(y*s)+(s>>2),s>>1,s>>1);
          }
          //if ((a > 0) & ((a & 1) == 1)) {
          //  g.setColor(black);
          //  g.drawOval((x*s)+(s>>2),(y*s)+(s>>2),s>>1,s>>1);
          //}
        }
        x = x + 1;
      }
      y = y + 1;
    }

    y = 0;
    while ((y*s) < h) {
      if ((y-1+row_offset) > num_notes24) {break;}
      g.setColor(black);
      //if ((y % 3) == 0) {g.setColor(dark_red);}
      if ((y % npo) == 0) {g.setColor(dark_red);}
      if ((y % npo) == f23) {g.setColor(dark_blue);}
      g.drawLine(0,y*s,w,y*s);
      y = y + 1;
    }
    g.setColor(black);
    x = 0;
    while ((x*s) < w) {
        g.drawLine(x*s,0,x*s,h);
        x = x + 1;
    }
    x = 0;
    int spos = song_pos;
    if (song_pos >= 0) {
      int sz = main_app.song_list.size();
      song_list_entryc en = (song_list_entryc) main_app.song_list.get(spos);
      patternc p = (patternc) main_app.pattern_list.get(en.pattern);
      while ((x*s) < w) {
        spos = (spos + 1)%sz;
        x = x + p.length;
        en = (song_list_entryc) main_app.song_list.get(spos);
        p = (patternc) main_app.pattern_list.get(en.pattern);
        g.drawLine((x*s)-1,0,(x*s)-1,h);
      }
    }

    //update_pos_line(0,g);
  }
  public void keyPressed(KeyEvent e){
    int a = e.getKeyCode();
    char ch = e.getKeyChar();  
    String s = e.getKeyText(a);
    ch = Character.toLowerCase(ch);
    boolean use_arrow_keys43 = false;
    patternc pattern = main_app.song_player.pattern;

    pattern_playerc p = main_app.pattern_player;
    if (ch == '_') {
      if (p.bass > 0) {
        p.bass = p.bass - 1;
      }
    }
    if (ch == 'h') {
      sel_harmonic = sel_harmonic + 1;
      if (sel_harmonic == harmonic_color.size()) {
        sel_harmonic = 0;
      }
      main_app.main_panel.repaint();
    }
    if (ch == '+') {
      p.bass = p.bass + 1;
    }
    if (ch == '-') {
      //if (p.volume > 0) {
        p.volume = p.volume - 1;
      //}
    }
    if (ch == '=') {
      //if (p.volume < 320) {
        p.volume = p.volume + 1;
      //}
    }
    if (a == KeyEvent.VK_SPACE) {
      main_app.pattern_player.paused = !main_app.pattern_player.paused;
    }
    if (use_arrow_keys43) {
    if (a == KeyEvent.VK_UP) {
      if (row_offset > 0) {
        row_offset = row_offset - 1;
      }
      main_app.main_panel.repaint();
    }
    if (a == KeyEvent.VK_DOWN) {
      row_offset = row_offset + 1;
      main_app.main_panel.repaint();
    }
    if (a == KeyEvent.VK_LEFT) {
      if (col_offset > 0) {
        col_offset = col_offset - 1;
      }
      main_app.main_panel.repaint();
    }
    if (a == KeyEvent.VK_RIGHT) {
      col_offset = col_offset + 1;
      main_app.main_panel.repaint();
    }
    }
    //if ((ch == '<') | (ch == ',')) {
    //  if (main_app.octave_offset < (main_app.num_octaves-1)) {
    //    main_app.octave_offset = main_app.octave_offset + 1;
    //  }
    //  main_app.main_panel.repaint();
    //}
    //if ((ch == '>') | (ch == '.')) {
    //  if (main_app.octave_offset > 0) {
    //    main_app.octave_offset = main_app.octave_offset - 1;
    //  }
    //  main_app.main_panel.repaint();
    //}
    if ((ch == '[') | (ch == '{')) {
      if (main_app.sqsize > 5) {
        main_app.sqsize = main_app.sqsize - 1;
      }
      update_size();
      main_app.main_panel.repaint();
    }
    if ((ch == ']') | (ch == '}')) {
      if (main_app.sqsize < 260) {
        main_app.sqsize = main_app.sqsize + 1;
      }
      update_size();
      main_app.main_panel.repaint();
    }
    if (ch == 'q') {
      int l = pattern.get_length();
      if ((pattern.is_colunm_empty(l-1) == true) & (l > 4)) {
        pattern.set_length(l-1);
      }
      update_size();
      main_app.update_status_bar();
      main_app.main_panel.repaint();
    }
    if (ch == 'w') {
      int l = pattern.get_length();
      if (l < 65000) {
        pattern.set_length(l+1);
      }
      update_size();
      main_app.update_status_bar();
      main_app.main_panel.repaint();
    }
    if (ch == 'a') {
      pattern.transpose_up();
      main_app.main_panel.repaint();
    }
    if (ch == 'z') {
      pattern.transpose_down();
      main_app.main_panel.repaint();
    }
    if (main_app.play_mode == main_app.NOTE_MODE) {
      int pos = main_app.pattern_player.pos;
      main_app.main_panel.update_pos_line(pos,null,0);
      if (ch == 's') {pos = pos - 1;}
      if (ch == 'd') {pos = pos + 1;}      
      main_app.pattern_player.pos = pos;
      main_app.main_panel.update_pos_line(pos,null,1);
      main_app.hex_keyboard_panel.repaint();
    } else {
      if (ch == 's') {
        pattern.shift_left();
        main_app.main_panel.repaint();
      }
      if (ch == 'd') {
        pattern.shift_right();
        main_app.main_panel.repaint();
      }
    }
    if (ch == 'f') {
      main_app.play_mode = (main_app.play_mode + 1)%3;
      main_app.update_status_bar();
    }
    if (ch == 'x') {main_app.pan_note = 1;}
    if (ch == 'c') {main_app.pan_note = 2;}
    if (ch == 'v') {main_app.pan_note = 3;}
    main_app.update_status_bar();
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}
  public void windowActivated(WindowEvent e){}
  public void windowClosed(WindowEvent e){
  }
  public void windowClosing(WindowEvent e){
    main_app.exit2();  
    //try{
    //  System.exit(0);
    //}catch(SecurityException err){
    //  System.out.println("can not exit");
    //}
  }
  public void windowDeactivated(WindowEvent e){}
  public void windowDeiconified(WindowEvent e){}
  public void windowIconified(WindowEvent e){}
  public void windowOpened(WindowEvent e){}

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  long time43;
  long get_time43() {
    long t = System.currentTimeMillis();
    long r = t-time43;
    time43 = t;
    return r;
  }
  public void mousePressed(MouseEvent e) {  
    int x = e.getX();    
    int y = e.getY();    
    int s = main_app.sqsize;
    int y2 = 0;//main_app.octave_offset;
    long t = get_time43();
    //y2 = y2 * main_app.notes_per_octave;
    y2 = y2 + (y/s)+row_offset;
    row34 = y2;  
    if (t > 30) {
      update_cell(x/s,y2);
    }
    col34 = (x/s);    
    
    int pos = main_app.pattern_player.pos;
    main_app.main_panel.update_pos_line(pos,null,0);
    main_app.pattern_player.pos = col34;
    main_app.main_panel.update_pos_line(col34,null,1);
    main_app.hex_keyboard_panel.repaint();
    this.requestFocusInWindow();
  }
  public void mouseReleased(MouseEvent e) {
    long t = get_time43();
  }
    
  public void mouseMoved(MouseEvent e) {
  }
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();    
    int y = e.getY();    
    int b = e.getButton();
    int s = main_app.sqsize;
    int y2 = 0;//main_app.octave_offset;
    //y2 = y2 * main_app.notes_per_octave;
    y2 = y2 + (y/s)+row_offset;
    if ((col34 != (x/s))&(y2 == row34)) {
      update_cell(x/s,y2);
      col34 = (x/s);
    }
  }
  void update_cell(int x2,int y2) {
      //int o = harmonic_offset[sel_harmonic];
      int o = 0;
      patternc p = main_app.song_player.pattern;
      if (song_pos >= 0) {
          int spos = song_pos;
          int sz = main_app.song_list.size();
          song_list_entryc en = (song_list_entryc) main_app.song_list.get(spos);
          p = (patternc) main_app.pattern_list.get(en.pattern);
          while (x2 >= p.length) {
            spos = (spos + 1)%sz;
            x2 = x2 - p.length;
            y2 = y2 - en.mode;
            en = (song_list_entryc) main_app.song_list.get(spos);
            y2 = y2 + en.mode;
            p = (patternc) main_app.pattern_list.get(en.pattern);
          }
      }
      int a = p.get_cell(x2,y2+o);
      a = a ^ main_app.pan_note;
      //System.out.println("a: " + a);
      main_app.song_modified = true;
      p.set_cell(x2,y2+o,a);
      repaint();  
  }
}

