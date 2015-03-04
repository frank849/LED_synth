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

public class song_browserc extends JFrame implements MouseListener,ActionListener {
  JList listbox;
  //JButton playbutton;
  JScrollPane listboxscroller;
  String directory;
  DefaultListModel list;
  ZipFile zip = null;
  JPanel button_panel;

  song_browserc(String filename) {
    super("song browser");
    list = new DefaultListModel();
    listbox = new JList(list);
    listboxscroller = new JScrollPane(listbox);
    //playbutton = new JButton("play");
    //playbutton.setActionCommand("play");
    //playbutton.addActionListener(this);
    //SpringLayout spl = new SpringLayout();
    button_panel = new JPanel();
    
    button_panel.setLayout(new GridLayout(1,1));
    //this.getContentPane().setLayout(spl); 
    create_button("play","play");
    this.getContentPane().add(listboxscroller);
    this.getContentPane().add(button_panel,BorderLayout.SOUTH);    
    loadzipfile(filename);
    setBounds(20,20,300,300);
    setVisible(true);
    listbox.addMouseListener(this);
    this.pack();
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    button_panel.add(button);
    //this.getContentPane().add(button);
    return button;
  }

  void loadzipfile(String filename) {
    try {
      zip = new ZipFile(filename);
      list.clear();
      Enumeration e = zip.entries();
      while(e.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) e.nextElement();
        list.addElement(entry.getName());
      }    
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void actionPerformed(ActionEvent e) {        
    //String action = e.getActionCommand();
      loadfile();
  }
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      loadfile();
    }
  }
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  void loadfile() {
    main_app.pattern_player.paused = true;
    try {
      int i = listbox.getSelectedIndex();
      String filename = (String) list.get(i);
      ZipEntry e = zip.getEntry(filename);       
      DataInputStream infile = new DataInputStream(new BufferedInputStream(
            new  GZIPInputStream(zip.getInputStream(e))));
      main_app.open_file(infile);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    main_app.pattern_player.paused = false;
  }
}
