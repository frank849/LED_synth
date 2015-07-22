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

public class hex_keyboard_panelc extends JPanel implements MouseListener, KeyListener {
  static int x_step = 3;
  static int y_step = 7;
  int x_offset = 0;
  int y_offset = 0;
  int zoom = 1;
  hex_keyboard_panelc() {
    this.addMouseListener(this);

  } 
  public void keyPressed(KeyEvent e){
    int a = e.getKeyCode();
    char ch = e.getKeyChar();  
    String s = e.getKeyText(a);
    ch = Character.toLowerCase(ch);
    patternc pattern = main_app.song_player.pattern;
    if (ch == '[') {
      if (zoom > 1) {
        zoom = zoom - 1;
      }
    }
    if (ch == ']') {
        zoom = zoom + 1;
    }
    if (ch == 'a') {
      pattern.transpose_up();
      main_app.main_panel.repaint();
    }
    if (ch == 'z') {
      pattern.transpose_down();
      main_app.main_panel.repaint();
    }
    int pos = main_app.pattern_player.pos;
    int old_pos = pos;
    if (ch == 's') {
      pos = pos - 1;
    }
    if (ch == 'd') {
      pos = pos + 1;
    }      
    if (main_app.pattern_player.pos != pos) {
      main_app.pattern_player.pos = pos;
      main_app.main_panel.update_pos_line(old_pos,null,1);
      main_app.main_panel.update_pos_line(pos,null,1);
    }
    if (a == KeyEvent.VK_UP) {
      y_offset = y_offset - 1;
    }
    if (a == KeyEvent.VK_DOWN) {
      y_offset = y_offset + 1;
    }
    if (a == KeyEvent.VK_LEFT) {
      x_offset = x_offset - 1;
    }
    if (a == KeyEvent.VK_RIGHT) {
      x_offset = x_offset + 1;
    }
    //System.out.println("key: " + ch);
    repaint();
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {
    int x = e.getX();    
    int y = e.getY();     
    int w = this.getWidth();
    int h = this.getHeight();
    int xs = x_step;
    int ys = y_step;
    if (x_step > y_step) {
      xs = y_step;
      ys = x_step;
      int tmp = x;x = y;y = tmp;
    }
    int npo = main_app.notes_per_octave;
    //int num_notes24 = main_app.num_octaves*npo;
    int num_notes24 = main_app.number_of_keys;
    int pos = main_app.pattern_player.pos;
    int z = zoom;
    int xo = x_offset;
    int yo = y_offset;
    patternc p = main_app.song_player.pattern;
    int x1 = x / z;
    int y1 = y / z;
    int x2 = (x1 / ys)+xo;
    int a = ((x2*xs)%ys);
    int y2 = (y1 - a) / ys;
    y2 = ((y2 + yo) * ys) + a;
    int c = p.get_cell(pos,y2);
    p.set_cell(pos,y2,c ^ 2);
    main_app.main_panel.repaint();
    repaint();
  }
  public void mouseReleased(MouseEvent e) {}
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    int w = this.getWidth();
    int h = this.getHeight();
    int xs = x_step;
    int ys = y_step;
    if (x_step > y_step) {
      xs = y_step;
      ys = x_step;
    }
    int npo = main_app.notes_per_octave;
    //int num_notes24 = main_app.num_octaves*npo;
    int num_notes24 = main_app.number_of_keys;
    int pos = main_app.pattern_player.pos;
    int z = zoom;
    int xo = x_offset;
    int yo = y_offset;
    patternc p = main_app.song_player.pattern;
    if (x_step > y_step) {
      int tmp = w;w = h;h = tmp;
    }
    for (int x = 0;x < (w / (ys*z));x++) {
      for (int y = 0;y < (h / (ys*z));y++) {
        int x2 = (x*ys);
        int y2 = (y*ys)+(((x+xo)*xs)%ys);
        if (x_step > y_step) {
          x2 = y2;
          y2 = (x*ys);
        }
        int y3 = ((y+yo)*ys)+(((x+xo)*xs)%ys);
        if (y3 < 0) {continue;}
        if (y3 >= num_notes24) {continue;}
        //harmonic_color.size();
        int a2 = 0;
        int sz = main_panelc.harmonic_color.size();
        for (int hm = sz;hm > 0;hm--) {
          int o = main_panelc.harmonic_offset[hm-1];
          int a = p.get_cell(pos,y3+o);
          int s = ys*z;
          a2 = a2 | (a ^ (a >> 1));
          if (a > 0) {
            g.setColor((Color) main_panelc.harmonic_color.get(hm-1));
          }
    	  if (a == 1) {g.fillRect(x2*z,y2*z,s>>1,s);}
    	  if (a == 2) {g.fillRect(x2*z,y2*z,s,s);}
    	  if (a == 3) {
            g.fillRect((x2*z)+(s>>1),y2*z,(s+1)>>1,s);	
          }
        }
        if (a2 != 3) {
          int i43 = ((y3-1+npo)%npo);
          int s = ys*z;
          if (i43 < 0) {i43 = i43 + npo;}
          if (main_panelc.get_scale34(npo-i43-1) == 1) {
            g.setColor(main_panelc.gray);
      	    if (a2 == 2) {g.fillRect(x2*z,y2*z,s>>1,s);}
            if (a2 == 0) {g.fillRect(x2*z,y2*z,s,s);}
     	    if (a2 == 1) {
              g.fillRect((x2*z)+(s>>1),y2*z,(s+1)>>1,s);	
            }
          }
        }
        g.setColor(Color.black);
        g.drawRect(x2*z,y2*z,ys*z,ys*z);
      }
    }
  }
}

