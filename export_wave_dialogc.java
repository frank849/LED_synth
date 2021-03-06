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
import java.util.prefs.Preferences;

public class export_wave_dialogc extends JDialog implements ActionListener {
  //JPanel button_panel;
  //JPanel option_panel;
  JButton ok_button;
  JButton cancel_button;
  ButtonGroup bit_group;
  ButtonGroup nch_group;
  JRadioButton op_8bit;
  JRadioButton op_16bit;
  JRadioButton op_mono;
  JRadioButton op_stereo;
  JComboBox sample_rate_combo_box;
  JLabel fade_in_label;
  JLabel fade_out_label;
  JSpinner fade_in_spinner;
  JSpinner fade_out_spinner;
  int sample_rate = 0;

  boolean ok_clicked = false;
  export_wave_dialogc(Frame owner) {
    super(owner,"wave options",true);
    this.setLayout(new GridLayout(6,2));
    Preferences prefs = main_app.prefs;
    //button_panel = new JPanel();
    //button_panel.setLayout(new GridLayout(1,2));
    //option_panel = new JPanel();
    //option_panel.setLayout(new GridLayout(2,2));
    ok_button = create_button("ok","ok");
    cancel_button = create_button("cancel","cancel");
    //button_panel.add(ok_button);
    //button_panel.add(cancel_button);
    //this.getContentPane().add(option_panel);
    //this.getContentPane().add(button_panel);
    bit_group = new ButtonGroup();
    nch_group = new ButtonGroup();
    op_8bit = new JRadioButton("8 bit");
    bit_group.add(op_8bit);
    op_16bit = new JRadioButton("16 bit");
    bit_group.add(op_16bit);
    this.getContentPane().add(op_8bit);
    this.getContentPane().add(op_16bit);
    op_mono = new JRadioButton("mono");
    nch_group.add(op_mono);
    op_stereo = new JRadioButton("stereo");
    nch_group.add(op_stereo);

    this.getContentPane().add(op_mono);
    this.getContentPane().add(op_stereo);
    sample_rate_combo_box = add_combobox("sample rate:");
    update_sample_rate_combo_box();

    double fi = prefs.getDouble("wave_fade_in",2.0);
    fade_in_label = new JLabel("fade in:");
    fade_in_spinner = new JSpinner(new SpinnerNumberModel(fi, 0.0, 100.0, 0.5));
    this.getContentPane().add(fade_in_label);
    this.getContentPane().add(fade_in_spinner);

    double fo = prefs.getDouble("wave_fade_out",2.0);
    fade_out_label = new JLabel("fade out:");
    fade_out_spinner = new JSpinner(new SpinnerNumberModel(fo,0.0,100.0,0.5));
    this.getContentPane().add(fade_out_label);
    this.getContentPane().add(fade_out_spinner);
    this.getContentPane().add(ok_button);
    this.getContentPane().add(cancel_button);
    //option_group = new JPanel();
    if (prefs.getBoolean("wave_stereo",true)) {
      op_stereo.setSelected(true);
    } else {
      op_mono.setSelected(true);
    }
    if (prefs.getBoolean("wave_16bit",true)) {
      op_16bit.setSelected(true);
    } else {
      op_8bit.setSelected(true);
    }
    this.setBounds(20,20,300,150);
  }
  void update_sample_rate_combo_box() {
    if (sample_rate != pattern_playerc.sample_rate) {
      sample_rate_combo_box.removeAllItems();
      sample_rate = pattern_playerc.sample_rate;
      sample_rate_combo_box.addItem(sample_rate);
      sample_rate_combo_box.addItem(sample_rate>>1);
    }
  }
  JComboBox add_combobox(String text) {
    this.getContentPane().add(new JLabel(text));
    JComboBox cb = new JComboBox();
    this.getContentPane().add(cb);
    return cb;
  }

  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    //button_panel.add(button);
    //this.getContentPane().add(button);
    return button;
  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    //System.out.println(action);
    if (action.equals("ok")) {
      ok_clicked = true;
    }
    if (action.equals("cancel")) {
      ok_clicked = false;
    }
    this.setVisible(false);
  }
}

