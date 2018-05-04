import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Grephy {

    public static String NFA_FILE;
    public static String DFA_FILE;
    public static String REGEX;
    public static String INPUT_FILE;

    public static void main (String[] args){

        if(!handleErrors(args)){
            System.out.println("NFA_FILE: " + NFA_FILE);
            System.out.println("DFA_FILE: " + DFA_FILE);
            System.out.println("REGEX: " + REGEX);
            System.out.println("INPUT_FILE: "+ INPUT_FILE);

            try {

                File nfa_file = new File(NFA_FILE);

                if (!nfa_file.exists()) {
                   System.out.println("FILE DOES NOT EXIST");
                } else {
                    FileWriter nfa_fw = new FileWriter(nfa_file);
                    BufferedWriter nfa_bw = new BufferedWriter(nfa_fw);
                    PrintWriter nfa_pw = new PrintWriter(nfa_bw);

                    RegexToNFA.NFA nfa = RegexToNFA.compile(REGEX);
                    nfa_pw.write(nfa.toString());
                    System.out.println("File Written Successfully");
                    nfa_pw.close();

                    NFAToDFA(nfa);
                }

            } catch (IOException ioe){
                ioe.printStackTrace();
            }


        }

    }

    public static boolean handleErrors(String[] args){
        boolean error = false;

        if (args.length > 0){
            int i = 0;
            while(i < args.length){
                if(args[i].equals("-n")){
                    if(NFA_FILE == null){
                        i++;
                        NFA_FILE = args[i];
                    } else {
                        error = true;
                        // TODO: DOUBLE ARGUMENT ERROR
                    }
                } else if(args[i].equals("-d")){
                    if(DFA_FILE == null){
                        i++;
                        DFA_FILE = args[i];
                    } else {
                        error = true;
                        // TODO: DOUBLE ARGUMENT ERROR
                    }
                } else {
                    if (REGEX == null){
                        REGEX = args[i];
                    } else if (INPUT_FILE == null){
                        INPUT_FILE = args[i];
                    } else {
                        error = true;
                        // TODO: TOO MANY ARGUMENTS ERROR
                    }
                }

                i++;
            }
        } else {
            error = true;
            // TODO: NO ARGUMENTS ERROR
        }

        return error;
    }

    public static void NFAToDFA(RegexToNFA.NFA nfa){
         NFAToDFA.DFA dfa = NFAToDFA.generateDFA(nfa);
         dfa.display();
    }
}
