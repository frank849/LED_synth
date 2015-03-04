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

public class new_song_wizardc extends JDialog implements ActionListener {
  //JLabel number_of_tones_label;
  JComboBox number_of_tones_combo_box;
  //JTextField number_of_tones_field;
  //JLabel generator_label;
  //JTextField generator_field;
  //JLabel period_label;
  //JTextField period_field;
  //JLabel equal_divisions_label;
  //JTextField equal_divisions_field;

  int equal_divisions;
  int generator;
  int period;
  JSpinner generator_spinner;
  JSpinner period_spinner;
  JSpinner equal_divisions_spinner;
  
  //JLabel interval_label;
  JComboBox interval_combo_box;
  //JLabel highest_freq_label;
  //JTextField highest_freq_field;
  //JLabel base_freq_label;
  //JTextField base_freq_field;
  JSpinner highest_freq_spinner;
  JSpinner base_freq_spinner;
  JSpinner black_notes_spinner;
  JLabel name_label;
  JTextField name_field;
  boolean result;
  JButton backButton;
  JButton nextButton;
  JButton cancelButton;
  JPanel current_panel;
  int current_index;
  JPanel interval_panel;
  JPanel range_panel;
  JPanel generator_panel;
  JPanel mos_panel;
  Stack previous;
  String[] interval_strings = {"2/1","3/1","4/1"};
  double numerator;
  double denominator;
  new_song_wizardc(Frame owner,String title) {
    super(owner,title,true);
    setBounds(20,20,200,200);
    previous = new Stack();
    backButton = create_button("back","back");
    nextButton = create_button("next","next");
    cancelButton = create_button("cancel","cancel");
    backButton.setEnabled(false);

    JPanel navButtons = new JPanel();
    navButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
    navButtons.add(backButton);
    navButtons.add(nextButton);

    JPanel cancelButtons = new JPanel();
    cancelButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
    cancelButtons.add(cancelButton);

    JPanel buttons = new JPanel();
    buttons.setLayout(new BorderLayout());
    buttons.add(navButtons, BorderLayout.EAST);
    buttons.add(cancelButtons, BorderLayout.WEST);
    add(buttons, BorderLayout.SOUTH);

    range_panel = new JPanel();
    range_panel.setLayout(new GridLayout(3,2));
    black_notes_spinner = create_spinner(range_panel,"black notes",new SpinnerNumberModel(0,0,1,1));
    highest_freq_spinner = create_spinner(range_panel,"highest freq",new SpinnerNumberModel(16000,1000,96000,1000));
    base_freq_spinner = create_spinner(range_panel,"lowest freq",new SpinnerNumberModel(16,1,96,1));



    interval_panel = new JPanel();
    interval_panel.setLayout(new GridLayout(3,2));
    equal_divisions_spinner = create_spinner(interval_panel,"equal_divisions",new SpinnerNumberModel(12,1,32000,1));

    interval_panel.add(new JLabel("interval:"));
    double cents = scalec.interval_size;
    cents = cents / 65536.0;
//new_song_dialogc.find_closest_interval(cents)
    interval_combo_box = new JComboBox(interval_strings);
    interval_combo_box.setSelectedIndex(0);
    interval_combo_box.setEditable(true);
    interval_panel.add(interval_combo_box);



    generator_panel = new JPanel();
    generator_panel.setLayout(new GridLayout(3,2));
    generator_spinner = create_spinner(generator_panel,"generator",new SpinnerNumberModel(700,1,32000,1));
    period_spinner = create_spinner(generator_panel,"period",new SpinnerNumberModel(1200,1,32000,1));

    mos_panel = new JPanel();
    mos_panel.setLayout(new GridLayout(3,2));
    mos_panel.add(new JLabel("number of tones:"));
    number_of_tones_combo_box = new JComboBox();
    //number_of_tones_combo_box.addItem("12");
    mos_panel.add(number_of_tones_combo_box);
    //number_of_tones_combo_box.addActionListener(this);
    name_label = new JLabel("name of first pattern:");
    mos_panel.add(name_label);
    name_field = new JTextField("pattern");
    mos_panel.add(name_field);
    
    //mos_panel.add(new JLabel("black notes"));
    //mos_panel.add(new JTextField(""));

    current_index = 0;
    current_panel = interval_panel;
    add(current_panel, BorderLayout.CENTER);

  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    //this.getContentPane().add(button);
    return button;
  }
  JSpinner create_spinner(JPanel p,String text,SpinnerNumberModel m){
    JLabel label = new JLabel(text);
    p.add(label);
    JSpinner s = new JSpinner(m);
    p.add(s);
    return s;
  }
  int gcd(int a,int b) {
    int c = a % b;
    while (c != 0) {
      a = b;
      b = c;
      c = a % b;
    }
    return b;
  }
  int add_mos_sizes() {
    int a = generator % period;
    int b = a;int n = 1;
    if ((period-a) <= b) {b = period-a;}
    number_of_tones_combo_box.addItem(new Integer(1));    
    for (int i = 2;i <= period;i++) {
      if (b == 0) {break;}
      if (i > 255) {break;}
      a = (a + generator) % period;
      if ((period-a) <= b) {
        b = period-a;n++;
        number_of_tones_combo_box.addItem(new Integer(i));    
      } 
      if (a <= b) {
        b = a;n++;
        number_of_tones_combo_box.addItem(new Integer(i));    
      }  
    }
    return n;
  }
  boolean parse_interval(String Int_String) {
    String I_str[] = Int_String.split("/");
    double n = 2.0;
    double d = 1.0;
    if (I_str.length == 0) {return false;}
    try {
      n = Double.parseDouble(I_str[0]);
      if (I_str.length > 1) {
        d = Double.parseDouble(I_str[1]);
      }
    } catch (NumberFormatException e) {
      return false;
    }
    double cents = (Math.log(n/d)*1200.0) / Math.log(2);
    scalec.interval_size = (int) (cents*65536.0);
    numerator = n;
    denominator = d;
    return true;
  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    Number n;
    //System.out.println("action: " + action);
    //if (e.getSource() == number_of_tones_combo_box) {
    //  Integer i = (Integer) number_of_tones_combo_box.getSelectedItem();      
    //  int t = i.intValue();
    //  n = (Number) black_notes_spinner.getValue();      
    //  int bn = n.intValue();
    //  if (bn > t) {
    //    black_notes_spinner.setValue(i);
    //  }
    //  SpinnerNumberModel m = (SpinnerNumberModel) black_notes_spinner.getModel();
    //  m.setMaximum(i);
    //}
    if (action.equals("back")) {
      remove(current_panel);
      current_panel = (JPanel) previous.pop();
      add(current_panel, BorderLayout.CENTER);
      current_index = current_index - 1;
      if (previous.size() == 0) {
        backButton.setEnabled(false);
      }
      nextButton.setText("next");
      setVisible(true);
      current_panel.revalidate();
      current_panel.updateUI();
      //current_panel.setVisible(true);
    }
    if (action.equals("next")) {
      backButton.setEnabled(true);
      remove(current_panel);
      previous.push(current_panel);
      if (current_index == 0) {
        String Int_String = (String) interval_combo_box.getSelectedItem();
        if (parse_interval(Int_String) == true) {
          current_panel = generator_panel;        
          n = (Number) equal_divisions_spinner.getValue();
          period_spinner.setValue(n);
          equal_divisions = n.intValue();         
          double g = equal_divisions;
          double f0 = numerator / denominator;
          double f1 = (numerator+1.0) / (denominator+1.0);

          g = (g * Math.log(f1)) / Math.log(f0);
          int gen = (int) (g + 0.5);
          gen = gen / gcd(equal_divisions,gen);
          generator_spinner.setValue(new Integer(gen));
        } else {
          JOptionPane.showMessageDialog(this,
            Int_String + " is invalid","error",
          JOptionPane.ERROR_MESSAGE);          
          current_index = current_index - 1;
        }
      }
      if (current_index == 1) {
        n = (Number) generator_spinner.getValue();
        generator = n.intValue();
        n = (Number) period_spinner.getValue();
        period = n.intValue();
        number_of_tones_combo_box.removeAllItems();
        int ns = add_mos_sizes();
        number_of_tones_combo_box.setSelectedIndex(ns-1);
        current_panel = mos_panel;
      }
      if (current_index == 2) {
        int i = number_of_tones_combo_box.getSelectedIndex();      
        
        n = (Number) number_of_tones_combo_box.getItemAt(i);
        int i1 = n.intValue();
        if (i > 0) {
          n = (Number) number_of_tones_combo_box.getItemAt(i-1);
          int i2 = n.intValue();
          if (i2 > (i1 / 2)) {
            black_notes_spinner.setValue(i1-i2);
          } else {
            black_notes_spinner.setValue(i2);
          }
        } else {
          black_notes_spinner.setValue(0);
        }
        SpinnerNumberModel m = (SpinnerNumberModel) black_notes_spinner.getModel();
        m.setMaximum(new Integer(i1));
        current_panel = range_panel;
        nextButton.setText("finish");
      }
      if (current_index == 3) {
        n = (Number) number_of_tones_combo_box.getSelectedItem();
        main_app.notes_per_octave = n.intValue();
        scalec.period = period;
        scalec.generator = generator;
        scalec.equal_divisions = equal_divisions;
        n = (Number) base_freq_spinner.getValue();
        main_app.base_freq = n.intValue();
        double lowest = main_app.base_freq;
        n = (Number) highest_freq_spinner.getValue();
        double highest = n.intValue();
        //double number_of_keys = Math.log(highest/lowest) / Math.log(n/d);
        //main_app.num_octaves = (int) (number_of_keys+1.0);
        //number_of_keys = number_of_keys * main_app.notes_per_octave;
        //main_app.number_of_keys = (int) number_of_keys;
        int nk = scalec.get_num_total_key_notes_in_range(lowest,highest);
        main_app.number_of_keys = nk;

        n = (Number) black_notes_spinner.getValue();
        patternc.black_notes_per_octave = n.intValue();

        main_app.pattern_player.paused = true;
        main_app.new_song(main_app.notes_per_octave,name_field.getText()); 
        main_app.song_player.create_players();
    
        main_app.pattern_list_window.update_list_box();
        main_app.song_modified = false;

        main_app.main_panel.update_size();
        if (main_app.tunning_table_window.isVisible()) {
          main_app.tunning_table_window.show();
        }
        //main_panelc.setup_scale34(main_app.notes_per_octave,bn);

        result = true;         
        hide();
      }
      if (current_index < 3) {
        add(current_panel, BorderLayout.CENTER);
        current_index = current_index + 1;
        setVisible(true);
        current_panel.revalidate();
        current_panel.updateUI();
      }
      //current_panel.setVisible(true);
      //this.getContentPane().revalidate();
      //this.getContentPane().updateUI();
      //current_panel.display();

    }
    if (action.equals("cancel")) {
      result = false;         
      hide();
    }

  }
}

