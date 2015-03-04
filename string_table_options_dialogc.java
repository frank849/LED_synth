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

public class string_table_options_dialogc extends JDialog implements ActionListener{
  static String[] sizeStrings = {"256kB","384kB","512kB","768kB",
  "1MB", "1.5MB", "2MB", "3MB", "4MB", "6MB", "8MB", "12MB", "16MB" };
  static String[] numharmonicStrings = {"1","2","4","8","16","32","64", 
  "128", "256", "512", "1024" };
  static int minSize = 16;
  static int maxSize = sizeStrings.length+minSize-1;
  JComboBox sizeList;
  JComboBox numharmonicList;
  JSpinner bandwidth_spinner;
  JSpinner seed_spinner;
  string_table_options_dialogc(Frame owner,String title) {
    super(owner,title,false);
    setBounds(20,20,200,200);
    this.getContentPane().setLayout(new GridLayout(5,2));
    create_label("size:");
    sizeList = new JComboBox(sizeStrings);
    this.getContentPane().add(sizeList);
    create_label("max harmonics:");
    numharmonicList = new JComboBox(numharmonicStrings);
    this.getContentPane().add(numharmonicList);
    //create_spinner(new SpinnerNumberModel(60.0,3.0,1800.0,1.0));  
    create_label("bandwidth:");
    bandwidth_spinner = create_spinner(new SpinnerNumberModel(50.0,3.0,1800.0,1.0));  
    create_label("seed:");
    seed_spinner = create_spinner(new SpinnerNumberModel(0,0,1000000000,1));  
    create_button("ok","ok");
    create_button("cancel","cancel");
  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    Number n;
    if (action.equals("ok")) {
      int o = numharmonicList.getSelectedIndex();
      int s = sizeList.getSelectedIndex()+minSize;
      main_app.pattern_player.ins.string_table_num_octaves = o+1;
      main_app.pattern_player.ins.string_table_psize = s;
      n = (Number) bandwidth_spinner.getValue();
      main_app.pattern_player.ins.bandwidth = n.floatValue();
      main_app.pattern_player.ins.alloc_string_tables();
      n = (Number) seed_spinner.getValue();
      s = n.intValue();
      main_app.pattern_player.ins.string_table_seed = s;
      main_app.song_player.create_players();
      this.hide();
    }
    if (action.equals("cancel")) {
      this.hide();
    }
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    this.getContentPane().add(button);
    return button;
  }
  JLabel create_label(String text) {  
    JLabel label = new JLabel(text);
    this.getContentPane().add(label);
    return label;
  }
  JSpinner create_spinner(SpinnerNumberModel m){
    JSpinner spinner = new JSpinner(m);
    this.getContentPane().add(spinner);
    return spinner;
  }
  public void show() {
    super.show();     
    int o = main_app.pattern_player.ins.string_table_num_octaves-1;
    int s = main_app.pattern_player.ins.string_table_psize;
    numharmonicList.setSelectedIndex(o);
    sizeList.setSelectedIndex(s-minSize);
    float b = main_app.pattern_player.ins.bandwidth;
    bandwidth_spinner.setValue(new Float(b));
    s = main_app.pattern_player.ins.string_table_seed;
    seed_spinner.setValue(new Integer(s));
  }
}

