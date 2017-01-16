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


public class add_random_patterns_dialog extends JDialog implements ActionListener{
  JSpinner mode_length_spinner;
  JSpinner key_length_spinner;
  JSpinner num_patterns_spinner;
  int num_patterns;
  int mode_length;
  int key_length;
  add_random_patterns_dialog(Frame owner) {
    super(owner,"add random patterns",false);
    this.getContentPane().setLayout(new GridLayout(4,2));
    int nm = main_app.notes_per_octave;
    //add_spinner("mode_offset",new SpinnerNumberModel(0,0,nm,1));
    //add_spinner("num_modes",new SpinnerNumberModel(nm,1,nm,1));

    int ed = scalec.equal_divisions;
    //add_spinner("key_offset",new SpinnerNumberModel(0,0,ed,1));
    //add_spinner("num_keys",new SpinnerNumberModel(ed,1,ed,1));

    mode_length_spinner = add_spinner("mode length",new SpinnerNumberModel(1,1,1500,1));
    key_length_spinner = add_spinner("key length",new SpinnerNumberModel(1,1,1500,1));
    num_patterns_spinner = add_spinner("num_patterns",new SpinnerNumberModel(10,1,1500,1));
    create_button("ok","ok");
    create_button("cancel","cancel");
    this.pack();
  }
  void add_random_patterns() {
    Random rand = new Random();
    Number n = (Number) num_patterns_spinner.getValue();
    num_patterns = n.intValue();
    n = (Number) mode_length_spinner.getValue();
    mode_length = n.intValue();
    n = (Number) key_length_spinner.getValue();
    key_length = n.intValue();

    Set s = main_app.pattern_list.keySet(); 
    Object pnames[] = s.toArray();
    int psize = s.size();

    s = main_app.tuning_map.keySet(); 
    Object tnames[] = s.toArray();
    int tsize = s.size();
    int os = main_app.song_player.scale.interval_size;
    int notes_per_octave = main_app.notes_per_octave;
    int mode = 0;
    int key = 0;
    for (int i = 0;i < num_patterns;i++) {
        String pname = (String) pnames[rand.nextInt(psize)];
        String tname = (String) tnames[rand.nextInt(tsize)];
        if ((i % mode_length) == 0) {
          mode = rand.nextInt(notes_per_octave); 
        }
        int ed = scalec.equal_divisions;
        if ((i % key_length) == 0) {
          key = rand.nextInt(ed);
        }
        double k = key;
        double c2 = mode;
        c2 = (c2 / notes_per_octave) + (k / ed);
        if (c2 > 1.0) {
          k = k - ed;
        }
        int c = scalec.key_to_cents(k);
        main_app.song_list.add(new song_list_entryc(pname,mode,c,tname));
    }
    main_app.pattern_list_window.update_list_box();

  }
  public void actionPerformed(ActionEvent e) {        
    Number n;
    String action = e.getActionCommand();
    if (action.equals("ok")) {
      //n = (Number) num_patterns_spinner.getValue();
      //num_patterns = n.intValue();
      add_random_patterns();
      this.hide();
    }
    if (action.equals("cancel")) {
      num_patterns = 0;
      this.hide();
    }

  }

  JSpinner add_spinner(String text,SpinnerNumberModel m){
    JLabel label = new JLabel(text);
    this.getContentPane().add(label);
    JSpinner sp = new JSpinner(m);
    this.getContentPane().add(sp);
    return sp;
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    this.getContentPane().add(button);
    return button;
  }
}

