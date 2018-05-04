import java.util.ArrayList;
import java.util.Collections;

public class NFAToDFA {

    public static class Trans{
        public ArrayList<Integer> states_from, states_to;
        public char trans_symbol;

        public Trans(ArrayList<Integer> v1, ArrayList<Integer> v2, char sym){
            this.states_from = v1;
            this.states_to = v2;
            this.trans_symbol = sym;
        }
    }

    public static class DFA{

        public ArrayList <ArrayList<Integer>> states;
        public ArrayList <Trans> transitions;
        public ArrayList <ArrayList<Integer>> final_states;
        public RegexToNFA.NFA nfa;
        public ArrayList <Character> alphabet;

        public DFA(RegexToNFA.NFA nfa){
            this.states = new ArrayList <ArrayList<Integer>> ();
            this.transitions = new ArrayList <Trans> ();
            this.final_states = new ArrayList <ArrayList<Integer>> ();
            this.nfa = nfa;
            this.alphabet = nfa.generateAlphabet();
        }

        public void epsClosure (ArrayList<Integer> state){
            int i = 0;
            while (i < state.size()){
                ArrayList<Integer> toState = this.nfa.findStatesFrom(state.get(i));

                for(int x : toState){
                    if(!state.contains(x)){
                        state.add(x);
                    }
                }
            }
            Collections.sort(state);
        }

        public void generateStartState(){
            ArrayList<Integer> startState = new ArrayList<Integer>();
            startState.add(this.nfa.states.get(0));
            epsClosure(startState);
        }

        public void populateTransition(ArrayList<Integer> state, char c){
            ArrayList<Integer> newState = new ArrayList<Integer>();
            for (int i : state){
                ArrayList<Integer> toState = this.nfa.findStatesFrom(i, c);

                for(int x : toState){
                    if(!newState.contains(x)){
                        newState.add(x);
                    }
                }
            }

            epsClosure(newState);
            Collections.sort(newState);

            if(!this.states.contains(newState)){
                this.states.add(newState);
            }
            this.transitions.add(new Trans(state, newState, c));
        }

        public void populateStateTransitions(ArrayList<Integer> state){
            for(char c: this.alphabet){
                populateTransition(state, c);
            }
        }

        public void display(){
            System.out.println("States: ");
            for (ArrayList<Integer> state : this.states){
                System.out.println(state);
            }
            System.out.println("Transitions: ");
            for (Trans t: this.transitions){
                System.out.println("(" + t.states_from + ", " + t.trans_symbol + ") = " + t.states_to);
            }
            System.out.println("Accepting States: ");
            for (ArrayList<Integer> state : this.final_states){
                System.out.println(state);
            }
        }

    }

    public static DFA generateDFA(RegexToNFA.NFA nfa){
        DFA dfa = new DFA(nfa);
        dfa.generateStartState();
        int i = 0;
        while(i < dfa.states.size()){
            dfa.populateStateTransitions(dfa.states.get(i));
            i++;
        }
        // find accepting states
        for(ArrayList<Integer> state : dfa.states){
            if(state.contains(dfa.nfa.final_state)){
                dfa.final_states.add(state);
            }
        }
        return dfa;
    }
}
