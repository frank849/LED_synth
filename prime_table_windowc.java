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
    return pattern_list_column_modelc.getColumnName(column);
     //return null;
  }
  public int getColumnCount() { 
    return 5;
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
    double f1 = main_app.prime_list.prime_factor[row];
    double f2 = scalec.get_interval_fraction();
    if (col == 3) {
      song_playerc sp = main_app.song_player;
      int s = sp.scale.size;
      return new Float((Math.log(f1)*s)/Math.log(f2));
    }
    if (col == 4) {
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
class prime_table_column_modelc extends DefaultTableColumnModel {
  private int num_visible_columns = 5;
  static int max_columns = 5;
  public int column_table[];
  TableColumn t[];
  prime_table_column_modelc() {
    super();
    t = new TableColumn[max_columns];
    column_table = new int[max_columns];
    for (int i = 0;i < max_columns;i++) {
      int size = 100;
      //if (i == 0) {size = 300;}
      t[i] = new TableColumn(i,size);
      String s = getColumnName(i);
      t[i].setHeaderValue(s);
      addColumn(t[i]);
      column_table[i] = i;
    }
  }
  int get_num_visible_columns() {
    return num_visible_columns;
  }
  void set_num_visible_columns(int c) {
    for (int i = 0;i < max_columns;i++) {
      removeColumn(t[i]);
    }
    for (int i = 0;i < c;i++) {
      addColumn(t[column_table[i]]);
    }
    num_visible_columns = c;
  }
  public TableColumn getColumn(int columnIndex) {
    return t[column_table[columnIndex]];    
  }
  public int getColumnCount() {
    return num_visible_columns;
  }
  public boolean getResizable() {
    return true;
  }
  static public String getColumnName(int column) {
     if (column == 0) {return "freq";}
     if (column == 1) {return "DB vol";}
     if (column == 2) {return "bandwidth";}
     if (column == 3) {return "steps";}
     if (column == 4) {return "equal divisions";}
     return null;
  }
}
class prime_table_visible_columns_dialog extends JDialog implements ActionListener{
  JPanel button_panel;

  int num_checkboxes;
  JCheckBox checkbox[] = new JCheckBox[5];
  boolean result;
  prime_table_visible_columns_dialog(Frame owner,String title,
                         prime_table_column_modelc pl_column_model) {
    super(owner,title,true);
    setBounds(20,20,200,200);
    
    this.getContentPane().setLayout(new GridLayout(6,1));
    create_checkbox("freq");
    create_checkbox("db vol");
    create_checkbox("bandwidth");
    create_checkbox("steps");
    create_checkbox("equal divisions");
    //create_checkbox("tuning");

    int num_visible_columns = pl_column_model.get_num_visible_columns();
    for (int i = 0;i < num_visible_columns;i++) {
      int j = pl_column_model.column_table[i];
      checkbox[j].setSelected(true);
    }

    button_panel = new JPanel();
    this.getContentPane().add(button_panel);    
    button_panel.setLayout(new GridLayout(1,2));    
    create_button("ok","ok");
    create_button("cancel","cancel");
  }
  boolean is_visible(int i) {
    return checkbox[i].isSelected();
  }
  void create_checkbox(String text) {
    int i = num_checkboxes;
    checkbox[i] = new JCheckBox(text);
    this.getContentPane().add(checkbox[i]);
    num_checkboxes = num_checkboxes + 1;
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    button_panel.add(button);
    return button;
  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    //System.out.println(action);
    result = false;         
    if (action.equals("ok")) {
      result = true;
    }
    hide();
  }
  boolean OK_Clicked() {
    return result;
  }
}
public class prime_table_windowc extends JFrame implements ActionListener {
  JTable table;
  JPanel button_panel; 
  JButton add_button;
  JButton update_button;
  JScrollPane listboxscroller;
  JMenuBar main_menu_bar;
  prime_table_column_modelc pt_column_model;

  prime_table_windowc() {
    pt_column_model = new prime_table_column_modelc();
    table = new JTable(new prime_table_modelc(),pt_column_model);    
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
    main_menu_bar = setup_menu();
    this.setJMenuBar(main_menu_bar);   
    this.setTitle("prime harmonics");
    this.pack();
  }
  JMenuItem createMenuItem(String text,JMenu menu,String action) {
    JMenuItem mi = new JMenuItem(text);
    menu.add(mi);
    mi.setActionCommand(action);    
    mi.addActionListener(this);
    return mi;
  }
  JMenuBar setup_menu() {
    JMenuBar mb = new JMenuBar();
    JMenu hmenu = new JMenu("harmonics");
    createMenuItem("add",hmenu,"add");
    createMenuItem("remove",hmenu,"remove");
    createMenuItem("update",hmenu,"update");
    createMenuItem("visible columns",hmenu,"visible_columns");

    mb.add(hmenu);
    return mb;
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
    if (action.equals("visible_columns")) {
      prime_table_visible_columns_dialog d = new prime_table_visible_columns_dialog(this,"visible columns",pt_column_model);
      d.show();
      if (d.OK_Clicked()) {
        int max_columns = prime_table_column_modelc.max_columns;
        int j = 0;
        for (int i = 0;i < max_columns;i++) {
          if (d.is_visible(i)) {
            pt_column_model.column_table[j] = i;
            j = j + 1;
          }
        }
        pt_column_model.set_num_visible_columns(j);
        //table.resizeAndRepaint();
        table.updateUI();
      }
    }
  }
  void add_row(int i) {      
    System.out.println("add_row");
  }
}


