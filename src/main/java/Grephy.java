import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.FileReader;

public class Grephy {

    public static void main (String[] args){

        Grephy g = handleInput(args);
        g.nfa = RegexToNFA.compile(g.REGEX);
        g.dfa = NFAToDFA.generateDFA(g.nfa);

        if(g.nfa.states.size() <= 0){
            System.exit(6);
        }

        if(g.NFA_FILE != null){
            g.printDOTNFA();
        }
        if(g.DFA_FILE != null){
            g.printDOTDFA();
        }

        g.parseInput();
    }


    public File NFA_FILE;
    public File DFA_FILE;
    public String REGEX;
    public File INPUT_FILE;
    public RegexToNFA.NFA nfa;
    public NFAToDFA.DFA dfa;

    public Grephy(String nfa_file, String dfa_file, String regex, String input_file ){
        NFA_FILE = new File(nfa_file);
        DFA_FILE = new File(dfa_file);
        REGEX = regex;
        INPUT_FILE = new File(input_file);
        nfa = null;
        dfa = null;

    }

    public static Grephy handleInput(String[] args){
        boolean error =false;

        String NFA_FILE = null;
        String DFA_FILE = null;
        String REGEX = null;
        String INPUT_FILE = null;

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
        if(error) {
            System.exit(2);
        }
        return new Grephy(NFA_FILE, DFA_FILE, REGEX, INPUT_FILE);
    }

    public boolean printDOTNFA(){
        try {
            if (!NFA_FILE.exists()) {
                System.out.println("NFA FILE DOES NOT EXIST");
                return false;
            } else {
                FileWriter fw = new FileWriter(NFA_FILE);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);

                pw.println("digraph d{");
                pw.print("node [shape = doublecircle]; " + nfa.final_state);
                pw.println("node [shape = circle];");

                for(RegexToNFA.Trans t: nfa.transitions){
                    if(!t.epsilon) {
                        pw.println("\t" + t.state_from + " -> " + t.state_to + " [label=" + t.trans_symbol + "];");
                    } else {
                        pw.println("\t" + t.state_from + " -> " + t.state_to + " [label=\"eps\"];");
                    }
                }

                pw.println("}");

                System.out.println("NFA File Written Successfully");
                pw.close();
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean printDOTDFA(){
        try {
            if (!DFA_FILE.exists()) {
                System.out.println("DFA FILE DOES NOT EXIST");
                return false;
            } else {
                FileWriter fw = new FileWriter(DFA_FILE);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);

                pw.println("digraph g{");
                pw.print("node [shape = doublecircle]; ");

                for(HashSet<Integer> set : dfa.final_states){
                    pw.print("\"" + set + "\" ");
                }
                pw.println();

                pw.println("node [shape = circle];");

                for(NFAToDFA.Trans t: dfa.transitions){
                    if(!t.error) {
                        pw.println("\t\"" + t.states_from + "\" -> \"" + t.states_to + "\" [label=" + t.trans_symbol + "];");
                    } else {
                        pw.println("\t\"" + t.states_from + "\" -> \"error\" [label=" + t.trans_symbol + "];");
                    }
                }

                for(NFAToDFA.Trans t: dfa.transitions){
                    if(t.error){
                        pw.print("\t\"error\" -> \"error\" [label=\"");
                        for(int i = 0; i < dfa.alphabet.size(); i++){
                            pw.print(dfa.alphabet.get(i));
                            if(i < dfa.alphabet.size()-1){
                                pw.print(", ");
                            }
                        }
                        pw.print("\"];");
                        pw.println();
                        break;
                    }
                }

                pw.println("}");

                System.out.println("DFA File Written Successfully");
                pw.close();
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public void parseInput(){
        try {
            BufferedReader bf = new BufferedReader(new FileReader(INPUT_FILE));

            String line;
            while((line = bf.readLine()) != null){
                if(traverseDFA(line)){
                    System.out.println(line);
                }
            }

        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean traverseDFA(String s){
        boolean accepts = false;
        HashSet<Integer> curState = dfa.states.get(0);
        NFAToDFA.Trans trans;

        if(dfa.final_states.contains(curState)){
            return true;
        }

        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(dfa.alphabet.contains(c)){
                trans = dfa.findTrans(curState, c);
                if(trans.error){
                    accepts = false;
                    break;
                } else {
                    curState = trans.states_to;
                }
            } else {
                accepts = false;
                break;
            }
            if(dfa.final_states.contains(curState)){
                accepts = true;
            }
        }

        return accepts;
    }
}
