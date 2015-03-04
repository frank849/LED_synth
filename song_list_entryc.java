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

public class song_list_entryc {
  song_list_entryc(String s,int k, int c,String t) {  
    this.pattern = s;
    this.mode = k;
    this.cents = c;
    this.tuning = t;
  }
  String pattern;
  int mode;
  int cents;
  String tuning;
}
