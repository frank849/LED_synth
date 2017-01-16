import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*; 

import java.awt.*;
import java.awt.event.*; 

public class add_pattern_dialog extends JDialog implements ActionListener{
  JLabel name_label;
  JTextField name_field;
  JLabel start_label;
  JSpinner start_field;
  JLabel end_label;
  JSpinner end_field;
  //JButton ok_button;  
  //JButton cancel_button;  
  boolean result;
  int pat_len;
  long id;
  add_pattern_dialog(Frame owner,String title,int pat_len) {
    super(owner,title,true);
    setBounds(20,20,200,200);
    this.getContentPane().setLayout(new GridLayout(5,2));
    name_label = new JLabel("name:");
    this.getContentPane().add(name_label);
    id = main_app.prefs.getLong("pattern_id",0);
    name_field = new JTextField("pattern" + id);
    this.getContentPane().add(name_field);
    
    start_label = new JLabel("start:");
    this.getContentPane().add(start_label);
    start_field = new JSpinner(new SpinnerNumberModel(0,-pat_len,0,1));
    this.getContentPane().add(start_field);
        
    end_label = new JLabel("end:");
    this.getContentPane().add(end_label);
    end_field = new JSpinner(new SpinnerNumberModel(0,0,pat_len,1));
    this.getContentPane().add(end_field);
    create_button("all","all");
    create_button("none","none");
    create_button("ok","ok");
    create_button("cancel","cancel");
    this.pat_len = pat_len;
    this.setBounds(20,20,300,200);
  }
  boolean OK_Clicked() {
    return result;
  }
  String get_name() {
    return name_field.getText();
  }
  int get_start_value() {
    Number n = (Number) start_field.getValue();
    return n.intValue();
  }
  int get_end_value() {
    Number n = (Number) end_field.getValue();
    return n.intValue();
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
    if (action.equals("all")) {
      start_field.setValue(new Integer(0));
      end_field.setValue(new Integer(pat_len));
    }
    if (action.equals("none")) {
      start_field.setValue(new Integer(0));
      end_field.setValue(new Integer(0));
    }
    if (action.equals("ok")) {
      result = true;
      main_app.prefs.putLong("pattern_id",id+1);
      hide();
    }
    if (action.equals("cancel")) {
      result = false;         
      hide();
    }
  }

}

