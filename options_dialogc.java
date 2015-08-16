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


public class options_dialogc extends JDialog implements ActionListener{
  int num_textfields;
  int num_spinners;
  JTextField textfield[];
  JSpinner spinner[];
  JComboBox interpolation_combo_box;
  String[] interpolation_names = {"Nearest neighbor","linear","quadratic","cubic"};
  JComboBox sample_rate_combo_box;
  String[] sample_rates = {"44100","48000","88200","96000","176400","192000"};
  String[] tunning_table_step_size_combobox_values = 
  {"1","0.5","0.25","0.2","0.1","0.05","0.04","0.02","0.01"};
  JComboBox tunning_table_step_size_combobox;

  options_dialogc(Frame owner,String title) {
    super(owner,title,false);
    setBounds(20,20,200,200);
    textfield = new JTextField[20];
    spinner = new JSpinner[20];
    this.getContentPane().setLayout(new GridLayout(10,2));
    add_textfield("lowest note freq:");
    add_textfield("highest note freq:");
    sample_rate_combo_box = add_combobox("sample rate:",sample_rates);
    sample_rate_combo_box.setEditable(true);
    //sample_rate_combo_box.setSelectedIndex(1);


    interpolation_combo_box = add_combobox("interpolation: ",interpolation_names);

    tunning_table_step_size_combobox = add_combobox("tunning table step size: ",
    tunning_table_step_size_combobox_values);
    tunning_table_step_size_combobox.setEditable(true);

    //add_spinner("octave cents: ",new SpinnerNumberModel(1200,0,32000,1));
    add_spinner("beats per minute: ",new SpinnerNumberModel(60.0,3.0,1800.0,1.0));    
    int np = main_app.prime_list.num_primes;
    add_spinner("number of primes: ",new SpinnerNumberModel(np,1,54,1));    
    add_spinner("hex x dir: ",new SpinnerNumberModel(np,1,500,1));    
    add_spinner("hex y dir: ",new SpinnerNumberModel(np,1,500,1));    

    create_button("ok","ok");
    create_button("cancel","cancel");
    pack();
  }
  JComboBox add_combobox(String text,String[] list) {
    this.getContentPane().add(new JLabel(text));
    JComboBox cb = new JComboBox(list);
    this.getContentPane().add(cb);
    return cb;
  }
  void add_textfield(String text){
    JLabel label = new JLabel(text);
    this.getContentPane().add(label);
    textfield[num_textfields] = new JTextField("");
    this.getContentPane().add(textfield[num_textfields]);
    num_textfields = num_textfields + 1;
  }
  void add_spinner(String text,SpinnerNumberModel m){
    JLabel label = new JLabel(text);
    this.getContentPane().add(label);
    spinner[num_spinners] = new JSpinner(m);
    this.getContentPane().add(spinner[num_spinners]);
    num_spinners = num_spinners + 1;  
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    this.getContentPane().add(button);
    return button;
  }
  public void show() {
    super.show();
    textfield[0].setText(Integer.toString(main_app.base_freq));
    textfield[1].setText(Integer.toString(song_playerc.highest_note_freq));
    ComboBoxEditor ed = sample_rate_combo_box.getEditor();
    ed.setItem(Integer.toString(pattern_playerc.sample_rate));

    ed = tunning_table_step_size_combobox.getEditor();
    ed.setItem(Double.toString(tunning_table_windowc.step_size));

    //spinner[0].setValue(new Integer(main_app.octave_cents));
    int i = sampleplayerc.interpolation;
    interpolation_combo_box.setSelectedIndex(i);

    spinner[0].setValue(new Float(main_app.tempo));
    int np = main_app.prime_list.num_primes;
    spinner[1].setValue(new Integer(np));
    int x_step = hex_keyboard_panelc.x_step;
    spinner[2].setValue(new Integer(x_step));
    int y_step = hex_keyboard_panelc.y_step;
    spinner[3].setValue(new Integer(y_step));
  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    Number n;
    if (action.equals("ok")) {
      this.hide();
      Preferences prefs = main_app.prefs;
      main_app.base_freq = Integer.parseInt(textfield[0].getText());
      song_playerc.highest_note_freq = Integer.parseInt(textfield[1].getText());
      //n = (Number) spinner[0].getValue();
      //main_app.octave_cents = n.intValue();
      ComboBoxEditor ed = sample_rate_combo_box.getEditor();
      int sample_rate = Integer.parseInt((String) ed.getItem());
      if (pattern_playerc.sample_rate != sample_rate) {
         equalizer.sample_rate_changed();
         pattern_playerc.sample_rate = sample_rate;
         prefs.putInt("sample_rate",sample_rate);
      }
      ed = tunning_table_step_size_combobox.getEditor();
      tunning_table_windowc.step_size = Double.parseDouble((String) ed.getItem());
      main_app.tunning_table_window.update_step_size();
      int i = interpolation_combo_box.getSelectedIndex();
      sampleplayerc.interpolation = i;
      prefs.putInt("interpolation",i);
      n = (Number) spinner[0].getValue();
      main_app.tempo = n.floatValue();
      n = (Number) spinner[1].getValue();
      int np = n.intValue();
      n = (Number) spinner[2].getValue();
      hex_keyboard_panelc.x_step = n.intValue();
      n = (Number) spinner[3].getValue();
      hex_keyboard_panelc.y_step = n.intValue();
      int np2 = main_app.prime_list.num_primes;
      while (np > main_app.prime_list.num_primes) {
        main_app.prime_list.add_prime();
      }
      if (np != np2) {
        main_app.prime_list.num_primes = np;
        if (main_app.prime_table_window != null) {
          main_app.prime_table_window.create_new_panel();
          if (main_app.prime_table_window.isVisible()) {
            //main_app.prime_table_window.hide();
            main_app.prime_table_window.show();
          }
        }
        main_app.pattern_player.ins.alloc_string_tables();
        main_app.song_player.update_players();
        main_panelc.update_low_harmonics();
        main_panelc.update_harmonic_offsets();
      }
      
      main_app.song_player.update_players();
    }
    if (action.equals("cancel")) {
      this.hide();
    }
  }
}

