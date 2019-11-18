import java.io.*;

public class CreateFragments {
    public static void main(String[] args) {

        String inputFile = args[0];
        int minSeqLen = Integer.parseInt(args[1]);
        int maxSeqLen = Integer.parseInt(args[2]);
        String outputFile = args[3];
        createFragments(inputFile,minSeqLen,maxSeqLen,outputFile);
    }

    static void createFragments(String inputFile,int minSeqLen, int maxSeqLen,String outputFile )
    {
        int count=0;
        double x=minSeqLen;
        int y=maxSeqLen;
        try {
            FileInputStream fstream = new FileInputStream(inputFile);
            FileOutputStream fostream = new FileOutputStream(outputFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            StringBuilder sb=new StringBuilder();
            double rand=(Math.random() * ((y - x) + 1)) + x;
            int fragLen=(int)rand;

            while ((strLine = br.readLine()) != null)
            {
                while(strLine!=null && strLine.charAt(0)!='>')
                {
                    sb.append(strLine);
                    strLine = br.readLine();
                }
                if(sb.length()==0) continue;
                int numFrag=sb.length()/fragLen;
                int start=0;

                for(int i=0;i<numFrag;i++)
                {
                    String to_add=sb.substring(start,start+fragLen)+"\n";
                    start=start+fragLen;

                    String text = ">FASTA"+count;
                    byte[] mybytes = text.getBytes();
                    fostream.write(mybytes);

                    while(to_add.length()!=0) {
                        String rem="";
                        if(to_add.length()>80)
                        {
                            rem = to_add.substring(80,to_add.length());
                            to_add=to_add.substring(0,80);
                        }
                        String nextLine = "\n";
                        mybytes = nextLine.getBytes();
                        fostream.write(mybytes);
                        mybytes = to_add.getBytes();
                        fostream.write(mybytes);
                        to_add=rem;
                    }
                    count++;
                }
                sb=new StringBuilder();
            }
        }
        catch (Exception e) {  System.err.println("Error: " + e.getMessage());       }
    }
}
