import java.io.*;

public class adsr_envelopec {
  float attack = 0.0f;
  float decay = 0.0f;
  float sustain = 1.0f;
  float release = 1.0f;
  void read(DataInputStream infile) throws IOException {
    attack = infile.readFloat();
    decay = infile.readFloat();
    sustain = infile.readFloat();
    release = infile.readFloat();
  }
  void write(DataOutputStream outfile) throws IOException {
    outfile.writeFloat(attack);
    outfile.writeFloat(decay);
    outfile.writeFloat(sustain);
    outfile.writeFloat(release);
  }
}

