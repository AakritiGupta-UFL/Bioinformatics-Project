import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        double a=Double.valueOf(args[1]);
        double b=Double.valueOf(args[2]);
        double c=Double.valueOf(args[3]);
        double d=Double.valueOf(args[4]);
        double p=Double.valueOf(args[6]);
        int n = Integer.valueOf(args[0]);
        int k = Integer.valueOf(args[5]);
        String outputFileName= args[7];

        double i=a+b+c+d;

        generateSeq(n,a/i,b/i,c/i,k,p,outputFileName);

    }

    static void generateSeq(int n,double a,double b,double c,int k,double p,String outputFileName) {
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
            StringBuilder s = new StringBuilder();
            int i = 0;
            while (i < n) {
                double r = Math.random();
                if (r < a) {
                    s.append('A');
                    i++;
                } else if (r < a + b) {
                    s.append('C');
                    i++;
                } else if (r < a + b + c) {
                    s.append('G');
                    i++;
                } else {
                    s.append('T');
                    i++;
                }
            }

            writer.write(">FASTA");
            String to_add=s.toString();
            while(to_add.length()!=0) {
                String rem="";
                if(to_add.length()>80)
                {
                    rem = to_add.substring(80,to_add.length());
                    to_add=to_add.substring(0,80);
                }
                writer.newLine();
                writer.write(to_add);
                to_add=rem;
            }

            writer.newLine();

            int j = 1;

            while (j < k) {
                StringBuilder t = new StringBuilder();
                for (int o = 0; o < s.length(); o++) {
                    s.charAt(o);
                    t.append(s.charAt(o));
                }
                int len = s.length();
                for (int l = 0; l < len; l++) {
                    double nj = Math.random();
                    if (nj < p) {
                        double x = nj * p;
                        if (x < p / 2) {
                            //mutate
                            double r = Math.random();
                            if (r < a) {
                                if (t.charAt(l) == 'A')
                                    l--;
                                else t.setCharAt(l, 'A');
                            } else if (r < a + b) {
                                if (t.charAt(l) == 'C')
                                    l--;
                                else t.setCharAt(l, 'C');
                            } else if (r < a + b + c) {
                                if (t.charAt(l) == 'G')
                                    l--;
                                else
                                    t.setCharAt(l, 'G');
                            } else {
                                if (t.charAt(l) == 'T')
                                    l--;
                                else t.setCharAt(l, 'T');
                            }

                        } else {

                            t.deleteCharAt(l);
                            l--;
                            len--;
                        }
                    }
                }
                writer.write(">FASTA");
                to_add=t.toString();
                while(to_add.length()!=0) {
                    String rem="";
                    if(to_add.length()>80)
                    {
                        rem = to_add.substring(80,to_add.length());
                        to_add=to_add.substring(0,80);
                    }
                    writer.newLine();
                    writer.write(to_add);
                    to_add=rem;
                }

                writer.newLine();
                j++;
            }
            writer.close();
        } catch(IOException e) {
        }
    }

}
