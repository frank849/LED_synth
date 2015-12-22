import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.event.*; 
import javax.swing.*;   
import javax.swing.table.*;
      
import java.awt.*;
import java.awt.event.*;                       

public class pattern_list_model extends AbstractListModel {
  public int getSize() { 
    return main_app.pattern_list.size();     
  }
  public Object getElementAt(int index) { 
    //patternc p = (patternc) main_app.pattern_list.get(index);
    //return p.name; 
    return null;
  }
  static void add_pattern(patternc p) {
      //int i = get_sorted_index(p.name);
      if (main_app.pattern_list.containsKey(p.name)) {
        int op = JOptionPane.showConfirmDialog(null,
        "Do you want to overwrite " + p.name + "?","overwrite pattern?",
        JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
          main_app.pattern_list.put(p.name,p);
        }    
      } else {
        main_app.pattern_list.put(p.name,p);
      }
      //update_pattern_list_ids();
  }
  static patternc create_new_pattern(Frame owner) {    
    patternc pattern = main_app.song_player.pattern; 
    int l = pattern.length;
    add_pattern_dialog d = new add_pattern_dialog(owner,"add pattern",l);
    d.show();      
    if (d.OK_Clicked()) {
      patternc p = new patternc(l,d.get_name());  
      p.copy(pattern,d.get_start_value(),d.get_end_value());
      p.copy_scale(pattern);
      add_pattern(p);
      return p;
    }
    return null;
  }
  //static void update_pattern_list_ids() {   
  //  Vector l = main_app.pattern_list;
  //  for (int i = 0;i < l.size();i++) {
  //    patternc p = (patternc) l.get(i);
  //    p.id = i;
  //  }
  //}
}

