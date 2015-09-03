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


class prime_table_modelc extends  AbstractTableModel {
  public String getColumnName(int column) {
     if (column == 0) {return "freq";}
     if (column == 1) {return "DB vol";}
     if (column == 2) {return "bandwidth";}
     if (column == 3) {return "steps";}
     return null;
  }
  public int getColumnCount() { 
    return 4;
  }
  public int getRowCount() { 
    return main_app.prime_list.num_primes;
  }
  public Object getValueAt(int row, int col) { 
    if (col == 0) {
      return new Float(main_app.prime_list.prime_factor[row]);
    }
    if (col == 1) {
      float db = main_app.prime_list.prime_factor_DB[row];
      return new Float(db / 2.0);
    }
    if (col == 2) {
      return new Float(main_app.prime_list.prime_factor_bw[row]);
    }
    if (col == 3) {
      double f1 = main_app.prime_list.prime_factor[row];
      double f2 = scalec.get_interval_fraction();
      int ed = scalec.equal_divisions;
      return new Float((Math.log(f1)*ed)/Math.log(f2));
    }
    return null;
  }
  public boolean isCellEditable(int rowIndex,int columnIndex) {
    //if (columnIndex == 0) {return true;}
    //if (columnIndex == 1) {return true;}
    //if (columnIndex == 2) {return true;}
    return true;
  }
  public void setValueAt(Object val,int row,int col) {
    //col = column_table[col];
    String str = (String) val;
    if (col == 0) {
      float f = Float.parseFloat(str);
      main_app.prime_list.prime_factor[row] = f;
    }
    if (col == 1) {
      float f = Float.parseFloat(str);
      short db = (short) ((f*2) - 0.5);
      if ((db < 0) & (db > -256)) {
        main_app.prime_list.prime_factor_DB[row] = db;      
      }
    }
    if (col == 2) {
      float f = Float.parseFloat(str);
      main_app.prime_list.prime_factor_bw[row] = f;
    }
    if (col == 3) {
      float f1 = Float.parseFloat(str);
      double f2 = scalec.get_interval_fraction();
      int ed = scalec.equal_divisions;
      float f3 = (float) Math.exp(Math.log(f2)*(f1/ed));
      main_app.prime_list.prime_factor[row] = f3;
    }
  }
}

public class prime_table_windowc extends JFrame implements ActionListener {
  JTable table;
  JPanel button_panel; 
  JButton add_button;
  JButton update_button;
  JScrollPane listboxscroller;

  prime_table_windowc() {
    table = new JTable(new prime_table_modelc());    
    //table.getSelectionModel().addListSelectionListener(this);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listboxscroller = new JScrollPane(table);
    
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(listboxscroller);

    button_panel = new JPanel();
    this.getContentPane().add(button_panel,BorderLayout.SOUTH);    

    button_panel.setLayout(new GridLayout(1,2));
    add_button = create_button("add","add");
    update_button = create_button("update","update");
    //this.getContentPane().add(update_button,BorderLayout.SOUTH);
    this.setTitle("prime harmonics");
    this.pack();
  }
  void create_new_panel() {
      table.updateUI();
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    button_panel.add(button);
    //this.getContentPane().add(button);
    return button;
  }  
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    //System.out.println(action);
    if (action.equals("add")) {
      int num_primes = main_app.prime_list.num_primes;
      if (num_primes < 50) {
        main_app.prime_list.add_prime();
      }
      table.updateUI();
    }
    if (action.equals("update")) {
      main_app.pattern_player.ins.alloc_string_tables();
      main_app.song_player.update_players();
      main_panelc.update_low_harmonics();
      main_panelc.update_harmonic_offsets();
    }

  }
  void add_row(int i) {      
    System.out.println("add_row");
  }
}


