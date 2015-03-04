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

public class create_temperament_dialog extends JDialog implements ActionListener {
  JLabel offset_label;
  JSpinner offset_spinner;
  JLabel generator_label;
  JSpinner generator_spinner;
  boolean result;
  create_temperament_dialog(Frame owner,String title) {
    super(owner,title,true);
    int npo = main_app.notes_per_octave;
    setBounds(20,20,200,200);
    this.getContentPane().setLayout(new GridLayout(3,2));
    int octave_cents = main_app.song_player.scale.interval_size>>16;
    //int f3 = main_app.get_3fifth();
    //int f3 = (int) (scalec.generator + 0.5);

    double ed = scalec.equal_divisions;
    offset_label = new JLabel("offset:");
    this.getContentPane().add(offset_label);
    offset_spinner = new JSpinner(new SpinnerNumberModel(0.0,0.0,ed,1.0));
    this.getContentPane().add(offset_spinner);

    //double f2 = ((double)(f3*octave_cents)) / ((double) npo);
    //long lf2 = (long) ((f2*10.0)+0.5);
    //f2 = ((double) lf2) * 0.1;
    generator_label = new JLabel("generator:");
    this.getContentPane().add(generator_label);
    double g = scalec.generator;
    generator_spinner = new JSpinner(new SpinnerNumberModel(g,0.0,ed,0.5));
    this.getContentPane().add(generator_spinner);
      
    create_button("ok","ok");
    create_button("cancel","cancel");

  }
  double get_offset() {
    Number n = (Number) offset_spinner.getValue();
    return n.doubleValue();
  }
  double get_generator() {
    Number n = (Number) generator_spinner.getValue();
    return n.doubleValue();
  }
  boolean OK_Clicked() {
    return result;
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    this.getContentPane().add(button);
    return button;
  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    //System.out.println(action);
    if (action.equals("ok")) {
      scalec.offset = get_offset();
      scalec.generator = get_generator();
      result = true;         
      hide();
    }
    if (action.equals("cancel")) {
      result = false;         
      hide();
    }
  }
}

