import java.util.ArrayList;
import java.util.Stack;

public class RegexToNFA {

    /*
       Trans - object is used as a tuple of 3 items to depict transitions
           (state from, symbol of transition path, state to)
   */
    public static class Trans{
        public int state_from, state_to;
        public char trans_symbol;
        public boolean epsilon;

        public Trans(int v1, int v2, char sym){
            this.state_from = v1;
            this.state_to = v2;
            this.trans_symbol = sym;
            this.epsilon = false;
        }

        public Trans(int v1, int v2){
            this.state_from = v1;
            this.state_to = v2;
            this.epsilon = true;
        }
    }

    /*
        NFA - serves as the graph that represents the Non-Deterministic
            Finite Automata.
    */
    public static class NFA{
        public ArrayList <Integer> states;
        public ArrayList <Trans> transitions;
        public int final_state;

        public NFA(){
            this.states = new ArrayList <Integer> ();
            this.transitions = new ArrayList <Trans> ();
            this.final_state = 0;
        }
        public NFA(int size){
            this.states = new ArrayList <Integer> ();
            this.transitions = new ArrayList <Trans> ();
            this.final_state = 0;
            this.setStateSize(size);
        }
        public NFA(char c){
            this.states = new ArrayList<Integer> ();
            this.transitions = new ArrayList <Trans> ();
            this.setStateSize(2);
            this.final_state = 1;
            this.transitions.add(new Trans(0, 1, c));
        }

        public void setStateSize(int size){
            for (int i = 0; i < size; i++)
                this.states.add(i);
        }

        public void display(){
            for (Trans t: transitions){
                if (!t.epsilon) {
                    System.out.println("(" + t.state_from + ", " + t.trans_symbol +
                            ") = " + t.state_to);
                } else {
                    System.out.println("(" + t.state_from + ", eps) = " + t.state_to);
                }
            }

            System.out.println("Final State: " + final_state);
        }

        public String toString(){
            String s = "";
            for (Trans t: transitions){
                if (!t.epsilon) {
                    s = s + "(" + t.state_from + ", " + t.trans_symbol +
                            ") = " + t.state_to;
                } else {
                    s = s + "(" + t.state_from + ", eps) = " + t.state_to;
                }
                s = s + "\n";
            }
            return s;
        }

        public ArrayList<Integer> findStatesFrom (int state_from){
            ArrayList<Integer> a = new ArrayList<Integer>();
            for(int i = 0; i < this.transitions.size(); i++){
                Trans t = this.transitions.get(i);
                if(t.state_from == state_from){
                    if(t.epsilon){
                        a.add(t.state_to);
                    }
                }
            }
            return a;
        }

        public ArrayList<Integer> findStatesFrom (int state_from, char sym){
            ArrayList<Integer> a = new ArrayList<Integer>();
            for(int i = 0; i < this.transitions.size(); i++){
                Trans t = this.transitions.get(i);
                if(t.state_from == state_from){
                    if(t.trans_symbol == sym){
                        a.add(t.state_to);
                    }
                }
            }
            return a;
        }

        public ArrayList<Character> generateAlphabet (){
            ArrayList<Character> a = new ArrayList<Character>();
            for(Trans t : this.transitions){
                if(!t.epsilon) {
                    if (!a.contains(t.trans_symbol)) {
                        a.add(t.trans_symbol);
                    }
                }
            }
            return a;
        }
    }

    /*
        kleene() - Highest Precedence regular expression operator. Thompson
            algorithm for kleene star.
    */
    public static NFA kleene(NFA n){
        NFA result = new NFA(n.states.size()+2);
        result.transitions.add(new Trans(0, 1)); // new trans for q0

        // copy existing transitions
        for (Trans t: n.transitions){
            if(!t.epsilon) {
                result.transitions.add(new Trans(t.state_from + 1,
                        t.state_to + 1, t.trans_symbol));
            } else {
                result.transitions.add(new Trans(t.state_from + 1,
                        t.state_to + 1));
            }
        }

        // add empty transition from final n state to new final state.
        result.transitions.add(new Trans(n.states.size(), n.states.size() + 1));

        // Loop back from last state of n to initial state of n.
        result.transitions.add(new Trans(n.states.size(), 1));

        // Add empty transition from new initial state to new final state.
        result.transitions.add(new Trans(0, n.states.size() + 1));

        result.final_state = n.states.size() + 1;
        return result;
    }

    /*
        concat() - Thompson algorithm for concatenation. Middle Precedence.
    */
    public static NFA concat(NFA n, NFA m){

        m.states.remove(0); // delete m's initial state

        // copy NFA m's transitions to n, and handles connecting n & m
        for (Trans t: m.transitions){
            if(!t.epsilon) {
                n.transitions.add(new Trans(t.state_from + n.states.size() - 1,
                        t.state_to + n.states.size() - 1, t.trans_symbol));
            } else {
                n.transitions.add(new Trans(t.state_from + n.states.size() - 1,
                        t.state_to + n.states.size() - 1));
            }
        }

        // take m and combine to n after erasing initial m state
        for (Integer s: m.states){
            n.states.add(s + n.states.size() + 1);
        }

        n.final_state = n.states.size() + m.states.size() - 2;
        return n;
    }

