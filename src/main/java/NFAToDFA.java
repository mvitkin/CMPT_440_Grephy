import java.util.ArrayList;
import java.util.Collections;

public class NFAToDFA {

    public static class Trans{
        public ArrayList<Integer> state_from, state_to;
        public char trans_symbol;

        public Trans(ArrayList<Integer> v1, ArrayList<Integer> v2, char sym){
            this.state_from = v1;
            this.state_to = v2;
            this.trans_symbol = sym;
        }
    }

    public class DFA{

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
            for (int i : state){
                ArrayList<Integer> toState = this.nfa.findStatesFrom(i);

                for(int x : toState){
                    if(!state.contains(x)){
                        state.add(x);
                    }
                }
            }
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



    }
}
