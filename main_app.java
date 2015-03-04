import java.lang.*;
import java.util.*;
import java.io.*;

import javax.swing.event.*; 
import javax.swing.*;   
import javax.swing.table.*;
import javax.swing.filechooser.*;
      
import java.awt.*;
import java.awt.event.*;                       
import java.awt.datatransfer.*;

import javax.imageio.*;
import java.awt.image.*;
import java.util.zip.*;
import java.util.prefs.Preferences;

public class main_app implements Runnable,ActionListener {
  static JFrame main_frame;
  static pattern_list_windowc pattern_list_window;
  static main_panelc main_panel;
  static harmonic_colors_panelc harmonic_colors_panel;
  static JFrame harmonic_colors_window;
  static hex_keyboard_panelc hex_keyboard_panel;
  //static equalizer_windowc equalizer_window;
  static bass_treble_windowc bass_treble_window;
  static JFrame hex_keyboard_window;
  static JMenuBar main_menu_bar;
  static song_playerc song_player;
  static int sqsize = 32;
  static int notes_per_octave = 12;
  static int number_of_keys = 120;
  //static int num_octaves = 10;
  //static int octave_cents = 1200;
  static float tempo = 60.0f;
  //static int scale[];
  //static int octave_offset = 0;
  static int base_freq = 16;
  static int wolf_fifth_cents = 0;
  static pattern_playerc pattern_player;
  static int ID_NUM = 244368531;
  static int pan_note = 2;
  static JLabel status_bar;
  static boolean song_modified = false;
  static HashMap pattern_list;  
  static HashMap tuning_map;  

  static Vector song_list;
  static int PATTERN_MODE = 0;
  static int SONG_MODE = 1;
  static int NOTE_MODE = 2;
  static int play_mode = SONG_MODE;
  static options_dialogc options_dialog;
  //static new_song_dialogc new_song_dialog;
  static new_song_wizardc new_song_wizard;
  static JScrollPane main_panel_scroller; 
  static wave_writerc wave_writer;
  static tunning_table_windowc tunning_table_window;
  static string_table_options_dialogc string_table_options_dialog;
  static TextTransferc TextTransfer;
  static Preferences prefs;

  static export_wave_dialogc export_wave_dialog;
  static envelope_dialogc envelope_dialog;
  static short primes_table75[];
  //static short primes_table75[] = {2};
  //static short primes_table75[] = 
  //{2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97};
  static prime_listc prime_list; //= new prime_listc(9);
  static prime_table_windowc prime_table_window = null;
  //static int harmonic_pos[] = new int[4];
  static create_temperament_dialog CT_dialog;

  static double get_centsf(float f) {
    return (((Math.log(f) / Math.log(2)) * 1200)+0.5);
  }
  static int get_num_steps_from_cents(int c) {
    double f1 = c;    
    f1 = f1 * main_app.notes_per_octave;
    double f2 = song_player.scale.interval_size;
    double f3 = f1/f2;  
    if (f3 < 0) {
      return (int) (f3-0.5);
    }
    return (int) (f3+0.5);
  }
  