    /*
        union() - Lowest Precedence regular expression operator. Thompson
            algorithm for union (or).
    */
    public static NFA union(NFA n, NFA m){
        NFA result = new NFA(n.states.size() + m.states.size() + 2);

        // the branching of q0 to beginning of n
        result.transitions.add(new Trans(0, 1));

        // copy existing transitions of n
        for (Trans t: n.transitions){
            if(!t.epsilon) {
                result.transitions.add(new Trans(t.state_from + 1,
                        t.state_to + 1, t.trans_symbol));
            } else {
                result.transitions.add(new Trans(t.state_from + 1,
                        t.state_to + 1));
            }
        }

        // transition from last n to final state
        result.transitions.add(new Trans(n.states.size(),
                n.states.size() + m.states.size() + 1));

        // the branching of q0 to beginning of m
        result.transitions.add(new Trans(0, n.states.size() + 1));

        // copy existing transitions of m
        for (Trans t: m.transitions){
            if(!t.epsilon) {
                result.transitions.add(new Trans(t.state_from + n.states.size()
                        + 1, t.state_to + n.states.size() + 1, t.trans_symbol));
            } else {
                result.transitions.add(new Trans(t.state_from + n.states.size()
                        + 1, t.state_to + n.states.size() + 1));
            }
        }

        // transition from last m to final state
        result.transitions.add(new Trans(m.states.size() + n.states.size(),
                n.states.size() + m.states.size() + 1));

        // 2 new states and shifted m to avoid repetition of last n & 1st m
        result.final_state = n.states.size() + m.states.size() + 1;
        return result;
    }

    // simplify the repeated boolean condition checks
    // public static boolean alpha(char c){ return (!regexOperator(c));}
    public static boolean alphabet(char c){ return (!regexOperator(c));}
    public static boolean regexOperator(char c){
        return c == '(' || c == ')' || c == '*' || c == '|';
    }
    public static boolean validRegExChar(char c){
        return alphabet(c) || regexOperator(c);
    }
    // validRegEx() - checks if given string is a valid regular expression.
    public static boolean validRegEx(String regex){
        if (regex.isEmpty())
            return false;
        for (char c: regex.toCharArray())
            if (!validRegExChar(c))
                return false;
        return true;
    }

    /*
        compile() - compile given regular expression into a NFA using
            Thompson Construction Algorithm. Will implement typical compiler
            stack model to simplify processing the string. This gives
            descending precedence to characters on the right.
    */
    public static NFA compile(String regex){
        if (!validRegEx(regex)){
            System.out.println("Invalid Regular Expression Input.");
            return new NFA(); // empty NFA if invalid regex
        }

        Stack <Character> operators = new Stack <Character> ();
        Stack <NFA> operands = new Stack <NFA> ();
        Stack <NFA> concat_stack = new Stack <NFA> ();
        boolean ccflag = false; // concat flag
        char op, c; // current character of string
        int para_count = 0;
        NFA nfa1, nfa2;

        for (int i = 0; i < regex.length(); i++){
            c = regex.charAt(i);
            if (alphabet(c)){
                operands.push(new NFA(c));
                if (ccflag){ // concat this w/ previous
                    operators.push('.'); // '.' used to represent concat.
                }
                else
                    ccflag = true;
            }
            else{
                if (c == ')'){
                    ccflag = false;
                    if (para_count == 0){
                        System.out.println("Error: More end parentheses "+
                                "than beginning parentheses");
                        System.exit(1);
                    }
                    else{ para_count--;}
                    // process stuff on stack till '('
                    while (!operators.empty() && operators.peek() != '('){
                        op = operators.pop();
                        if (op == '.'){
                            nfa2 = operands.pop();
                            nfa1 = operands.pop();
                            operands.push(concat(nfa1, nfa2));
                        }
                        else if (op == '|'){
                            nfa2 = operands.pop();

                            if(!operators.empty() &&
                                    operators.peek() == '.'){

                                concat_stack.push(operands.pop());
                                while (!operators.empty() &&
                                        operators.peek() == '.'){

                                    concat_stack.push(operands.pop());
                                    operators.pop();
                                }
                                nfa1 = concat(concat_stack.pop(),
                                        concat_stack.pop());
                                while (concat_stack.size() > 0){
                                    nfa1 =  concat(nfa1, concat_stack.pop());
                                }
                            }
                            else{
                                nfa1 = operands.pop();
                            }
                            operands.push(union(nfa1, nfa2));
                        }
                    }
                }
                else if (c == '*'){
                    operands.push(kleene(operands.pop()));
                    ccflag = true;
                }
                else if (c == '('){ // if any other operator: push
                    operators.push(c);
                    para_count++;
                }
                else if (c == '|'){
                    operators.push(c);
                    ccflag = false;
                }
            }
        }
        while (operators.size() > 0){
            if (operands.empty()){
                System.out.println("Error: imbalance in operands and operators");
                System.exit(1);
            }
            op = operators.pop();
            if (op == '.'){
                nfa2 = operands.pop();
                nfa1 = operands.pop();
                operands.push(concat(nfa1, nfa2));
            }
            else if (op == '|'){
                nfa2 = operands.pop();
                if( !operators.empty() && operators.peek() == '.'){
                    concat_stack.push(operands.pop());
                    while (!operators.empty() && operators.peek() == '.'){
                        concat_stack.push(operands.pop());
                        operators.pop();
                    }
                    nfa1 = concat(concat_stack.pop(),
                            concat_stack.pop());
                    while (concat_stack.size() > 0){
                        nfa1 =  concat(nfa1, concat_stack.pop());
                    }
                }
                else{
                    nfa1 = operands.pop();
                }
                operands.push(union(nfa1, nfa2));
            }
        }
        return operands.pop();
    }

}
