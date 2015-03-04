import java.io.*;
import java.util.*;
public class scl_file
{
  boolean EOF;
  private transient BufferedReader in;
  private transient FileOutputStream out;
  private String title;
  private String comment;
  private String fileName;
  private String tableOutput = "";
  private String lineTerminator = "\n";
  private String commentDelim = "!";
    scl_file ()
  {

  }

 

  void read_table (String sclFile, scalec sc)
  {
    try {
      sc.scale[0] = 0;
      int s = 1;
      in = new BufferedReader (new FileReader (new File (sclFile)));
      EOF = false;
      String line = nextLine ();
      comment = "";

      boolean foundTitle = false;
      while (line != null && !EOF && !foundTitle) {

	if (line != "") {
	  if (line.startsWith ("!")) {
	    line = line.substring (1).trim ();
	    comment += line + "\n";
	  }
	  else {
	    title = line;
	    foundTitle = true;
	  }
	}
	line = nextLine ();
      }
      boolean foundNumTunings = false;
      while (line != null && !EOF && !foundNumTunings) {

	if (line != "") {
	  if (line.startsWith ("!")) {
	    line = line.substring (1).trim ();
	    comment += line + "\n";
	  }
	  else {
	    foundNumTunings = true;
	  }
	}
	line = nextLine ();
      }
      while (line != null && !EOF) {
	if (s >= main_app.notes_per_octave) {
	  break;
	}
	if (line.startsWith ("!")) {	
	  line = line.substring (1).trim ();
	  comment += line + "\n";
	}
	else {
	  line = line.trim ();
	  if (isCent (line)) {
	    boolean gotCents = false;
	    String token = "";
	    double cents = 0;
	    while (!gotCents && token != null) {
	      StringTokenizer toke = new StringTokenizer (line);
	      try {
		token = getToken (toke, " \t");
		cents = Double.valueOf (token).doubleValue ();
		gotCents = true;
	      }
	      catch (NumberFormatException nfe) {
		gotCents = false;
	      }
	      sc.scale[s] = (int) (cents*65536.0);
	      s = s + 1;
	    }
	  }
	  else {
	    StringTokenizer toke = new StringTokenizer (line);
	    String ratioString = getToken (toke,
					   " \t");
	    if (hasSlash (line)) {
	      String ss[] = ratioString.split ("/");
	      double n = Integer.valueOf (ss[0]).intValue ();
	      double d = Integer.valueOf (ss[1]).intValue ();
	      sc.scale[s] = (int) ((Math.log (n / d)
				    * 1200.0 * 65536.0)
				   / Math.log (2));
	      s = s + 1;
	    }
	    else {
	      double n = Integer.valueOf (ratioString).intValue ();
	      sc.scale[s] = (int) ((Math.log (n) * 1200.0 * 65536.0)
				   / Math.log (2));
	      s = s + 1;
	    }
	  }
	}
	line = nextLine ();
      }
      in.close ();
    } catch (Exception e) {
      System.out.println (e.getMessage ());
      e.printStackTrace ();
    }
  }
  void write_table(String SclDir, String sclFile, String title, scalec sc) {
    try {
      int notes_per_octave = main_app.notes_per_octave;
      PrintWriter out = new PrintWriter(new FileOutputStream(SclDir + sclFile));
      out.println("! " + sclFile);
      out.println("!");
      out.println(title);
      out.print(" ");
      out.println(notes_per_octave);
      out.println("!");
      for (int i = 1;i < notes_per_octave;i++) {
        double f = sc.scale[i]-sc.scale[0];
        f = f / 65536.0;
        out.print(" ");
        out.println(f);
      }
      out.print(" ");
      double f = main_app.song_player.scale.interval_size;
      f = f / 65536.0;
      out.print(f);
      out.close();
    } catch (Exception e) {
       System.out.println (e.getMessage ());
       e.printStackTrace ();
    }
  }

  private String nextLine () throws IOException
  {
    String line = null;
      try
    {
      line = in.readLine ();
    } catch (EOFException e)
    {
      EOF = true;
    }
    if (line == null) {
      EOF = true;
    }
    return line;
  }
  private String getToken (StringTokenizer st, String seperator)
  {
    String token;
    if (st.hasMoreTokens ()) {
      token = st.nextToken ();
      if (token.equalsIgnoreCase (seperator)) {
	return null;
      }
      else {
	if (st.hasMoreTokens ()) {
	  st.nextToken ();
	}
	return token;
      }
    }
    return null;
  }

  private boolean isCent (String candidate)
  {
    for (int i = 0; i < candidate.length (); i++) {
      if (candidate.charAt (i) == '.') {
	return true;
      }
    }
    return false;
  }

  private boolean hasSlash (String candidate)
  {
    for (int i = 0; i < candidate.length (); i++) {
      if (candidate.charAt (i) == '/') {
	return true;
      }
    }
    return false;
  }
}
