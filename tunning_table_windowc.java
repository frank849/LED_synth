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

public class tunning_table_windowc extends JFrame implements ChangeListener {
  JPanel panel;
  JScrollPane panel2 = null;
  JLabel label[];
  JCheckBox check_box[];
  JSpinner spinner[];
  static double step_size = 0.1;
  tunning_table_windowc() {
    setBounds(20,20,200,600);
    this.setTitle("tunning table");
    String v = main_app.prefs.get("tunning_table_step_size","1");
    step_size = Double.parseDouble(v);

  }
  void create_new_panel() {
    song_playerc sp = main_app.song_player;
    if (panel2 != null) {
      this.getContentPane().remove(panel2);
    }
    int notes_per_octave = main_app.notes_per_octave;
    panel = new JPanel();
    panel2 = new JScrollPane(panel);    
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();    
    panel.setLayout(gridbag);
    //panel.setLayout(new GridLayout(notes_per_octave,1));    
    //label = new JLabel[notes_per_octave];
    check_box = new JCheckBox[notes_per_octave];
    spinner = new JSpinner[notes_per_octave];
    for (int i = notes_per_octave-1;i >= 0;i--) {
      check_box[i] = new JCheckBox();
      if (main_panelc.get_scale34(i) == 1) {check_box[i].setSelected(true);}
      check_box[i].addChangeListener(this);

      c.fill = GridBagConstraints.BOTH;
      c.weightx = 0.0;
      c.weighty = 1.0;
      c.gridwidth = 1;
      c.gridheight = 1;
      gridbag.setConstraints(check_box[i],c);
      panel.add(check_box[i]);    
      
      double m = (32400.0 * scalec.equal_divisions * (1 << 16));
      m = m / scalec.get_interval_size();
      //System.out.println(m);
      //System.out.println(sp.scale.get(i));
      spinner[i] = new JSpinner(new SpinnerNumberModel(sp.scale.get(i), -m, m, step_size));
      //label[i] = new JLabel(i + ":");
      spinner[i].addChangeListener(this);
      //panel.add(label[i]);
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1.0;
      c.weighty = 1.0;
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.gridheight = 1;
      gridbag.setConstraints(spinner[i],c);
      panel.add(spinner[i]);    
    }
    this.getContentPane().add(panel2);
  }
  void update_step_size() {
    int n = main_app.notes_per_octave;
    for (int i = 0;i < n;i++) {
      SpinnerNumberModel m = (SpinnerNumberModel) spinner[i].getModel();
      m.setStepSize(step_size);
    }    
  }
  void update() {
    int n = main_app.notes_per_octave;
    
    for (int i = 0;i < n;i++) {
      if (main_panelc.get_scale34(i) == 1) {
        check_box[i].setSelected(true);
      } else {
        check_box[i].setSelected(false);
      }
    }
    song_playerc sp = main_app.song_player;
    for (int i = 0;i < n;i++) {
      Double s = new Double(sp.scale.get(i));
      spinner[i].setValue(s);
    }    
  }
  void update_scale() {
    song_playerc sp = main_app.song_player;
    int pk = sp.pattern_mode;
    int n = main_app.notes_per_octave;
    //int f23 = main_app.get_3fifth();
    int f23 = (int) (scalec.generator + 0.5);

    for (int i = 0;i < n;i++) {
        Number num = (Number) spinner[i].getValue();
        sp.scale.set(i,num.doubleValue());
        //int i2 = (i+pk+n-f23+1)%n;
	//if (i2 < 0) {i2 = i2 + n;}
	//if (main_panelc.scale34[i2] == 1) {
        //  label[i].setText(i + "*:");
        //} else {
        //  label[i].setText(i + ":");
        //}
	//System.out.print(main_app.scale[i]);
        //System.out.print(" ");
    }
    //System.out.println();
  }

  public void stateChanged(ChangeEvent e) {
    int notes_per_octave = main_app.notes_per_octave;
    for (int i = 0;i < notes_per_octave;i++) {
      if (e.getSource() == check_box[i]) {
        if (check_box[i].isSelected() == true) {
          main_panelc.set_scale34(i,1);
        } else {
          main_panelc.set_scale34(i,0);
        }
        main_app.main_panel.repaint();
      }
      if (e.getSource() == spinner[i]) {
        song_playerc sp = main_app.song_player;
        Number n = (Number) spinner[i].getValue();
        sp.scale.set(i,n.doubleValue());
        //System.out.println("sp " + i + ": " + n.intValue());
        sp.update_players_note(i);
      }
    }

  }
}

