public class FFT_Transformc {

  static void perform_c2r(float FFT_data[][]) {
    float PI = (float) Math.PI;
    int fft_len = FFT_data.length;
    //System.out.println("fft_len " + fft_len);
    if ((fft_len & (fft_len-1)) != 0) {
      System.out.println("error: size must be a power of 2");
      return;
    }
    for (int i1 = 1;i1 < (fft_len>>1);i1++) {
      float m_real1 = (float) Math.cos((i1 * PI) / (fft_len));
      float m_img1 = (float) Math.sin((i1 * PI) / (fft_len));      


      float l_real1 = FFT_data[i1][0];
      float l_img1 = FFT_data[i1][1];

      float r_real1 = (l_real1 * m_real1) - (l_img1 * m_img1);
      float r_img1  = (l_real1 * m_img1) + (m_real1 * l_img1);
  
      int i2 = fft_len-i1;
      float m_real2 = (float) Math.cos((i2 * PI) / (fft_len));
      float m_img2 = (float) Math.sin((i2 * PI) / (fft_len));      


      float l_real2 = FFT_data[i2][0];
      float l_img2 = FFT_data[i2][1];

      float r_real2 = (l_real2 * m_real2) - (l_img2 * m_img2);
      float r_img2  = (l_real2 * m_img2) + (m_real2 * l_img2);


      FFT_data[i1][0] = (l_real1+r_img1)/2;
      FFT_data[i1][1] = (-l_img1+r_real1)/2;
      FFT_data[i2][0] = (l_real1-r_img1)/2;
      FFT_data[i2][1] = (l_img1+r_real1)/2;

      FFT_data[i2][0] += (l_real2+r_img2)/2;
      FFT_data[i2][1] += (-l_img2+r_real2)/2;
      FFT_data[i1][0] += (l_real2-r_img2)/2;
      FFT_data[i1][1] += (l_img2+r_real2)/2;

    }
    perform(FFT_data,true);
  }
  static void rearrange(float FFT_data[][]) {
    int target = 0;
    for (int pos = 0;pos < FFT_data.length;pos = pos + 1) {
      if (target > pos) {
        float temp = FFT_data[target][0];
        FFT_data[target][0] = FFT_data[pos][0];
        FFT_data[pos][0] = temp;
        temp = FFT_data[target][1];
        FFT_data[target][1] = FFT_data[pos][1];
        FFT_data[pos][1] = temp;
      }
      int mask = FFT_data.length;
      mask = mask >> 1;
      while ((target & mask) != 0) {
        target = target & ~mask;
        mask = mask >> 1;
      }
      target = target | mask;
    }
  }
  static void perform(float FFT_data[][],boolean Inverse) {
    int fft_len = FFT_data.length;
    //System.out.println(fft_len);
    if ((fft_len & (fft_len-1)) != 0) {
      System.out.println("error: size must be a power of 2");
      return;
    }
    rearrange(FFT_data);
    float PI = (float) Math.PI;
    if (Inverse) {PI = (float) -Math.PI;}
    int step = 1;
    while (step < fft_len) {
      int jump = step + step;
      float delta = PI / ((float) step);
      float s = (float) Math.sin(delta / 2.0);
      float Multiplier_real = -2.0f * s * s;
      float Multiplier_img = (float) Math.sin(delta);
      float factor_real = 1.0f;
      float factor_img = 0.0f;
      for (int group = 0;group < step;group++) {
        for (int pair = group;pair < fft_len;pair = pair + jump) {
          int match = pair + step;

          float product_real = (factor_real*FFT_data[match][0]);
          product_real = product_real - (factor_img*FFT_data[match][1]);
          float product_img = (factor_real*FFT_data[match][1]);
          product_img = product_img + (factor_img*FFT_data[match][0]);

          FFT_data[match][0] = FFT_data[pair][0] - product_real;
          FFT_data[match][1] = FFT_data[pair][1] - product_img;

          FFT_data[pair][0] = FFT_data[pair][0] + product_real;
          FFT_data[pair][1] = FFT_data[pair][1] + product_img;
        }
        float new_factor_real = (Multiplier_real*factor_real);
        new_factor_real = new_factor_real - (Multiplier_img*factor_img);

        float new_factor_img = (Multiplier_real*factor_img);
        new_factor_img = new_factor_img + (Multiplier_img*factor_real);

        factor_real = new_factor_real + factor_real;
        factor_img = new_factor_img + factor_img;
      }
      step = step + step;    
    }    
  }
}

