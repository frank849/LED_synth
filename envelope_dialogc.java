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

public class envelope_dialogc extends JDialog implements ActionListener {
  JSpinner spinner[];
  int num_spinners;
  envelope_dialogc(Frame owner,String title) {
    super(owner,title,false);
    setBounds(20,20,200,200);
    this.getContentPane().setLayout(new GridLayout(5,2));
    spinner = new JSpinner[20];
    add_spinner("attack time: ",new SpinnerNumberModel(0.0,0.0,3.0,0.1));    
    add_spinner("decay time: ",new SpinnerNumberModel(0.0,0.0,3.0,0.1));    
    add_spinner("sustain level: ",new SpinnerNumberModel(1.0,0.0,100.0,0.1));    
    add_spinner("release time: ",new SpinnerNumberModel(1.0,0.0,3.0,0.1));    
    create_button("ok","ok");
    create_button("cancel","cancel");
  }
  void add_spinner(String text,SpinnerNumberModel m){
    JLabel label = new JLabel(text);
    this.getContentPane().add(label);
    spinner[num_spinners] = new JSpinner(m);
    this.getContentPane().add(spinner[num_spinners]);
    num_spinners = num_spinners + 1;  
  }
  JButton create_button(String text,String action) {  
    JButton button = new JButton(text);
    button.setActionCommand(action);    
    button.addActionListener(this);
    this.getContentPane().add(button);
    return button;
  }
  public void show() {
    super.show();
    adsr_envelopec e = sampleplayerc.envelope;
    spinner[0].setValue(new Float(e.attack));
    spinner[1].setValue(new Float(e.decay));
    spinner[2].setValue(new Float(e.sustain));
    spinner[3].setValue(new Float(e.release));

  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    Number n;
    if (action.equals("ok")) {
      n = (Number) spinner[0].getValue();
      sampleplayerc.envelope.attack = n.floatValue();
      n = (Number) spinner[1].getValue();
      sampleplayerc.envelope.decay = n.floatValue();
      n = (Number) spinner[2].getValue();
      sampleplayerc.envelope.sustain = n.floatValue();
      n = (Number) spinner[3].getValue();
      sampleplayerc.envelope.release = n.floatValue();

      this.hide();
    }
    if (action.equals("cancel")) {
      this.hide();
    }
  }
}

