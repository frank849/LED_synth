import java.util.Arrays;
import java.util.prefs.Preferences;


public class scalec {
  int scale[];
  short size;
  static int equal_divisions = 12;
  //static short equal_divisions = (short) main_app.notes_per_octave;
  //static int numerator = 2;
  //static int denominator = 1;
  static int interval_size = 1200<<16;
  static float generator = 7;
  static float period = 12;
  static float offset = 0;

  scalec(int n) {
    scale = new int[n];
    size = (short) n;
  }
  static void get_prefs(Preferences prefs) {
    equal_divisions = prefs.getInt("equal_divisions",equal_divisions);
    generator = prefs.getInt("generator",(int) generator);
    period = prefs.getInt("period",(int) period);
    interval_size = prefs.getInt("scalec_interval_size",interval_size);
    
  }
  static double get_interval_fraction() {
    //double frac = numerator;
    //frac = frac / denominator;
    double s = interval_size;
    double frac = Math.exp((Math.log(2)*s)/(1200<<16));
    return frac;
  }
  static int get_num_total_key_notes_in_range(double l,double h) {
    //double h = song_playerc.highest_note_freq;
    //double l = main_app.base_freq;
    double frac = get_interval_fraction(); 
    double t = Math.log(h/l)/Math.log(frac);
    //t = t * equal_divisions;
    t = t * main_app.notes_per_octave;
    return (int) t;
  }
  static int get_interval_size() {
    //double frac = get_interval_fraction();
    //double octave_cents = (Math.log(frac)*1200.0)/Math.log(2);
    //return ((int) (octave_cents+0.5)) << 16;
    return interval_size;
  }
  //private double octave_cents() {
  //  return get_octave_cents() * 65536.0;
  //}
  public void init() {
    int ed = equal_divisions;
    double t[] = new double[size];
    double v = offset;
    for (int i = 0;i < size;i++) {
      t[i] = v;
      v = (v + generator);
      if (v > period) {v = v - period;}      
  //    double f = get_interval_size();
  //    scale[i] = (int) ((f*i)/et);
    }
    Arrays.sort(t);
    for (int i = 0;i < size;i++) {
      set(i,t[i]);
    }   
  }
  public double get(int i) {
    int i2 = i%size;
    if (i2 < 0) {i2 = i2 + size;}
    double f = cents_to_key(scale[i2]);
    int o = (i/size);
    f = f + (o * period);
    return f;
  }
  static public double cents_to_key(int cents) {
    double key = cents;
    key = key / get_interval_size();
    key = key * equal_divisions;
    return key;
  }
  static public int key_to_cents(double key) {
    key = key / equal_divisions;
    int cents = (int) (key * get_interval_size());
    return cents;
  }
  public void set(int i,double v) {
    scale[i] = key_to_cents(v);
  }
}

