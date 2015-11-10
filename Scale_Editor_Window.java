import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
class Scale_Editor_Panel extends JPanel implements MouseListener {
  static int edo = 12;
  static int num_notes = 7;
  static byte scale[];
  static short note[];
  Scale_Editor_Panel() {
    this.addMouseListener(this);    
    new_scale();
  }
  static void new_scale() {
    scale = new byte[edo];
    note = new short[num_notes];
    for (int i = 0;i < num_notes;i++) {
      int n = (i*edo) / num_notes;
      scale[n] = (byte) 1;
    }
    update_notes();
  }
  static int get_note_from_scale(int m) {
     int o = m / num_notes;
     int n = note[m % num_notes];
     return (o*edo)+n;
  }
  static void update_notes() {
    int n = 0;
    for (int i = 0;i < edo;i++) {
      if (scale[i] == 1) {
        note[n] = (short) i;
        n = n + 1;
      }
    }
  }
  void rotate_scale(int d) {
    for (int i = 0;i < edo;i++) {
      int j = (i+d+edo) % edo;
      scale[j] = (byte) (scale[j] | ((scale[i] & 1) << 1));
    }
    for (int i = 0;i < edo;i++) {
      scale[i] = (byte) (scale[i] >> 1);
    }
  }
  protected void paintComponent(Graphics g){ 
    super.paintComponent(g);
    int notes_per_octave = main_app.notes_per_octave;
    int edo2 = scalec.equal_divisions;
    if ((edo != edo2) | (num_notes != notes_per_octave)) {
      edo = edo2;
      num_notes = notes_per_octave;
      new_scale();
    }
    int sq_width = getWidth() / edo;
    for (int x = 0;x < edo;x++){
      if (scale[x] == 1) {
        g.setColor(Color.white);
      } else {
        g.setColor(Color.black);
      }
      g.fillRect(x*sq_width,0,sq_width,getHeight());
    }
    g.setColor(Color.gray);
    for (int x = 1;x <= edo;x++){
      g.drawLine(x*sq_width,0,x*sq_width,getHeight());
    }
  }
  public void mouseClicked(MouseEvent e){
    
  }
  public void mouseEntered(MouseEvent e){
    
  } 
  public void mouseExited(MouseEvent e){
    
  }
  int last_note;
  public void mousePressed(MouseEvent e){
    int sq_width = getWidth() / edo;
    last_note = e.getX() / sq_width;

  }
  public void mouseReleased(MouseEvent e){
    int sq_width = getWidth() / edo;
    int x1 = last_note;
    int x2 = e.getX() / sq_width;
    if ((x1 < edo) & (x2 < edo)) {
      if ((scale[x1] ^ scale[x2]) == 1) {
        scale[x1] = (byte) (scale[x1] ^ 1);
        scale[x2] = (byte) (scale[x2] ^ 1);
      } else {
        rotate_scale(x2-x1);
      }
      update_notes();
    }
    repaint();
  }

}

class Scale_Editor_Window extends JFrame implements ActionListener {
  Scale_Editor_Panel panel;
  JPanel panel2;
  JTextField name_field;
  Scale_Editor_Window() {
    panel = new Scale_Editor_Panel();
    this.getContentPane().add(panel);
    panel2 = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.gridwidth = 1;
    //c.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(new JLabel("name:"),c);

    name_field = new JTextField();
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(name_field,c);

    c.gridx = 2;
    c.gridy = 0;
    c.weightx = 0;
    c.gridwidth = 1;
    JButton b = new JButton("add");
    b.setActionCommand("add");    
    b.addActionListener(this);

    panel2.add(b,c);

    this.getContentPane().add(panel2, BorderLayout.PAGE_END);
    this.pack();
  }
  public void actionPerformed(ActionEvent e) {
    String action = e.getActionCommand();
    if (action.equals("add")) {
        int notes_per_octave = main_app.notes_per_octave;
        scalec s = new scalec(notes_per_octave);
        s.init();
        for (int i = 0;i < notes_per_octave;i++) {
          s.set(i,Scale_Editor_Panel.note[i]);
        }
        main_app.tuning_map.put(name_field.getText(),s);      
    }
  }
}