  void display_song_length() {
    int total_length = 0;
    for (int i = 0;i < song_list.size();i++) {
      song_list_entryc e = (song_list_entryc) song_list.get(i);
      patternc pat = (patternc) pattern_list.get(e.pattern);
      total_length = total_length + pat.length;
    }
    float f = total_length;
    f = (f / tempo) * 60.0f;
    
    int minutes = (int) (f / 60);
    int seconds = (((int) f ) % 60);
    JOptionPane.showMessageDialog(main_frame,"this song is " + minutes + " minutes and " + seconds + " seconds  long","info",JOptionPane.INFORMATION_MESSAGE);
  }
  void add_random_patterns() {
    String msg = "how many patterns to add?";
    String number_string = JOptionPane.showInputDialog(main_frame,msg,"10");
    if (number_string == null) {return;}
    Random rand = new Random();
    try {
      int n = Integer.parseInt(number_string);
      Set s = pattern_list.keySet(); 
      Object pnames[] = s.toArray();
      int psize = s.size();

      s = tuning_map.keySet(); 
      Object tnames[] = s.toArray();
      int tsize = s.size();

      int os = song_player.scale.interval_size;
      for (int i = 0;i < n;i++) {
        String pname = (String) pnames[rand.nextInt(psize)];
        String tname = (String) tnames[rand.nextInt(tsize)];
        int k = rand.nextInt(notes_per_octave); 
        k = k - (notes_per_octave >> 1);
        int c = (-k * os) / notes_per_octave;
        c = c + rand.nextInt(1200)-600;
        song_list.add(new song_list_entryc(pname,k,c,tname));
      }
    } catch (java.lang.NumberFormatException e) {
      JOptionPane.showMessageDialog(main_frame,
      "invalid number","error",
      JOptionPane.ERROR_MESSAGE);
    }
    pattern_list_window.update_list_box();
    //pattern_list_window.update_list_box();
  }
  public void actionPerformed(ActionEvent e) {        
    String action = e.getActionCommand();
    patternc pattern = song_player.pattern;
    if (action.equals("menu_new")) {
      if (main_app.song_modified == true) {
        main_app.save_song_prompt();
      }
      //new_song_dialog.show();
      new_song_wizard.show();
    }
    if (action.equals("edit_harmonic_colors")) {
      harmonic_colors_window.show();
    }
    if (action.equals("menu_open_wave")) {
      load_instrument_as_wave();
    }
    if (action.equals("menu_open")) {
      open2();
    }
    if (action.equals("menu_save")) {
      save2();
    }
    if (action.equals("menu_export_wave")) {
      export_wave();
    }
    if (action.equals("menu_options")) {
      options_dialog.show();
    }
    if (action.equals("menu_exit")) {
      exit2();
    }
    if (action.equals("str_table_options")) {
      string_table_options_dialog.show();
    }

    if (action.equals("menu_add_pattern")) {
      patternc p = pattern_list_model.create_new_pattern(main_frame);      
      if (p != null) {
        pattern_list_window.add_to_song_list(p.name);
      }
    }

    if (action.equals("double_length")) {
      pattern.double_length();
      main_panel.repaint();
      main_app.update_status_bar();
    }
    if (action.equals("triple_length")) {
      pattern.triple_length();
      main_panel.repaint();
      main_app.update_status_bar();
    }
    if (action.equals("transpose_fifth_up")) {
      JOptionPane.showMessageDialog(main_frame,
      "transpose_fifth_up is obsolete",
      "error",
      JOptionPane.ERROR_MESSAGE);
      //pattern.transpose_fifth_up();
      main_panel.repaint();
    }
    if (action.equals("transpose_fourth_down")) {
      JOptionPane.showMessageDialog(main_frame,
      "transpose_fourth_down is obsolete",
      "error",
      JOptionPane.ERROR_MESSAGE);
      //pattern.transpose_fourth_down();
      main_panel.repaint();
    }
    if (action.equals("menu_pattern_list")) {
      pattern_list_window.show();
    }
    if (action.equals("menu_copy")) {
      String str = pattern.write_to_string();
      TextTransfer.setClipboardContents(str);
    }
    if (action.equals("menu_paste")) {
      String str = TextTransfer.getClipboardContents();
      patternc p = patternc.read_pattern_from_string(str);
      if (p != null) {
        pattern_list_model.add_pattern(p);
        pattern_list_window.add_to_song_list(p.name);
        pattern = p;
      }
    }
    //if (action.equals("add_random_patterns")) {
    //  add_random_patterns();      
    //}
    if (action.equals("menu_rename")) {
      String msg = "enter a new name for the pattern";
      String new_string = JOptionPane.showInputDialog(main_frame,msg,pattern.name);
      if (new_string != null) {
        pattern_list_window.update_pattern_name(new_string);
        pattern.name = new_string;
      }
      
    }
    if (action.equals("edit_envelope")) {
      envelope_dialog.show();
    }
    if (action.equals("bass_treble")) {
      if (bass_treble_window == null) {
        bass_treble_window = new bass_treble_windowc();
      }
      bass_treble_window.show();
    }
    //if (action.equals("equalizer")) {
    //  if (equalizer_window == null) {
    //    equalizer_window = new equalizer_windowc();
    //  }
    //  equalizer_window.show();
    //}
    if (action.equals("show_hex_keyboard")) {
      hex_keyboard_window.show();
    }
    if (action.equals("get_song_length")) {
      display_song_length();
    }
    if (action.equals("add_random_patterns")) {
      add_random_patterns();
    }
    if (action.equals("view_prime_table")) {
      if (prime_table_window == null) {
        prime_table_window = new prime_table_windowc();
      }
      prime_table_window.show();
    }
    if (action.equals("view_tunning_table")) {
      tunning_table_window.show();
    }
    if (action.equals("create_temperament")) {
      create_temperament_dialog d = new create_temperament_dialog(main_frame,"create temperament");
      d.show();
      if (d.OK_Clicked()) {
        //double cents = d.get_offset();
        //double generator = d.get_generator_cents();
        //int octave_cents = main_app.octave_cents;
        //double ed = scalec.equal_divisions;
        //Double s[] = new Double[notes_per_octave];
        //for (int i = 0;i < notes_per_octave;i++) {
        //  s[i] = new Double( cents);
        //  cents = cents + generator;
        //  if (cents > ed) {cents = cents - ed;}
        //}
        //Arrays.sort(s);
        //for (int i = 0;i < notes_per_octave;i++) {
        //  tunning_table_window.spinner[i].setValue(s[i]);
        //}
        song_player.scale.init();
        tunning_table_window.update();
        //System.out.println("OK_Clicked");
      }
    }
    if (action.equals("generate_mean_tone_scale")) {
      JOptionPane.showMessageDialog(main_frame,
    "generate_mean_tone_scale is obsolete",
    "error",
      JOptionPane.ERROR_MESSAGE);
    }
    /*
    if (action.equals("generate_mean_tone_scale")) {
      generate_mean_tone_scale_dialog d = new generate_mean_tone_scale_dialog(main_frame,"generate mean tone scale");
      d.show();
      if (d.OK_Clicked()) {
        //int octave_cents = main_app.octave_cents;
        int start = d.get_start_note();
        int step = d.get_step_value();
        double interval = d.get_interval();
        
        byte a[] = new byte[notes_per_octave];
        int i = start;
        double cents = song_player.scale.get(start);
        while (a[i] != 32) {
          a[i] = 32;
          tunning_table_window.spinner[i].setValue(new Integer((int) cents));
          cents = cents + interval;
          if (cents > octave_cents) {cents = cents - octave_cents;}
          i = (i + step) % notes_per_octave;
        }
      }
    }
    */  
    if (action.equals("import_scala")) {
      import_scala();
    }
    if (action.equals("export_scala")) {
      export_scala();
    }
  }
  static void import_scala() {
      try{
        FileDialog f = new FileDialog(main_frame,"import scala",FileDialog.LOAD);
        f.show();
        if (f.getFile() != null) {
          String filename = f.getDirectory() + f.getFile();
          scl_file sf = new scl_file();
          sf.read_table(filename,song_player.scale);
          tunning_table_window.update();
        }
      } catch (Exception err) {
        System.out.println("menusave error");
        System.out.println(err.getMessage());
        err.printStackTrace();
      }
  }
  static void export_scala() {
      try{
        FileDialog f = new FileDialog(main_frame,"export scala",FileDialog.SAVE);
        f.show();
        if (f.getFile() != null) {
          String title = JOptionPane.showInputDialog("Enter a title for this scale");
          if (title != null) {
            File f2 = new File(f.getDirectory() + f.getFile());
            if (f2.exists() == false) {
              scl_file sf = new scl_file();
              sf.write_table(f.getDirectory(),f.getFile(),title,song_player.scale);
              tunning_table_window.update();
            } else {
              JOptionPane.showMessageDialog(main_frame,new JLabel("file already exists"),"error",JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      } catch (Exception err) {
        System.out.println("menusave error");
        System.out.println(err.getMessage());
        err.printStackTrace();
      }
  }
  static void export_wave() {
      try{
        FileDialog f = new FileDialog(main_frame,"export to wave",FileDialog.SAVE);
        f.setDirectory(main_panelc.dirname);
        f.setFile("output90613708930439146539.wav");
        f.show();
        if (f.getFile() != null) {
          String filename = f.getDirectory() + f.getFile();
          //pattern_list_window.table.changeSelection(0,0,false,false);
          boolean b = wave_writerc.read_wave_id(new File(filename));
          if (b == true) {
            if (export_wave_dialog == null) {
              export_wave_dialog = new export_wave_dialogc(main_frame);
            }
            export_wave_dialogc d = export_wave_dialog;            
            d.setVisible(true);
            if (d.ok_clicked) {
              Number n = null;
              n = (Number) d.fade_in_spinner.getValue();
              float fi = n.floatValue();
              n = (Number) d.fade_out_spinner.getValue();
              float fo = n.floatValue();
              boolean st = d.op_stereo.isSelected();
              boolean sh = d.op_16bit.isSelected();
              main_app.wave_writer = new wave_writerc(filename,st,sh,fi,fo);
              main_app.wave_writer.write_song();
              main_app.wave_writer.close();
            }
          } else {
            JOptionPane.showMessageDialog(main_frame,"error: file already exists","error",JOptionPane.ERROR_MESSAGE);
          }
        }  
      } catch (Exception err) {
        System.out.println("menusave error");
        System.out.println(err.getMessage());
        err.printStackTrace();
      }

  }
  static int save_song_prompt() {
    int op = JOptionPane.showConfirmDialog(main_frame,
    "Do you want to save your song?","save song?",
    JOptionPane.YES_NO_OPTION);
    if (op == JOptionPane.YES_OPTION) {
      save2();       
    }    
    return op;
  }

  static void exit2() {
   try{
     while (main_app.song_modified == true) {
       int old_vol = pattern_player.volume;
       pattern_player.volume = 0;
       int op = save_song_prompt();
       if (op == JOptionPane.NO_OPTION) {
         System.exit(0);
       }
     } 
     System.exit(0);	       
   } catch(SecurityException err){
      System.out.println("can not exit");
   }
  
  }
  static void update_status_bar() {
    String str = "volume: " + pattern_player.volume;
    //str = str + " bass: " + pattern_player.bass;
    //int o = num_octaves-octave_offset;
    str = str + " pan: "; 
    if (pan_note == 1) {str = str + "left";} 
    if (pan_note == 2) {str = str + "center";} 
    if (pan_note == 3) {str = str + "right";} 
    
    if (play_mode == PATTERN_MODE) {str = str + " mode: P ";}
    if (play_mode == SONG_MODE) {str = str + " mode: S ";}
    if (play_mode == NOTE_MODE) {str = str + " mode: N ";}
    str = str + " length: " + song_player.pattern.get_length();
    int sh = main_panelc.sel_harmonic;
    int h = (int)((main_panelc.harmonic_low[sh]*10)+0.5);
    str = str + " harmonic: ";
    str = str + ((int) (h/10)) + ".";
    str = str + ((int) (h%10));
    status_bar.setText(str);
  }
  static void open2() {
      try{
        FileDialog f = new FileDialog(main_frame,"open pattern",FileDialog.LOAD);
        f.setDirectory(main_panelc.dirname);
        f.show();
        if (f.getFile() != null) {
          String filename = f.getDirectory() + f.getFile();
          
          int ft = get_file_type(filename);
          if (ft == 1) {
            DataInputStream infile = new DataInputStream(new BufferedInputStream(
            new  GZIPInputStream(new FileInputStream(filename))));
            open_file(infile);
          } else if (ft == 2) {
            new song_browserc(filename);
          } else {
            JOptionPane.showMessageDialog(main_frame,
            "error: invalid gz file","can not open file",
            JOptionPane.ERROR_MESSAGE);
          
          }
          main_panel.repaint();
          main_panelc.filename = f.getFile();
          main_panelc.dirname = f.getDirectory();
	
        }         
      } catch (Exception err) {
        System.out.println("menuopen error");
        System.out.println(err.getMessage());
        err.printStackTrace();
      }
  }
  static void save2() {
      try{
        FileDialog f = new FileDialog(main_frame,"save pattern",FileDialog.SAVE);
        f.setDirectory(main_panelc.dirname);
        //f.setFile(main_panelc.filename);
        f.show();
        if (f.getFile() != null) {
          String filename = f.getDirectory() + f.getFile();
          File f2 = new File(filename);
          String filename2 = main_panelc.dirname;
          filename2 = filename2 + main_panelc.filename;
          if ((f2.exists() == false) | (filename.equals(filename2)) == true) {
            save_file(filename);
            main_panelc.filename = f.getFile();
            main_panelc.dirname = f.getDirectory();
          } else {
            JOptionPane.showMessageDialog(main_frame,new JLabel("file already exists"),"error",JOptionPane.ERROR_MESSAGE);
          }
        }
      } catch (Exception err) {
        System.out.println("menusave error");
        System.out.println(err.getMessage());
        err.printStackTrace();
      }
  }  
  static int get_file_type(String filename) {
    try {
      byte b[] = new byte[4];
      InputStream in = new FileInputStream(filename);
      in.read(b);
      //System.out.println("bytes: " + b[0] + " " + b[1] + " " + b[2] + " " + b[3]);
      if ((b[0] == 31) & (b[1] == -117) & (b[2] == 8) & (b[3] == 0)) {
        return 1;
      }   
      if ((b[0] == 0x50) & (b[1] == 0x4b) & (b[2] == 0x03) & (b[3] == 0x04)) {
        return 2;
      }
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
  static boolean isColorDark(int col) {
    int a = (col&255)+((col>>8)&255)+((col>>16)&255);
    if (a < 384) {return true;}
    return false;
  }
  static Color get_text_color(Color col) {
    return get_text_color(color_to_int(col));
  }
  static Color get_text_color(int col) {
    if (isColorDark(col) == true) {
      return Color.white;
    } else {
      return Color.black;
    }
  }
  static int color_to_int(Color col) {
    int red = col.getRed();
    int green = col.getGreen();
    int blue = col.getBlue();
    return red + (green << 8) + (blue << 16);
  }
  static Color int_to_color(int col) { 
    int red = col & 255;
    int green = (col >> 8) & 255;
    int blue = (col >> 16) & 255;
    return new Color(red,green,blue);
  }
  static void open_file(DataInputStream infile) {

 
    try {
    int id = infile.readInt();
    if (id != ID_NUM) {
      JOptionPane.showMessageDialog(main_frame,
    "error: invalid gz file",
    "can not open file",
      JOptionPane.ERROR_MESSAGE);
      return;
    }
    int version = infile.readUnsignedShort();
    if ((version < 0) | (version >= 9)) {
      JOptionPane.showMessageDialog(main_frame,
    "error: the file is too new",
    "can not open file",
      JOptionPane.ERROR_MESSAGE);
      return;
    }
    main_panelc.song_pos = -1;

    notes_per_octave = infile.readUnsignedByte();
    if ((notes_per_octave == 0) & (version >= 8)) {
      notes_per_octave = 256;
    }
    scalec.equal_divisions = (short) notes_per_octave;
    scalec.period = (short) notes_per_octave;
    //scale = new int[notes_per_octave];   
    scalec scale = new scalec(notes_per_octave);
    song_player.scale = scale;
    if (version >= 8) {
      number_of_keys = infile.readInt();
    } else {
      int num_octaves = infile.readUnsignedByte();
      number_of_keys = num_octaves*notes_per_octave;
    }
    int num_patterns = 0;
    if (version >= 1) {num_patterns = infile.readInt();}
    else {num_patterns = infile.readUnsignedByte()+1;}
    int num_primes = infile.readUnsignedByte();
    prime_list = new prime_listc(num_primes);
    base_freq = infile.readUnsignedShort();
    int song_length = infile.readInt();    
    int num_extra_bytes = 0;
    Vector pattern_name_list = new Vector();
    if (version >= 7) {
      
      song_playerc.highest_note_freq = infile.readInt();
      if (version >= 8) {
        infile.readInt();
      } else {
        int sample_rate = infile.readInt();
        if (pattern_playerc.sample_rate != sample_rate) {
          equalizer.sample_rate_changed();
          pattern_playerc.sample_rate = sample_rate;
        }      
      }
      equalizer e2 = equalizer.e2;
      e2.read(infile,version);
      equalizer.e1.copy(e2);
      equalizer.e3.copy(e2);              
      infile.readShort();
      if (version >= 8) {
        infile.readByte();
      } else {
        sampleplayerc.interpolation = infile.readByte();
      }
      if (bass_treble_window != null) {
        bass_treble_window.update();
      }
    }
    if (version >= 4) {
      infile.readByte();
      if (version >= 6) {
        infile.readInt();
        infile.readInt();
      }
      num_extra_bytes = infile.readInt();
      //System.out.println(num_extra_bytes);
      if (version <= 7) {
        scalec.interval_size = infile.readShort() << 16;
      }
      tempo = infile.readFloat();
      main_panelc.harmonic_color.clear();
      int nh = infile.readInt();
      for (int i = 0;i < nh;i++) {
        main_panelc.harmonic_color.add(int_to_color(infile.readInt()));
      }
      //System.out.println("num_extra_bytes: " + num_extra_bytes);
      if (num_extra_bytes >= 10) {
        num_extra_bytes = num_extra_bytes - 10;
        int b = infile.readByte();
        pattern_player.ins.string_table_num_octaves = b;
        //System.out.println("num_octaves: " + b);
        b = infile.readByte();        
        if (version <= 7) {b = b+b+20;}
        int minSize = string_table_options_dialogc.minSize;
        int maxSize = string_table_options_dialogc.maxSize;
        if (b < minSize) {b = minSize;}
        if (b > maxSize) {b = maxSize;}
        pattern_player.ins.string_table_psize = b;
        //System.out.println("table_psize: " + b);
        int s = infile.readInt();
        pattern_player.ins.string_table_seed = s;
        //System.out.println("seed: " + s);
        float bandwidth = infile.readFloat();
        pattern_player.ins.bandwidth = bandwidth;
        //System.out.println("bandwidth: " + bandwidth);
        if (num_extra_bytes >= 4) {
          num_extra_bytes = num_extra_bytes - 4;
          hex_keyboard_panelc.x_step = infile.readShort();
          hex_keyboard_panelc.y_step = infile.readShort();
          if (num_extra_bytes >= 16) {
            num_extra_bytes = num_extra_bytes - 16;
            sampleplayerc.envelope.read(infile);
          }
        }

      }
    }
    //num_extra_bytes = num_extra_bytes + 2;
    //System.out.println(num_extra_bytes);
    for (int i = 0;i < num_extra_bytes;i++) {
      infile.readUnsignedByte();
    }

    if (version >= 8) {
      scalec.interval_size = infile.readInt();
      //System.out.println("interval_size: " + scalec.interval_size);
      scalec.equal_divisions = infile.readUnsignedShort();
      //System.out.println("equal_divisions: " + scalec.equal_divisions);
      scalec.generator = infile.readFloat();
      //System.out.println("generator: " + scalec.generator);
      scalec.period = infile.readFloat();
      //System.out.println("period: " + scalec.period);
    }
    if (version == 3) {
      infile.readByte();
      int header_size = 32;
      int header_size2 = infile.readInt();
      num_extra_bytes = header_size2-header_size;
      scalec.interval_size = infile.readShort() << 16;
      tempo = infile.readFloat();
    } 
    if (version < 3) {
      if (version >= 1) {
        infile.readInt();infile.readInt();
        infile.readInt();infile.readInt();
      }
      infile.readInt();infile.readInt();
      infile.readInt();infile.readShort();
    }
    song_list.clear();
    pattern_list.clear();
    tuning_map.clear();
    //System.out.println("tunings ");

    if (version >= 3) {
      if (version >= 6) {
        int num_tunings = infile.readInt();
        for (int i = 0;i < num_tunings;i++) {
          scale = new scalec(notes_per_octave);
          String tuning_name = infile.readUTF();
          //System.out.println("tuning: " + tuning_name);
          if (version >= 8) {
            for (int j = 0;j < notes_per_octave;j++) {
              scale.scale[j] = infile.readInt();
            }
          } else {
            for (int j = 0;j < notes_per_octave;j++) {
              scale.scale[j] = infile.readShort() << 16;
            }
          }
          tuning_map.put(tuning_name,scale);
        }
      } else {
        for (int i = 0;i < notes_per_octave;i++) {
          scale.scale[i] = infile.readShort() << 16;
        }
        tuning_map.put("t",scale);
      }
    }
    if (version >= 8) {
      prime_list.read_list(infile);
    } else {
      for (int i = 0;i < prime_list.num_primes;i++) {
        prime_list.prime_factor[i] = infile.readFloat();
      }
    }
    for (int i = 0;i < num_patterns;i++) {      
      int length = infile.readUnsignedShort();
      //System.out.println("length: " + length);
      patternc pattern = new patternc(length);
      song_player.pattern = pattern;
      if (version >= 1) {
        pattern.name = infile.readUTF();
        if (version >= 6) {
          for (int j = 0;j < ((notes_per_octave>>3)+1);j++) {
            pattern.scale[j] = infile.readByte();
          }
        }
        int b = infile.readShort();
        if ((b & 1) == 1) {
          pattern.image = infile.readUTF();
        }
        infile.readShort();
        infile.readInt();
        infile.readInt();
        infile.readInt();
        infile.readInt();
      } else {
        pattern.name = "pattern";
      }
      for (int x = 0;x < pattern.length;x++) {
        int n = number_of_keys;
        if (version == 0) {n = n - 1;}
        for (int y = 0;y <= n;y++) {
          int a = infile.readByte();
          pattern.set_cell(x,y,a);
        }
      }
      String patternname2 = pattern.name;
      int x = 2;
      while (pattern_list.get(patternname2) != null) {
        patternname2 = pattern.name + x;
        x = x + 1;
      }
      pattern_list.put(patternname2,pattern);
      if (version <= 4) {
        pattern_name_list.add(patternname2);
      }
    }
    //pattern_player.volume = 1;
    for (int i = 0;i < song_length;i++) {      
      String pat_name;
      if (version >= 5) {
        pat_name = infile.readUTF();
      } else {
        int i2 = infile.readInt();
        pat_name = (String) pattern_name_list.get(i2);
      }
      patternc p = (patternc) main_app.pattern_list.get(pat_name);
      song_list_entryc en = new song_list_entryc(pat_name,0,0,"t");
      main_app.song_list.add(en);      
      if (version >= 2) {
        if (version >= 8) {
          en.cents = infile.readInt();
        } else {
          en.cents = infile.readShort() << 16;
        }
        en.mode = infile.readByte();
        infile.readByte();
      }
      if (version >= 6) {
        en.tuning = infile.readUTF();
      }
    }
    //pattern_list_model.update_pattern_list_ids();
    pattern_list_window.update_list_box();
    main_app.song_modified = false;
    main_app.main_panel.update_size();
    main_app.song_player.create_players();
    //main_panelc.setup_scale34(main_app.notes_per_octave);
    tunning_table_window.create_new_panel();
    if (prime_table_window != null) {
      prime_table_window.create_new_panel();
    }
    main_panelc.update_harmonics();
    //main_panelc.update_harmonic_offsets();
    //main_app.pattern_player.alloc_string_tables(6);
    //main_app.pattern_player.update_players();
    //if (main_app.song_list.size() > 0) {
    //  pattern_list_window.table.setSelectedRow(0);
    //}
    main_app.pattern_list_window.play_next_pattern();
    infile.close();
    update_status_bar();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  static void save_file(String filename) {

    
    try {
    main_app.song_modified = false;
    DataOutputStream outfile = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))));
    outfile.writeInt(ID_NUM); //ID
    outfile.writeShort(8); //Version
    outfile.writeByte(notes_per_octave);
    //int bn = patternc.black_notes_per_octave;
    //outfile.writeByte(bn);
    //outfile.writeByte(num_octaves);
    outfile.writeInt(number_of_keys);
    outfile.writeInt(pattern_list.size());//num patterns
    outfile.writeByte(prime_list.num_primes);
    outfile.writeShort(base_freq);
    outfile.writeInt(song_list.size());//song length
    outfile.writeInt(song_playerc.highest_note_freq);
    outfile.writeInt(0);
    equalizer.e2.write(outfile);
    outfile.writeShort(0);
    outfile.writeByte(0);    
    outfile.writeByte(0);
    outfile.writeInt(0);
    outfile.writeInt(0);
    outfile.writeInt(30);//extra bytes

    outfile.writeFloat(tempo);
    outfile.writeInt(main_panelc.harmonic_color.size());
    for (int i = 0;i < main_panelc.harmonic_color.size();i++) {
      Color c = (Color) main_panelc.harmonic_color.get(i);
      outfile.writeInt(color_to_int(c));
    }
    int b = 0;
    b = pattern_player.ins.string_table_num_octaves;
    outfile.writeByte(b);
    b = pattern_player.ins.string_table_psize;
    outfile.writeByte(b);
    int seed = pattern_player.ins.string_table_seed;
    outfile.writeInt(seed);
    float bandwidth = pattern_player.ins.bandwidth;
    outfile.writeFloat(bandwidth);
    int x_step = hex_keyboard_panelc.x_step;
    outfile.writeShort(x_step);
    int y_step = hex_keyboard_panelc.y_step;
    outfile.writeShort(y_step);
    sampleplayerc.envelope.write(outfile);

    outfile.writeInt(scalec.interval_size);
    outfile.writeShort(scalec.equal_divisions);
    outfile.writeFloat((float) scalec.generator);
    outfile.writeFloat((float) scalec.period);
    
    //for (int i = 0;i < notes_per_octave;i++) {
    //  outfile.writeShort(scale.get(i));
    //}
    outfile.writeInt(tuning_map.size());
    Set ks = tuning_map.keySet();     
    for (Iterator it = ks.iterator();it.hasNext();) { 
      String tuning_name = (String) it.next();
      scalec s = (scalec) tuning_map.get(tuning_name);
      outfile.writeUTF(tuning_name);
      for (int i = 0;i < notes_per_octave;i++) {
        outfile.writeInt(s.scale[i]);
      }
    }
    prime_list.write_list(outfile);
    //for (int i = 0;i < prime_list.num_primes;i++) {
    //}
    //for (int i = 0;i < pattern_list.size();i++) {      
    ks = pattern_list.keySet();     
    for (Iterator it = ks.iterator();it.hasNext();) { 
      String pat_name = (String) it.next();
      patternc p = (patternc) pattern_list.get(pat_name);
      outfile.writeShort(p.length);
      outfile.writeUTF(p.name);
      for (int i = 0;i < ((notes_per_octave>>3)+1);i++) {
        outfile.writeByte(p.scale[i]);
      }
      //if (p.image != null) {
      //  outfile.writeShort(1);
      //  outfile.writeUTF(p.image);
      //} else {
        outfile.writeShort(0);      
      //}
      outfile.writeShort(0);      
      outfile.writeInt(0);
      outfile.writeInt(0);
      outfile.writeInt(0);
      outfile.writeInt(0);
      for (int x = 0;x < p.length;x++) {
        //int n = notes_per_octave*num_octaves;
        int n = number_of_keys;
        for (int y = 0;y <= n;y++) {
          outfile.writeByte(p.get_cell(x,y));
        }
      }
    }
    for (int i = 0;i < song_list.size();i++) {      
      song_list_entryc e = (song_list_entryc) main_app.song_list.get(i);
      //patternc p = e.pattern;
      outfile.writeUTF(e.pattern);
      outfile.writeInt(e.cents);
      outfile.writeByte(e.mode);
      outfile.writeByte(0);
      outfile.writeUTF(e.tuning);
    }
    outfile.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  //static int get_3fifth() {
  //  float prime2 = 1000000.0f;
  //  float prime3 = 1000000.0f;
  //  for (int i = 0;i < prime_list.num_primes;i++) {
  //    float p = prime_list.prime_factor[i];
  //    if (p < prime2) {prime2 = p;}
  //    else if (p < prime3) {prime3 = p;}
  //  }    
  //  if (prime_list.num_primes == 1) {prime3 = prime2;}
  //  int i = (int) (((Math.log(prime3)/Math.log(prime2)) * notes_per_octave) + 0.5);
  //  return i % notes_per_octave;
  //}
  static void init_primes_table75() {
    //int a = -10 % 10;
    //System.out.println("a: " + a);
    primes_table75 = new short[60];
    int num_primes = 6;
    int n = 1;
    primes_table75[0] = 2;primes_table75[1] = 3;
    primes_table75[2] = 5;primes_table75[3] = 7;
    primes_table75[4] = 11;primes_table75[5] = 13;
    while (num_primes < 60) {
      n = n + 2;
      if ((n % 3) == 0) {continue;}
      if ((n % 5) == 0) {continue;}
      if ((n % 7) == 0) {continue;}
      if ((n % 11) == 0) {continue;}
      if ((n % 13) == 0) {continue;}
      primes_table75[num_primes] = (short) n;
      num_primes = num_primes + 1;
    }
  }
  public static void main(String[] args)
    throws	Exception
  {    
    TextTransfer = new TextTransferc();
    init_primes_table75();
    //int ptlen = primes_table75.length;
    int ptlen = 15;
    prime_list = new prime_listc(ptlen);
    //for (int i = 0;i < ptlen;i++) {
    //  prime_list.prime_factor[i] = primes_table75[i];
    //}
    prefs = Preferences.userRoot().node("led_synth23948797523");
    sampleplayerc.sample_rate = prefs.getInt("sample_rate",44100);
    sampleplayerc.interpolation = prefs.getInt("interpolation",2);


    tuning_map = new HashMap();
    pattern_list = new HashMap();
    song_list = new Vector();
    song_player = new song_playerc(new instrumentc());
    new_song(12,"default");
    pattern_player = new pattern_playerc(song_player);
    //pattern_list.add(new patternc(12,"default2"));
    javax.swing.SwingUtilities.invokeLater(new main_app());        
  }
  static void new_song(int et,String patname) {
    pattern_list.clear();
    song_list.clear();
    main_panelc.song_pos = -1;
    song_player.pattern = new patternc(6,patname);
    pattern_list.put(patname,song_player.pattern);
    song_list.add(new song_list_entryc(patname,0,0,"t"));  
    song_player.scale = new scalec(et);
    song_player.scale.init();
    //for (int i = 0;i < et;i++) {
    //  song_player.scale.set(i,(int) ((((double)octave_cents)*i)/et));
    //}
    tuning_map.put("t",song_player.scale);
    if (tunning_table_window == null) {
      tunning_table_window = new tunning_table_windowc();
    }
    tunning_table_window.create_new_panel();
    if (prime_table_window != null) {
      prime_table_window.create_new_panel();
    }
    main_panelc.filename = null;
    main_panelc.dirname = null;
    main_panelc.update_low_harmonics();
    main_panelc.update_harmonic_offsets();

  }
  public void run() {    
    main_frame = new JFrame();
    main_panel = new main_panelc();
    main_panel_scroller = new JScrollPane(main_panel); 
    main_frame.getContentPane().setLayout(new BorderLayout());
    main_frame.getContentPane().add(main_panel_scroller);


    JScrollBar hscroll = main_app.main_panel_scroller.getHorizontalScrollBar();
    JScrollBar vscroll = main_app.main_panel_scroller.getVerticalScrollBar();
    hscroll.setBlockIncrement(100);
    hscroll.setUnitIncrement(30);
    vscroll.setBlockIncrement(100);
    vscroll.setUnitIncrement(30);

    //hscroll = new JScrollBar(); 
    //hscroll.setOrientation(JScrollBar.HORIZONTAL);
    //vscroll = new JScrollBar(); 
    //vscroll.setOrientation(JScrollBar.VERTICAL);
    harmonic_colors_window = new JFrame();
    harmonic_colors_panel = new harmonic_colors_panelc();
    harmonic_colors_window.getContentPane().add(harmonic_colors_panel);
    harmonic_colors_window.setBounds(20,20,300,300);
    hex_keyboard_window = new JFrame();
    hex_keyboard_panel = new hex_keyboard_panelc();
    hex_keyboard_window.addKeyListener(hex_keyboard_panel);

    hex_keyboard_window.getContentPane().add(hex_keyboard_panel);
    hex_keyboard_window.setBounds(20,20,300,300);

    status_bar = new JLabel("status bar");
    main_frame.getContentPane().add(status_bar,BorderLayout.SOUTH);
    //main_frame.getContentPane().add(hscroll,BorderLayout.NORTH);
    main_frame.addKeyListener(main_panel);
    main_panel.addKeyListener(main_panel);
    //main_panel_scroller.addKeyListener(main_panel);
    main_frame.addWindowListener(main_panel);
    
    main_menu_bar = setup_menu();
    main_frame.setJMenuBar(main_menu_bar);
    main_frame.setBounds(30,30,500,500);
    main_frame.setVisible(true);
    main_frame.setTitle("frank's java LED Synthesizer 2.4");
    pattern_list_window = new pattern_list_windowc("pattern list");
    //pattern_list_window.setVisible(true);
    string_table_options_dialog = new string_table_options_dialogc(main_frame,"options");
    envelope_dialog = new envelope_dialogc(main_frame,"envelope");
    options_dialog = new options_dialogc(main_frame,"options");
    new_song_wizard = new new_song_wizardc(main_frame,"new song");
    update_status_bar();
    pattern_player.start();
    main_panel.update_size();
  }
  JMenuItem createMenuItem(String text,JMenu menu,String action) {
    JMenuItem mi = new JMenuItem(text);
    menu.add(mi);
    mi.setActionCommand(action);    
    mi.addActionListener(this);
    return mi;
  }
  void load_instrument_as_wave() {
      try{
        JFileChooser fc = new JFileChooser();
//(hex_keyboard_window,"load instrument",FileDialog.LOAD);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "WAV audio files", "wav");
        fc.setFileFilter(filter);
        String ins_dir = prefs.get("instrument_directory",null);
        if (ins_dir != null) {
          fc.setCurrentDirectory(new File(ins_dir));
        }
        int rv = fc.showOpenDialog(hex_keyboard_window);
        if (rv == JFileChooser.APPROVE_OPTION) {
          File f = fc.getSelectedFile();
          System.out.println(f.getName());
          System.out.println(f.getParent());
          //String filename = f.getDirectory() + f.getFile();
          String dirname = f.getParent() + File.separator;
          String filename = dirname + f.getName();
          //sampletablec t = new sampletablec(filename);
          song_player.ins = new instrumentc(filename);
          song_player.update_players();
          prefs.put("instrument_directory",dirname);
          //prefs.put("instrument_file",f.getName());
        }
      } catch (Exception err) {
        System.out.println("menuopen error");
        System.out.println(err.getMessage());
        err.printStackTrace();
      }
  }

  JMenuBar setup_menu() {
    JMenuBar mb = new JMenuBar();
    JMenu file_menu = new JMenu("file");
    createMenuItem("new",file_menu,"menu_new");
    createMenuItem("open",file_menu,"menu_open");
    createMenuItem("save",file_menu,"menu_save");
    createMenuItem("export to wave",file_menu,"menu_export_wave");
    createMenuItem("options",file_menu,"menu_options");
    //createMenuItem("load instrument",file_menu,"menu_open_wave");
    createMenuItem("exit",file_menu,"menu_exit");    
    mb.add(file_menu);
    JMenu pattern_menu = new JMenu("pattern");
    createMenuItem("new pattern",pattern_menu,"menu_add_pattern");    
    createMenuItem("double length",pattern_menu,"double_length");    
    createMenuItem("triple length",pattern_menu,"triple_length");    
    //createMenuItem("transpose fifth",pattern_menu,"transpose_fifth_up");
    //createMenuItem("transpose fourth",pattern_menu,"transpose_fourth_down");
    createMenuItem("copy",pattern_menu,"menu_copy");
    createMenuItem("paste",pattern_menu,"menu_paste");
    createMenuItem("rename",pattern_menu,"menu_rename");
    mb.add(pattern_menu);
    JMenu song_menu = new JMenu("song");
    //createMenuItem("show equalizer",song_menu,"equalizer");
    createMenuItem("parametric equalizer",song_menu,"bass_treble");
    createMenuItem("show pattern list",song_menu,"menu_pattern_list");
    createMenuItem("get song length",song_menu,"get_song_length");
    createMenuItem("show hex keyboard",song_menu,"show_hex_keyboard");
    createMenuItem("edit envelope",song_menu,"edit_envelope");
    createMenuItem("add random patterns",song_menu,"add_random_patterns");

    mb.add(song_menu);
 
    JMenu tunning_table_menu = new JMenu("table");
    createMenuItem("view tunning table",tunning_table_menu,"view_tunning_table");    
    //createMenuItem("generate mean tone",tunning_table_menu,"generate_mean_tone_scale");    
    createMenuItem("create temperament",tunning_table_menu,"create_temperament");
    createMenuItem("import scala",tunning_table_menu,"import_scala");
    createMenuItem("export scala",tunning_table_menu,"export_scala");
    mb.add(tunning_table_menu);

    JMenu harmonics_menu = new JMenu("harmonics");
    createMenuItem("edit harmonic colors",harmonics_menu,"edit_harmonic_colors");    
    createMenuItem("edit prime harmonics",harmonics_menu,"view_prime_table");    
    createMenuItem("options",harmonics_menu,"str_table_options");    
    mb.add(harmonics_menu);
    
    return mb;
  }
}

