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
  Preferences prefs;

  new_song_wizardc(Frame owner,String title) {
    super(owner,title,true);
    setBounds(20,20,200,200);

    prefs = main_app.prefs;
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
    int highest = prefs.getInt("highest_freq",16000);
    int lowest = prefs.getInt("lowest_freq",16);

    highest_freq_spinner = create_spinner(range_panel,"highest freq",new SpinnerNumberModel(highest,1000,96000,1000));
    base_freq_spinner = create_spinner(range_panel,"lowest freq",new SpinnerNumberModel(lowest,1,96,1));



    interval_panel = new JPanel();
    interval_panel.setLayout(new GridLayout(3,2));
    int ed = prefs.getInt("equal_divisions",12);
    equal_divisions_spinner = create_spinner(interval_panel,"equal_divisions",new SpinnerNumberModel(ed,1,32000,1));

    interval_panel.add(new JLabel("interval:"));
//    double cents = scalec.interval_size;
//    cents = cents / 65536.0;
//new_song_dialogc.find_closest_interval(cents)
    interval_combo_box = new JComboBox(interval_strings);
    interval_combo_box.setEditable(true);
    int i = prefs.getInt("interval_combo_box_index",0);
    if (i == -1) {
      ComboBoxEditor int_ed = interval_combo_box.getEditor();
      String int_str = prefs.get("interval_str","2/1");
      int_ed.setItem(int_str);
      System.out.println(int_str);
    } else {
      interval_combo_box.setSelectedIndex(i);
    }

    //numerator = prefs.getDouble("numerator",2);
    //denominator = prefs.getDouble("denominator",1);
    //String int_str = numerator + "/" + denominator;

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

    String fp_name = prefs.get("first_pattern_name","pattern");
    name_field = new JTextField(fp_name);
    mos_panel.add(name_field);
    
    //mos_panel.add(new JLabel("black notes"));
    //mos_panel.add(new JTextField(""));

    go_back_to_first_panel();
    current_index = 0;

  }
  void go_back_to_first_panel() {
    previous.clear();
    current_panel = interval_panel;
    add(current_panel, BorderLayout.CENTER);
    backButton.setEnabled(false);
    nextButton.setText("next");

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
    int b = a;
    int n = 1;
    int b2 = period-a;
    number_of_tones_combo_box.addItem(new Integer(1));    
    for (int i = 2;i <= period;i++) {
      if (b == 0) {break;}
      if (i > 255) {break;}
      a = (a + generator) % period;
      if ((period-a) <= b2) {
        b2 = period-a;n++;
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
          if (equal_divisions != n.intValue()) {
            equal_divisions = n.intValue();         
            period_spinner.setValue(n);
            double g = equal_divisions;
            double f0 = numerator / denominator;
            double f1 = (numerator+1.0) / (denominator+1.0);

            g = (g * Math.log(f1)) / Math.log(f0);
            int gen = (int) (g + 0.5);
            gen = gen / gcd(equal_divisions,gen);
            int ed = prefs.getInt("equal_divisions",12);
            if (ed == equal_divisions) { 
              gen = prefs.getInt("generator",gen);
              int period = prefs.getInt("period",ed);
              period_spinner.setValue(new Integer(period));
            } //else {
            //  prefs.putInt("equal_divisions",equal_divisions);
            //}
            generator_spinner.setValue(new Integer(gen));
          }
        } else {
          JOptionPane.showMessageDialog(this,
            Int_String + " is invalid","error",
          JOptionPane.ERROR_MESSAGE);          
          current_index = current_index - 1;
        }
      }
      if (current_index == 1) {
        n = (Number) generator_spinner.getValue();
        int g = n.intValue();
        n = (Number) period_spinner.getValue();
        int p = n.intValue();
        if ((generator != g) | (period != p)) { 
          generator = g;
          period = p;
          number_of_tones_combo_box.removeAllItems();
          int ns = add_mos_sizes();
          number_of_tones_combo_box.setSelectedIndex(ns-1);
          generator = prefs.getInt("generator",g);
          period = prefs.getInt("period",p);
          if ((generator == g) & (period == p)) {
            int i = prefs.getInt("number_of_tones_index",ns-1);
            number_of_tones_combo_box.setSelectedIndex(i);
          }
          generator = g;
          period = p;
        }
        current_panel = mos_panel;
      }
      if (current_index == 2) {
        int i = number_of_tones_combo_box.getSelectedIndex();      
        
        n = (Number) number_of_tones_combo_box.getItemAt(i);
        int i1 = n.intValue();
        int bn = 0;
        if (i > 0) {
          n = (Number) number_of_tones_combo_box.getItemAt(i-1);
          int i2 = n.intValue();
          if (i2 > (i1 / 2)) {
            bn = i1-i2;
          } else {
            bn = i2;
          }
        }
        int g = prefs.getInt("generator",-1);
        int p = prefs.getInt("period",-1);
        int i3 = prefs.getInt("number_of_tones_index",-1);
        if ((g == generator) & (p == period) & (i3 == i)) {
           bn = prefs.getInt("black_notes_per_octave",bn);
        }  
        black_notes_spinner.setValue(bn);
                
        SpinnerNumberModel m = (SpinnerNumberModel) black_notes_spinner.getModel();
        m.setMaximum(new Integer(i1));
        current_panel = range_panel;
        nextButton.setText("finish");
      }
      if (current_index == 3) {
        n = (Number) number_of_tones_combo_box.getSelectedItem();
        main_app.notes_per_octave = n.intValue();
        prefs.putInt("notes_per_octave",n.intValue());
        int i = number_of_tones_combo_box.getSelectedIndex();      
        prefs.putInt("number_of_tones_index",i);
        scalec.period = period;
        prefs.putInt("period",period);
        scalec.generator = generator;
        prefs.putInt("generator",generator);
        scalec.equal_divisions = equal_divisions;
        prefs.putInt("equal_divisions",equal_divisions);
        i = interval_combo_box.getSelectedIndex();
        prefs.putInt("interval_combo_box_index",i);
        String Int_String = (String) interval_combo_box.getSelectedItem();
        prefs.put("interval_str",Int_String);

        double cents = (Math.log(numerator/denominator)*1200.0) / Math.log(2);
        scalec.interval_size = (int) (cents*65536.0);
        prefs.putInt("scalec_interval_size",scalec.interval_size); 
        n = (Number) base_freq_spinner.getValue();
        main_app.base_freq = n.intValue();
        int lowest = main_app.base_freq;
        n = (Number) highest_freq_spinner.getValue();
        int highest = n.intValue();
        //double number_of_keys = Math.log(highest/lowest) / Math.log(n/d);
        //main_app.num_octaves = (int) (number_of_keys+1.0);
        //number_of_keys = number_of_keys * main_app.notes_per_octave;
        //main_app.number_of_keys = (int) number_of_keys;
        prefs.putInt("lowest_freq",lowest);
        prefs.putInt("highest_freq",highest);
        int nk = scalec.get_num_total_key_notes_in_range(lowest,highest);
        main_app.number_of_keys = nk;
        prefs.putInt("number_of_keys",main_app.number_of_keys);

        n = (Number) black_notes_spinner.getValue();
        patternc.black_notes_per_octave = n.intValue();
        prefs.putInt("black_notes_per_octave",n.intValue());


        main_app.pattern_player.paused = true;
        prefs.put("first_pattern_name",name_field.getText());

        main_app.new_song();
        //main_app.new_song(main_app.notes_per_octave,name_field.getText()); 
        main_app.song_player.create_players();
    
        main_app.pattern_list_window.update_list_box();
        main_app.song_modified = false;

        main_app.main_panel.update_size();
        if (main_app.tunning_table_window.isVisible()) {
          main_app.tunning_table_window.show();
        }
        //main_panelc.setup_scale34(main_app.notes_per_octave,bn);

        result = true;         
        go_back_to_first_panel();
        hide();
      }
      if (current_index < 3) {
        add(current_panel, BorderLayout.CENTER);
        current_index = current_index + 1;
        setVisible(true);
        current_panel.revalidate();
        current_panel.updateUI();
      } else {
        current_index = 0;
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

