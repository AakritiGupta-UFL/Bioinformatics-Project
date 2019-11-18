import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MergeFragments {

    public static void main(String[] args)
    {
        String inputFile = args[0];
        int matchScore = Integer.parseInt(args[1]);
        int repPenalty = Integer.parseInt(args[2]);
        int delPenalty = Integer.parseInt(args[3]);
        String outputFile = args[4];
        mergeFragments(delPenalty, repPenalty, matchScore,inputFile, outputFile);
    }

    static void mergeFragments(int delPenalty, int repPenalty,int matchScore, String inputFile,String outputFile)  {

        List<String> allFragments = new ArrayList<String>();
        StringBuilder toAdd = new StringBuilder();

        try {
            FileInputStream fstream = new FileInputStream(inputFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            StringBuilder sb=new StringBuilder();

            while ((strLine = br.readLine()) != null)
            {
                if(strLine.charAt(0)=='>')
                {
                    if(toAdd!=null)
                    {
                        allFragments.add(toAdd.toString());
                        toAdd=new StringBuilder();
                    }

                }
                else toAdd.append(strLine);
            }

            if(toAdd!=null)
            {
                allFragments.add(toAdd.toString());
                toAdd=new StringBuilder();
            }
        }
        catch (Exception e) {  System.err.println("Error: " + e.getMessage());       }
        allFragments.remove("");

      /*  for(int i=0;i<allFragments.size();i++)
            System.out.println("Fragment: "+allFragments.get(i));*/

        while(allFragments.size()>1)
        {
            int max=Integer.MIN_VALUE;
            int[] temp=new int[3];
            int [] arr=new int[2];
            String s="";
            for(int i=0;i<allFragments.size();i++)
            {
                for(int j=i+1;j<allFragments.size();j++)
                {
                    int[][] dp=dpMatrix(allFragments.get(i),allFragments.get(j),delPenalty,repPenalty,matchScore);
                    temp= mergeScore(dp);
                    if(temp[0]>max)
                    {
                        max=temp[0];
                        arr[0]=i;
                        arr[1]=j;

                        s = mergehelper(allFragments.get(arr[0]), allFragments.get(arr[1]), delPenalty, repPenalty, matchScore, temp, dp);
                    }

                    if(i==allFragments.size()-2 && j==allFragments.size()-1)
                    {
                        String s1=allFragments.get(arr[0]);
                        String s2=allFragments.get(arr[1]);
                        allFragments.remove(s1);
                        allFragments.remove(s2);
                        allFragments.add(s);
                        if(allFragments.size()==1)
                        {
                             System.out.println("Final string is: "+s+" and has been written in result.txt file in fasta format");
                            try
                            {
                                FileOutputStream fostream = new FileOutputStream(outputFile);
                                String text = ">FASTA";
                                byte[] mybytes = text.getBytes();
                                fostream.write(mybytes);
                                while(s.length()!=0) {
                                    String rem="";
                                    if(s.length()>80)
                                    {
                                        rem = s.substring(80,s.length());
                                        s=s.substring(0,80);
                                    }
                                    String nextLine = "\n";
                                    mybytes = nextLine.getBytes();
                                    fostream.write(mybytes);
                                    mybytes = s.getBytes();
                                    fostream.write(mybytes);
                                    s=rem;
                                }
                            }
                            catch (Exception e) {  System.err.println("Error: " + e.getMessage());       }
                        }

                    }
                }
            }
        }

    }


    static int[][] dpMatrix(String f1,String f2,int delPenalty,int repPenalty,int matchScore)
    {
        int max=Integer.MIN_VALUE;
        int[][] dp = new int[f1.length() + 1][f2.length() + 1];

        for (int i = 0; i < dp.length; i++)
            dp[i][0] = 0;

        for (int i = 0; i < dp[0].length; i++)
            dp[0][i] = 0;

        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[0].length; j++) {
                int toInsert = dp[i - 1][j] + delPenalty;
                int toDelete = dp[i][j - 1] + delPenalty;
                int to_add_vAL = (f1.charAt(i - 1) == f2.charAt(j - 1)) ? matchScore : repPenalty;
                int toReplace = dp[i - 1][j - 1] + to_add_vAL;

                dp[i][j] = Math.max(Math.max(toInsert, toDelete), toReplace);
            }
        }
        return dp;
    }

    static int[] mergeScore(int[][] dp)
    {
        int[] result=new int[3];
        int colmax = Integer.MIN_VALUE;
        int rowmax = Integer.MIN_VALUE;
        int[] pos = new int[2];

        for (int i = 1; i < dp.length; i++) {
            if (dp[i][dp[0].length - 1] > colmax) {
                colmax = dp[i][dp[0].length - 1];
                pos[0] = i;
            }
        }

        for (int i = 1; i < dp[0].length; i++) {
            if (dp[dp.length - 1][i] > rowmax) {
                rowmax = Math.max(dp[dp.length - 1][i], rowmax);
                pos[1] = i;
            }
        }
        int max = Math.max(colmax, rowmax);
        int i = 0, j = 0;
        if (max == colmax) {
            i = pos[0];
            j = dp[0].length - 1;
        } else {
            i = dp.length - 1;
            j = pos[1];
        }

        result[0]=max;
        result[1]=i;
        result[2]=j;
        return result;
    }
    static String mergehelper(String f1,String f2,int delPenalty, int repPenalty,int matchScore,int[]temp,int[][] dp) {

        int i=temp[1];
        int j=temp[2];

        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        while (i != 1 && j != 1) {

            if (dp[i][j] == dp[i - 1][j - 1] + matchScore && f1.charAt(i - 1) == f2.charAt(j - 1)) {

                result1.append(f1.charAt(i - 1));
                result2.append(f2.charAt(j - 1));
                i--;
                j--;
            } else if (dp[i][j] == dp[i - 1][j - 1] + repPenalty) {

                result1.append(f1.charAt(i - 1));
                result2.append(f2.charAt(j - 1));
                i--;
                j--;
            } else if (dp[i][j] == dp[i][j - 1] + delPenalty) {
                result2.append(f2.charAt(j - 1));
                j--;
            } else if (dp[i][j] == dp[i - 1][j] + delPenalty) {

                result1.append(f1.charAt(i - 1));

                i--;
            } else {
            }
        }
        String x = "";
        String y = "";
        String t = "";

        if (j != 1) {

            x = f2.substring(0, j);
            t = x + result1.reverse();
            if(temp[1]==dp.length-1){
                t=t+f2.substring(temp[2],f2.length());
            }
            else if(temp[2]==dp[0].length-1)
            {
                t=t+f1.substring(temp[1],f1.length());
            }
        }

        else if (i != 1) {

            if(temp[2]==dp[0].length-1)
            {
                t=t+f2;
                t=f1.substring(0,i);
                t=t+f1.substring(temp[1],f1.length());

            }
            if(temp[1]==dp.length-1)
            {
                t=t+f2.substring(temp[2],f2.length());
                t=t+result1.reverse();
                t=t+f1.substring(0,i);
            }

        }
        if(i==1 && j==1)
        {
            t=f1;
        }

        StringBuilder g=new StringBuilder();
        return t.toString();

    }


}
