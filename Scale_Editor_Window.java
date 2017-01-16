import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
class Scale_Editor_Panel extends JPanel implements MouseListener {
  static int edo = 12;
  static int num_notes = 7;
  static short note[];
  Scale_Editor_Panel() {
    this.addMouseListener(this);    
    new_scale();
  }
  static void new_scale() {
    note = new short[num_notes];
    for (int i = 0;i < num_notes;i++) {
      int n = (i*edo) / num_notes;
      note[i] = (short) n;
    }
  }
  static int get_note_from_scale(int m) {
     int o = m / num_notes;
     int n = note[m % num_notes];
     return (o*edo)+n;
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
    g.setColor(Color.black);
    g.fillRect(0,0,getWidth(),getHeight());

    int sq_width = getWidth() / edo;
    g.setColor(Color.white);
    for (int i = 0;i < num_notes;i++){
      g.fillRect(note[i]*sq_width,0,sq_width,getHeight());
    }
  }
  public void mouseClicked(MouseEvent e){
    
  }
  public void mouseEntered(MouseEvent e){
    
  } 
  public void mouseExited(MouseEvent e){
    
  }
  int find_nearest_note(double p) {
    int min = 0;
    int max = num_notes-1;
    if (p < note[min]) {return min;}
    if (p > note[max]) {return max;}
    while ((max-min) > 1) {
      int mid = (max+min) >> 1;
      if (p > note[mid]) {min = mid;}
      if (p < note[mid]) {max = mid;}
    }
    if ((p-note[min]) < (note[max]-p)) {
      return min;
    } else {
      return max;
    }

  }
  public void mousePressed(MouseEvent e){
    int sq_width = getWidth() / edo;
    double p = e.getX();
    p = p / sq_width;
    int i = find_nearest_note(p-0.5);
    note[i] = (short) p;
    repaint();
  }
  public void mouseReleased(MouseEvent e){
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


