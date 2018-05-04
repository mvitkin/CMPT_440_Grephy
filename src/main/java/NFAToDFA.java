import java.util.HashSet;
import java.util.Collections;
import java.util.ArrayList;

public class NFAToDFA {

    public static class Trans{
        public HashSet<Integer> states_from, states_to;
        public char trans_symbol;

        public Trans(HashSet<Integer> v1, HashSet<Integer> v2, char sym){
            this.states_from = v1;
            this.states_to = v2;
            this.trans_symbol = sym;
        }
    }

    public static class DFA{

        public ArrayList <HashSet<Integer>> states;
        public ArrayList <Trans> transitions;
        public ArrayList <HashSet<Integer>> final_states;
        public RegexToNFA.NFA nfa;
        public ArrayList <Character> alphabet;

        public DFA(RegexToNFA.NFA nfa){
            this.states = new ArrayList <HashSet<Integer>> ();
            this.transitions = new ArrayList <Trans> ();
            this.final_states = new ArrayList <HashSet<Integer>> ();
            this.nfa = nfa;
            this.alphabet = nfa.generateAlphabet();
        }

        public void epsClosure (HashSet<Integer> stateSet){
            ArrayList<Integer> state = new ArrayList<Integer>(stateSet);
            int i = 0;
            while (i < state.size()){
                HashSet<Integer> toState = this.nfa.findStatesFrom(state.get(i));

                for(int x : toState){
                    if(!state.contains(x)){
                        state.add(x);
                    }
                }
                i++;
            }
            stateSet.clear();
            for(int x : state){
                stateSet.add(x);
            }
        }

        public void generateStartState(){
            HashSet<Integer> startState = new HashSet<Integer>();
            startState.add(this.nfa.states.get(0));
            epsClosure(startState);
            this.states.add(startState);
        }

        public void populateTransition(HashSet<Integer> state, char c){
            HashSet<Integer> newState = new HashSet<Integer>();
            for (int i : state){
                HashSet<Integer> toState = this.nfa.findStatesFrom(i, c);

                for(int x : toState){
                    if(!newState.contains(x)){
                        newState.add(x);
                    }
                }
            }

            epsClosure(newState);
            if(newState.size() > 0) {
                if (!this.states.contains(newState)) {
                    this.states.add(newState);
                }
                this.transitions.add(new Trans(state, newState, c));
            }
        }

        public void populateStateTransitions(HashSet<Integer> state){
            for(char c: this.alphabet){
                populateTransition(state, c);
            }
        }

        public void display(){
            System.out.println("States: ");
            for (HashSet<Integer> state : this.states){
                System.out.println(state);
            }
            System.out.println("Transitions: ");
            for (Trans t: this.transitions){
                System.out.println("(" + t.states_from + ", " + t.trans_symbol + ") = " + t.states_to);
            }
            System.out.println("Accepting States: ");
            for (HashSet<Integer> state : this.final_states){
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
        for(HashSet<Integer> state : dfa.states){
            if(state.contains(dfa.nfa.final_state)){
                dfa.final_states.add(state);
            }
        }
        return dfa;
    }
}
