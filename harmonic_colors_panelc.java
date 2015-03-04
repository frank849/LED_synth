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

public class harmonic_colors_panelc extends JPanel implements MouseListener {
  harmonic_colors_panelc() {
    this.addMouseListener(this);
  }
  protected void paintComponent(Graphics g) {
    int w = this.getWidth();
    int h = this.getHeight();
    int nhc = main_panelc.harmonic_color.size();
    int s = h / (nhc+1);
    for (int i = 0;i <= nhc;i++) {
      if (i == nhc) {
        g.setColor(Color.white);
        g.fillRect(0,h-((i+1)*s)-1,w,s);
        g.setColor(Color.black);
        g.drawString("add new harmonic color",4,(h-((i+1)*s)-1)+(s/2));
      } else {
        Color c = (Color) main_panelc.harmonic_color.get(i);
        g.setColor(c);
        g.fillRect(0,h-((i+1)*s)-1,w,s);
        g.setColor(Color.black);
        //g.setColor(main_app.get_text_color(c));
        int hm = (int)((main_panelc.harmonic_low[i]*10)+0.5);
        //main_panelc.harmonic_low[i]
        String str = "harmonic ";
        str = str + ((int) (hm/10)) + ".";
        str = str + ((int) (hm%10)) + " color";        
        g.drawString(str,4,(h-((i+1)*s)-1)+(s/2));
      }
    }
  }
  public void mouseClicked(MouseEvent e) {
    int w = this.getWidth();
    int h = this.getHeight();
    int nhc = main_panelc.harmonic_color.size();
    int s = h / (nhc+1);
    int i = (nhc)-(e.getY() / s);    
    if (i == nhc) {
      Color c = JColorChooser.showDialog(this,"harmonic color",null);
      if (c != null) {main_panelc.add_harmonic_color(c);}
    } else {
      Color c = (Color) main_panelc.harmonic_color.get(i);
      c = JColorChooser.showDialog(this,"harmonic color",c);
      if (c != null) {main_panelc.harmonic_color.set(i,c);}
    }
    repaint();
    main_app.main_panel.repaint();
  }
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}

}

