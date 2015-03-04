import java.io.*;

class EQ_filter {
  private float real;
  private float img;
  private float cos45;
  private float sin45;
  private float vol;
  private float vol2;

  private int freq;
  private int freq_step;
  private int band_width;
  private int band_width_step;
  private int db;
  String name;
  EQ_filter(String name,int f,int fs,int bw,int bw_s) {
    this.name = name;
    this.freq = f;
    this.freq_step = fs;
    this.band_width = bw;
    this.band_width_step = bw_s;
  }
  EQ_filter() {
    cos45 = 1.0f;
    sin45 = 0.0f;
  }
  void copy(EQ_filter e) {
    name = e.name;
    set_freq(e.get_freq());
    set_freq_step(e.get_freq_step());
    set_bandwidth(e.get_bandwidth());
    set_bandwidth_step(e.get_bandwidth_step());
    set_DB(e.get_DB());

  }
  void read(DataInputStream infile,int version) throws IOException {
    name = infile.readUTF();
    set_freq(infile.readUnsignedShort());
    set_freq_step(infile.readUnsignedShort());
    set_bandwidth(infile.readUnsignedShort());
    set_bandwidth_step(infile.readUnsignedShort());
    set_DB(infile.readByte());
  }
  void write(DataOutputStream outfile) throws IOException {
    outfile.writeUTF(name);
    outfile.writeShort(freq);
    outfile.writeShort(freq_step);
    outfile.writeShort(band_width);
    outfile.writeShort(band_width_step);
    outfile.writeByte(db);
  }
  int get_freq() {
    return freq;
  }
  void set_freq(int freq) {
    this.freq = freq;
    cos45 = (float) Math.cos((Math.PI * 2 * freq) / sampleplayerc.sample_rate);
    sin45 = (float) Math.sin((Math.PI * 2 * freq) / sampleplayerc.sample_rate);
  }
  int get_freq_step() {
    return freq_step;
  }
  void set_freq_step(int s) {
    freq_step = s;
  }
  int get_bandwidth() {
    return band_width;
  }
  void set_bandwidth(int bw) {
    this.band_width = bw;
    float v = (float) Math.exp(-0.0001 * bw);
    vol = 1-v;
  }
  int get_bandwidth_step() {
    return band_width_step;
  }
  void set_bandwidth_step(int s) {
    band_width_step = s;
  }
  int get_DB() {
    return db;
  }
  void set_DB(int db) {
    vol2 = (float) (Math.exp(Math.log(10.0)*(db/20.0f)) - 1.0f);  
    this.db = db;
  }

  void filter(float buf[],int buf_size) {
     float r = 0.0f;
     float i = 0.0f;
     for (int j = 0;j < buf_size;j++) {
       real = real + ((buf[j]-real) * vol);
       buf[j] = buf[j] + (real * vol2);
       r  = (real*cos45)-(img*sin45);
       i = (real*sin45)+(img*cos45);
       real = r;img = i;
     }
  }  
}
public class equalizer {
  static equalizer e1 = new equalizer();
  static equalizer e2 = new equalizer();
  static equalizer e3 = new equalizer();
  EQ_filter band[];
  int num_bands = 3;
  static byte DB_min = -128;
  static byte DB_max = 127;
  equalizer() {

    band = new EQ_filter[num_bands];
    band[0] = new EQ_filter("treble",15000,500, 1500,200);
    band[1] = new EQ_filter("mid",   1000,20,   300,50);
    band[2] = new EQ_filter("bass",  50,1,      50,10);
  }
  static void update_EQ() {
    sample_rate_changed();
  }
  static void sample_rate_changed() {
    for (int i = 0;i < e2.num_bands;i++) {
      e2.band[i].set_freq(e2.band[i].get_freq());
    }
    e1.copy(e2);
    e3.copy(e2);            
  }
  void copy(equalizer e) {
    if (e.num_bands != num_bands) {
      num_bands = e.num_bands; 
      band = new EQ_filter[num_bands];
      for (int i = 0;i < num_bands;i++) {
        band[i] = new EQ_filter();
      }    
    }
    for (int i = 0;i < num_bands;i++) {
      band[i].copy(e.band[i]);
    }
  }

  void read(DataInputStream infile,int version) throws IOException {
    DB_min = infile.readByte();
    DB_max = infile.readByte();
    num_bands = infile.readUnsignedShort();
    band = new EQ_filter[num_bands];
    for (int i = 0;i < num_bands;i++) {
      band[i] = new EQ_filter();
      band[i].read(infile,version);
    }
  }
  void write(DataOutputStream outfile) throws IOException {
    outfile.writeByte(DB_min);
    outfile.writeByte(DB_max);
    outfile.writeShort(num_bands);
    for (int i = 0;i < num_bands;i++) {
      band[i].write(outfile);
    }

  }
  void filter(float buf[],int buf_size) {
    for (int i = 0;i < num_bands;i++) {
      if (band[i].get_DB() != 0) {
        band[i].filter(buf,buf_size);
      }
    }
  }
}

