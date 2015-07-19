import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.event.*; 
import javax.swing.*;   
import javax.swing.table.*;
      
import java.text.*;
import java.awt.*;
import java.awt.event.*;                       
//import java.awt.datatransfer.*;

//import javax.imageio.*;
//import java.awt.image.*;
//import java.util.zip.*;

class pattern_list_options_dialog extends JDialog implements ActionListener {
  JPanel cents_step_panel;
  JSpinner cents_spinner;
  JPanel button_panel;
  JCheckBox pattern_check_box;
  JCheckBox tunning_check_box;
  boolean result;
  static double cents_step_size = 20.0;
  pattern_list_options_dialog(Frame owner) {
    super(owner,"pattern list options",true);
    this.getContentPane().setLayout(new GridLayout(4,1));

    boolean b;
    pattern_check_box = new JCheckBox("create new patterns");
    this.getContentPane().add(pattern_check_box);
    b = pattern_list_table_modelc.new_patterns;
    pattern_check_box.setSelected(b);

    tunning_check_box = new JCheckBox("create new tunnings");
    this.getContentPane().add(tunning_check_box);
    b = pattern_list_table_modelc.new_tunnings;
    tunning_check_box.setSelected(b);

    cents_step_panel = new JPanel();
    cents_step_panel.setLayout(new GridLayout(1,2));    
    this.getContentPane().add(cents_step_panel);    

    cents_step_panel.add(new JLabel("cents step:"));
    double s = cents_step_size;
    cents_spinner = new JSpinner(new SpinnerNumberModel(s,0.1,200.0,1.0));
    cents_step_panel.add(cents_spinner);

    button_panel = new JPanel();
    this.getContentPane().add(button_panel);    
    button_panel.setLayout(new GridLayout(1,2));    
    create_button("ok","ok");
    create_button("cancel","cancel");
    this.pack();
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
      boolean b;
      b = pattern_check_box.isSelected();
      pattern_list_table_modelc.new_patterns = b;
      b = tunning_check_box.isSelected();
      pattern_list_table_modelc.new_tunnings = b;
      result = true;
    }
    hide();
  }
  boolean OK_Clicked() {
    return result;
  }

}


class visible_columns_dialog extends JDialog implements ActionListener{
  JPanel button_panel;

