import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.event.*; 
import javax.swing.*;   
import javax.swing.table.*;
      
import java.awt.*;
import java.awt.event.*;   

public class bass_treble_windowc extends JFrame implements ChangeListener {
  JSpinner spinner[];
  int num_spinners = 0;
  //freq volume bandwidth
 
  bass_treble_windowc() {
     update();
  }
  void update() {
    num_spinners = 0;
    equalizer e2 = equalizer.e2;
    this.getContentPane().removeAll();
    this.getContentPane().setLayout(new GridLayout(e2.num_bands+1,4));
    spinner = new JSpinner[e2.num_bands*3];
    this.getContentPane().add(new JLabel());
    this.getContentPane().add(new JLabel("freq"));
    this.getContentPane().add(new JLabel("volume"));
    this.getContentPane().add(new JLabel("bandwidth"));

    for (int i = 0;i < e2.num_bands;i++) {
      this.getContentPane().add(new JLabel(e2.band[i].name));
      int f = e2.band[i].get_freq();
      int fs = e2.band[i].get_freq_step();
      add_spinner(new SpinnerNumberModel(f,0,65535,fs));
    
      int db = e2.band[i].get_DB();
      add_spinner(new SpinnerNumberModel(db,-128,127,1));

      int bw = e2.band[i].get_bandwidth();
      int bw_s = e2.band[i].get_bandwidth_step();
      add_spinner(new SpinnerNumberModel(bw,0,65535,bw_s));
    }
    //this.getContentPane().add(new JLabel("bass"));
    //add_spinner(new SpinnerNumberModel(e2.pband[1].get_freq(),20,300,1));
    //add_spinner(new SpinnerNumberModel(0,0,100,1));
    //add_spinner(new SpinnerNumberModel(e2.pband[0].get_bandwidth(),0.1,100.0,0.5));

    this.pack();
  }

  
  void add_spinner(SpinnerNumberModel m){
    //JLabel label = new JLabel(text);
    //this.getContentPane().add(label);
    spinner[num_spinners] = new JSpinner(m);
    spinner[num_spinners].addChangeListener(this);
    this.getContentPane().add(spinner[num_spinners]);
    num_spinners = num_spinners + 1;  
  }
  public void stateChanged(ChangeEvent e) {
    for (int i = 0;i < num_spinners;i++) {
      if (e.getSource() == spinner[i]) {
        int i2 = i % 3;
        int i3 = i / 3;
        equalizer e2 = equalizer.e2;
        Number n = (Number) spinner[i].getValue();
        if (i2 == 0) {
          e2.band[i3].set_freq(n.intValue());
        }
        if (i2 == 1) {
          e2.band[i3].set_DB(n.intValue());
        }
        if (i2 == 2) {
          e2.band[i3].set_bandwidth(n.intValue());
        }
        equalizer.e1.copy(e2);
        equalizer.e3.copy(e2);        
      }
    }
  }
}


