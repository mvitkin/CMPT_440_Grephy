public class grephy {

    public static String NFA_FILE;
    public static String DFA_FILE;
    public static String REGEX;
    public static String INPUT_FILE;

    public static void main (String[] args){

        boolean error = false;

        if (args.length > 0){
            int i = 0;
            while(i < args.length){
                if(args[i].equals("-n")){
                    if(REGEX == null && INPUT_FILE == null){
                        i++;
                        NFA_FILE = args[i];
                    } else {
                        error = true;
                        // TODO: WRONG ARGUMENTS ORDER ERROR
                    }
                } else if(args[i].equals("-d")){
                    if(REGEX == null && INPUT_FILE == null){
                        i++;
                        DFA_FILE = args[i];
                    } else {
                        error = true;
                        // TODO: WRONG ARGUMENTS ORDER ERROR
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

        if(!error){
            System.out.println("NFA_FILE: " + NFA_FILE);
            System.out.println("DFA_FILE: " + DFA_FILE);
            System.out.println("REGEX: " + REGEX);
            System.out.println("INPUT_FILE: "+ INPUT_FILE);

            RegexToNFA.NFA nfa = RegexToNFA.compile(REGEX);
            nfa.display();
        }

    }
}