  int num_checkboxes;
  JCheckBox checkbox[] = new JCheckBox[4];
  boolean result;
  visible_columns_dialog(Frame owner,String title,
                         pattern_list_column_modelc pl_column_model) {
    super(owner,title,true);
    setBounds(20,20,200,200);
    
    this.getContentPane().setLayout(new GridLayout(5,1));
    create_checkbox("pattern");
    create_checkbox("mode");
    create_checkbox("key");
    create_checkbox("tuning");

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
class pattern_list_dialog extends JDialog implements ActionListener{
  JList listbox;
  JPanel button_panel;
  JScrollPane listboxscroller;
  DefaultListModel list;
  boolean result;
  pattern_list_dialog(Frame owner,String title) {
    super(owner,title,true);
    setBounds(20,20,200,200);
    list = new DefaultListModel();
    listbox = new JList(list);
    
    listboxscroller = new JScrollPane(listbox);
    
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(listboxscroller);

    button_panel = new JPanel();
    this.getContentPane().add(button_panel,BorderLayout.SOUTH);    
    button_panel.setLayout(new GridLayout(1,2));    
    create_button("ok","ok");
    create_button("cancel","cancel");
    update_list_box();
  }
  void select_pattern(String name) {
    for (int i = 0;i < list.size();i++) {
      if (name.equals(list.get(i))) {
        listbox.setSelectedIndex(i);
      }
    }
  }
  String get_selected_pattern() {
    int i = listbox.getSelectedIndex();
    //patternc p = (patternc) main_app.pattern_list.get(i);
    return (String) list.get(i);
  }
  boolean OK_Clicked() {
    if (listbox.getSelectedIndex() == -1) {return false;}
    return result;
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
  void update_list_box() {
    list.clear();    
    Set s = main_app.pattern_list.keySet(); 
    Object st[] = s.toArray(); 
    Arrays.sort(st);
    for (int i = 0;i < st.length;i++) {
      String str = (String) st[i];
      patternc p = (patternc) main_app.pattern_list.get(str);
      //if (p.is_empty() == false) {
        list.addElement(str);
      //}
    }
  }
}
class pattern_list_column_modelc extends DefaultTableColumnModel {
  private int num_visible_columns = 4;
  static int max_columns = 4;
  public int column_table[];
  TableColumn t[];
  pattern_list_column_modelc() {
    super();
    t = new TableColumn[max_columns];
    column_table = new int[max_columns];
    for (int i = 0;i < max_columns;i++) {
      int size = 100;
      if (i == 0) {size = 300;}
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
    if (column == 0) {return "pattern";}
    if (column == 1) {return "mode";}
    if (column == 2) {return "key";}
    if (column == 3) {return "tuning";}
    return null;
  }
}
class pattern_list_table_modelc extends  AbstractTableModel {
  static boolean new_patterns = true;
  static boolean new_tunnings = true;
  static DecimalFormat formatter = new DecimalFormat("#.####");

  public String getColumnName(int column) {
    return pattern_list_column_modelc.getColumnName(column);
  }
  public int getColumnCount() { 
    return pattern_list_column_modelc.max_columns;
  }
  public int getRowCount() { 
    return main_app.song_list.size();
  }
  public Object getValueAt(int row, int col) { 
    //col = column_table[col];
    song_list_entryc e = (song_list_entryc) main_app.song_list.get(row); 
    if (col == 0) {return e.pattern;}
    if (col == 1) {return new Integer(e.mode);}
    if (col == 2) {
      float key = (float) scalec.cents_to_key(e.cents);
      String s = formatter.format(key);
      return s;
    }
    if (col == 3) {return e.tuning;}
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
    song_list_entryc e = (song_list_entryc) main_app.song_list.get(row);
    song_playerc song_player = main_app.song_player;
    if (col == 0) {
      patternc p = (patternc) main_app.pattern_list.get(str);
      if ((p == null) & new_patterns) {
        p = new patternc(4,str);
        main_app.pattern_list.put(str,p);
      }
      if (p != null) {
        e.pattern = str;
        song_player.pattern = p;
      }
      main_app.main_panel.repaint();
      main_app.update_status_bar();
    }
    if (col == 1) {
      e.mode = Integer.parseInt(str);
      song_player.pattern_mode = e.mode;
      main_app.tunning_table_window.update_scale();
      song_player.update_players();
      main_app.main_panel.repaint();
    }
    if (col == 2) {
      double key = Double.parseDouble(str);
      e.cents = scalec.key_to_cents(key);
      song_player.base_freq = Math.exp(Math.log(2) * (e.cents / (1200.0 * 65536.0)));    
      main_app.song_player.update_players();
      main_app.main_panel.repaint();
    }
    if (col == 3) {
      scalec s = (scalec) main_app.tuning_map.get(str);
      if ((s == null) & new_tunnings) {
        s = new scalec(main_app.notes_per_octave);
        s.init();
        main_app.tuning_map.put(str,s);
      }
      if (s != null) {
        e.tuning = str;
        song_player.scale = s;
      }
      main_app.tunning_table_window.update();
    }
    //System.out.println(val);
  }
}

public class pattern_list_windowc extends JFrame implements ActionListener,ListSelectionListener,ChangeListener {
  //JList listbox;
  JTable table;
  JPanel button_panel;
  JScrollPane listboxscroller;
  //DefaultListModel list;
  JLabel mode_spinner_label;
  JSpinner mode_spinner;  
  JLabel key_spinner_label;
  JSpinner key_spinner;  
  SpinnerNumberModel key_spinner_model;
  JButton button1;
  JButton button2;
  JButton button3;
  JButton button4;
  JMenuBar main_menu_bar;
  //pattern_list_model list;  
  pattern_list_column_modelc pl_column_model;

  pattern_list_windowc(String Title) {
    super(Title);
    setBounds(20,20,200,200);
    //list = new pattern_list_model();
    //list = new DefaultListModel();
    //listbox = new JList(list);
    //listbox.addListSelectionListener(this);
    pl_column_model = new pattern_list_column_modelc();
    table = new JTable(
         new pattern_list_table_modelc(),
         pl_column_model
    );
    table.getSelectionModel().addListSelectionListener(this);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //listbox.setActionCommand("listbox");    
    //listbox.addActionListener(this);
    
    listboxscroller = new JScrollPane(table);
    
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(listboxscroller);

    button_panel = new JPanel();
    this.getContentPane().add(button_panel,BorderLayout.SOUTH);    
    
    button_panel.setLayout(new GridLayout(4,2));
    
    mode_spinner_label = new JLabel("mode:");
    mode_spinner = new JSpinner(new SpinnerNumberModel(0, -128, 127, 1));
    mode_spinner.addChangeListener(this);
    mode_spinner.setEnabled(false);
    button_panel.add(mode_spinner_label);
    button_panel.add(mode_spinner);

    key_spinner_label = new JLabel("key:");
    key_spinner_model = new SpinnerNumberModel(0.0, -32400.0, 32400.0, 1.0);
    key_spinner = new JSpinner(key_spinner_model);
    key_spinner.addChangeListener(this);
    key_spinner.setEnabled(false);
    button_panel.add(key_spinner_label);
    button_panel.add(key_spinner);

    button1 = create_button("add","add_pattern");
    button2 = create_button("remove","remove_pattern");
    button3 = create_button("up","move_up");
    button4 = create_button("down","move_down");

    main_menu_bar = setup_menu();
    this.setJMenuBar(main_menu_bar);    
    //create_button("new pattern","new_pattern");
    //button_panel.add(button2);
    //button_panel.add(button3);
    //button_panel.add(button4);
    update_list_box();
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
    JMenu song_menu = new JMenu("song");
    createMenuItem("visible columns",song_menu,"visible_columns");
    createMenuItem("new pattern",song_menu,"new_pattern");
    createMenuItem("add pattern",song_menu,"add_pattern");
    createMenuItem("remove pattern",song_menu,"remove_pattern");
    createMenuItem("options",song_menu,"options");


    mb.add(song_menu);
    return mb;
  }
  public void stateChanged(ChangeEvent e) {
    int index = table.getSelectedRow();
    song_playerc song_player = main_app.song_player;
    song_list_entryc en = (song_list_entryc) main_app.song_list.get(index);
    if (e.getSource() == mode_spinner) {
      //System.out.println("key stateChanged");
      Number n = (Number) mode_spinner.getValue();
      en.mode = n.intValue();
      song_player.pattern_mode = en.mode;
      main_app.tunning_table_window.update_scale();
      song_player.update_players();
    }
    if (e.getSource() == key_spinner) {
      //System.out.println("cents stateChanged");
      Number n = (Number) key_spinner.getValue();
      en.cents = scalec.key_to_cents(n.doubleValue());
      song_player.base_freq = Math.exp(Math.log(2) * (en.cents / (1200.0*65536.0)));    
      song_player.update_players();
    }
    main_app.main_panel.repaint();
    //System.out.println("up UI 1");
    //table.updateUI();
    table.repaint();
    //System.out.println("up UI 2");
    //System.out.println("n: " + n.intValue());
  }
  void play_last_pattern() {
    int s = main_app.song_list.size();
    table.changeSelection(s-1,0,false,false);
  }
  void play_next_pattern() {
    int i = table.getSelectedRow();
    //int s = list.getSize();
    int s = main_app.song_list.size();
    i = (i + 1) % s;
    //System.out.println("de0");
    table.changeSelection(i,0,false,false);
    //System.out.println("de6");    
  }
  void add_to_song_list(String s) {
    //int i = listbox.getSelectedIndex()+1;
    int i = table.getSelectedRow()+1;
    song_list_entryc en;
    if (i == 0) {
      en = new song_list_entryc(s,0,0,"t");
    } else {
      en = (song_list_entryc) main_app.song_list.get(i-1);
    //System.out.println("index: " + i);
      en = new song_list_entryc(s,en.mode,en.cents,en.tuning);
    }
    main_app.song_list.add(i,en);
    //list.add(i,p.name);    
    table.updateUI();
  }
  void update_list_box() {
  //  list.clear();    
  //  for (int i = 0;i < main_app.song_list.size();i++) {
  //    patternc p = (patternc) main_app.song_list.get(i);
  //    list.addElement(p.name);
  //  }
  //  if (list.size() > 0) {
  //    listbox.setSelectedIndex(0);
  //  }
    table.updateUI();
  }
  void update_pattern_name(String new_name) {

     String old_name = main_app.song_player.pattern.name;
     for (int i2 = 0;i2 < main_app.song_list.size();i2++) {
       song_list_entryc en = (song_list_entryc) main_app.song_list.get(i2);
       if (en.pattern.equals(old_name)) {
         en.pattern = new_name;
       }
     }
     patternc p = (patternc) main_app.pattern_list.get(old_name);
     main_app.pattern_list.remove(old_name);
     main_app.pattern_list.put(new_name,p);

  //    int i = listbox.getSelectedIndex();
  //    patternc p = (patternc) main_app.song_list.get(i);
  //    for (int i2 = 0;i2 < main_app.song_list.size();i2++) {
  //      patternc p2 = (patternc) main_app.song_list.get(i2);
  //      if (p == p2) {
	//  list.setElementAt(p.name,i2);
  //      }
   //   }
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
    //if (action.equals("new_pattern")) {
    //  patternc p = pattern_list_model.create_new_pattern(this);
    //  if (p != null) {
    //    add_to_song_list(p);
    //  }
    //}
    if (action.equals("options")) {
      Number n;
      pattern_list_options_dialog d = new pattern_list_options_dialog(this);
      d.show();
      //if (d.OK_Clicked()) {
      //  n = (Number) d.cents_spinner.getValue();
      //  double i = n.doubleValue();
      //  cents_spinner_model.setStepSize(i);
      //  pattern_list_options_dialog.cents_step_size = (double) i;
      //}
    }

    if (action.equals("visible_columns")) {
      visible_columns_dialog d = new visible_columns_dialog(this,"visible columns",pl_column_model);
      d.show();
      if (d.OK_Clicked()) {
        int max_columns = pattern_list_column_modelc.max_columns;
        int j = 0;
        for (int i = 0;i < max_columns;i++) {
          if (d.is_visible(i)) {
            pl_column_model.column_table[j] = i;
            j = j + 1;
          }
        }
        pl_column_model.set_num_visible_columns(j);
        //table.resizeAndRepaint();
        table.updateUI();
      }
    }
    if (action.equals("add_pattern")) {
      pattern_list_dialog d = new pattern_list_dialog(this,"add pattern");
      //d.listbox.setSelectedIndex(main_app.pattern.id);
      d.select_pattern(main_app.song_player.pattern.name);
      d.show();
      if (d.OK_Clicked()) {
        String p = d.get_selected_pattern();
        add_to_song_list(p);
        //add_to_song_list(main_app.pattern.name);
      }
    }
    if (action.equals("remove_pattern")) {
      //int i = listbox.getSelectedIndex();  
      int i = table.getSelectedRow();
      if ((i >= 0) & (main_app.song_list.size() > 0)) {
        main_app.song_list.remove(i);
	//list.remove(i);
        table.updateUI();
        listboxscroller.repaint();
      }
    }
    if (action.equals("move_up")) {
      //int i = listbox.getSelectedIndex();  
      int i = table.getSelectedRow();
      if (i >= 1) {
        //Object tmp = list.get(i);
        //list.set(i,list.get(i-1));
        //list.set(i-1,tmp);
        Vector list2 = main_app.song_list;
	Object tmp = list2.get(i);
        list2.set(i,list2.get(i-1));
        list2.set(i-1,tmp);
        //listbox.setSelectedIndex(i-1);
        table.changeSelection(i-1,0,false,false);
      }
    }
    if (action.equals("move_down")) {
      //int i = listbox.getSelectedIndex();  
      int i = table.getSelectedRow();
      Vector list2 = main_app.song_list;
      if (i < (list2.size()-1)) {
        //Object tmp = list.get(i);
        //list.set(i,list.get(i+1));
        //list.set(i+1,tmp);
	Object tmp = list2.get(i);
        list2.set(i,list2.get(i+1));
        list2.set(i+1,tmp);      
        //listbox.setSelectedIndex(i+1);
        table.changeSelection(i+1,0,false,false);
      }
    }
  }
  public void valueChanged(ListSelectionEvent e) {
    //System.out.println("valueChanged " + "," + e.getFirstIndex() + "," + e.getLastIndex()
    //+ "," + e.getValueIsAdjusting());
    //int index = e.getFirstIndex();
    //System.out.println("first index " + index);
    //index = e.getLastIndex();
    //System.out.println("last index " + index);
    //if ((e.getValueIsAdjusting() == false) & (main_app.song_list.size() > 0)) {
      //int index = listbox.getSelectedIndex();  
      //System.out.println("de1");
      int index = table.getSelectedRow();
      main_panelc.song_pos = index;
      if (index >= 0) {
        song_list_entryc en = (song_list_entryc) main_app.song_list.get(index);
        main_app.song_player.play_pattern(index);
        main_app.tunning_table_window.update();

        //System.out.println("f: " + f);
        //System.out.println("de2");
	mode_spinner.setEnabled(true);
        mode_spinner.setValue(new Integer(en.mode));
        //System.out.println("de3");

	key_spinner.setEnabled(true);
        double key = scalec.cents_to_key(en.cents);
        key_spinner.setValue(new Double(key));
        //System.out.println("de4");
	main_app.main_panel.repaint();
        main_app.song_player.update_players();
        main_app.update_status_bar();
        //System.out.println("de5");

      }
      //System.out.println("base_freq: " + sampleplayerc.base_freq);
    //}
  } 
}

