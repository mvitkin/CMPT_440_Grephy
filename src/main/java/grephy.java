public class grephy {

    public static String NFA_FILE;
    public static String DFA_FILE;
    public static String REGEX;
    public static String INPUT_FILE;

    public static void main (String[] args){

        if (args.length > 0){
            int i = 0;
            while(i < args.length){
                if(args[i].equals("-n")){
                    i++;
                    NFA_FILE = args[i];
                } else if(args[i].equals("-d")){
                    i++;
                    DFA_FILE = args[i];
                } else {
                    if (REGEX == null){
                        REGEX = args[i];
                    } else if (INPUT_FILE == null){
                        INPUT_FILE = args[i];
                    } else {
                        // TOO MANY ARGUMENTS ERROR
                    }
                }

                i++;
            }
        } else {
            System.out.println("no args");
        }

    }
}
