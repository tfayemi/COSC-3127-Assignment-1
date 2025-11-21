/**
 * Implementation of a Deterministic Finite Automaton (DFA) for COSC 3127 Assignment 1.
 * The Lexical Analyzer will use this DFA to recognize tokens in the input source code.
 * The Language will accept two data types: integer and real.
 * The assignment operator is ':=' and the supported arithmetic operators are +, -, *, / and ^.
 */

package mini; 
/**FOR-GROUP: in the files you showed me, I noticed you used "mini" for the Java package. Is this what 
 * we want to use? I'll keep it for now, but please let me know if you want to change it, I'll try to
 * remember to ask you next time.**/

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class DFA {
    private final String startState; // The start state of the DFA
    private final Set<String> acceptStates; // The set of accept states
    private final Map<String, Map<Character, String>> transitionTable; // The transition table

    /** Constructor to initialize the DFA 
     * @param startState The start state of the DFA
     * @param acceptStates The set of accept states
     * @param transitionTable The transition table
    */

    public DFA(String startState, Set<String> acceptStates, Map<String, Map<Character, String>> transitionTable) {
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.transitionTable = transitionTable;
    }

    /** Ayush had the idea to make the DFA modular so we can add some
     * transitions later - trying this here.
      */
    /** Add transition to DFA 
     * @param fromState The state from which the transition occurs
     * @param inputChar The input character that triggers the transition
     * @param toState The state to which the transition goes
    */
    public void addTransition(String fromState, char inputChar, String toState) {
        transitionTable.putIfAbsent(fromState, new HashMap<>());
        transitionTable.get(fromState).put(inputChar, toState);
    }

    /** Add transition for a range of characters (e.g., 'a' to 'z') 
     * @param fromState The state from which the transition occurs
     * @param startChar The starting character of the range
     * @param endChar The ending character of the range
     * @param toState The state to which the transition goes
    */

    public void addTransitionRange(String fromState, char startChar, char endChar, String toState) {
        transitionTable.putIfAbsent(fromState, new HashMap<>());
        for (char c = startChar; c <= endChar; c++) {
            addTransition(fromState, c, toState);
        }
    }

    /** Run DFA on input string 
     * @param input The input string to be processed by the DFA
     * @return true if the input is accepted by the DFA, false otherwise
    */
   public boolean run(String input) {
        String currentState = startState;
        for (char c : input.toCharArray()) {
            Map<Character, String> transitions = transitionTable.get(currentState);
            if (transitions == null || !transitions.containsKey(c)) {
                return false; // No valid transition, reject the input
            }
            currentState = transitions.get(c); // Move to next state
        }
        return acceptStates.contains(currentState); // Accept if in an accept state    
    }

    /** Run the DFA and return the longest accepted prefix of the input string
     * @param input The input string to be processed by the DFA
     * @return The longest accepted prefix of the input string, or -1 if none is accepted
    */
   public int matchLength(String input, int startIndex){
        String currentState = startState;
        int lastAcceptIndex = -1;

        for (int i = startIndex; i < input.length(); i++) {
            char c = input.charAt(i);
            Map<Character, String> transitions = transitionTable.get(currentState);
            if (transitions == null || !transitions.containsKey(c)) {
                break; // No valid transition, stop processing
            }
            currentState = transitions.get(c); // Move to next state
            if (acceptStates.contains(currentState)) {
                lastAcceptIndex = i - startIndex + 1; // Update last accept index
            }
        }
        return lastAcceptIndex; // Return the index of the last accepted character, or -1 if none
   }
   
   /**
    * Creates Identifier DFA
    * Pattern:[a-zA-Z_][A-Za-z0-9_]*
    * @return DFA for Identifiers
    */
    public static DFA createIdentifierDFA() {
          String startState = "START";
          Set<String> acceptStates = new HashSet<>();
          acceptStates.add("IDENTIFIER");
    
          Map<String, Map<Character, String>> transitionTable = new HashMap<>();
    
          // Transitions from START state
          Map<Character, String> startTransitions = new HashMap<>();
          for (char c = 'a'; c <= 'z'; c++) {
                startTransitions.put(c, "IDENTIFIER");
          }
          for (char c = 'A'; c <= 'Z'; c++) {
                startTransitions.put(c, "IDENTIFIER");
          }
          startTransitions.put('_', "IDENTIFIER");
          transitionTable.put("START", startTransitions);
    
          // Transitions from IDENTIFIER state
          Map<Character, String> identifierTransitions = new HashMap<>();
          for (char c = 'a'; c <= 'z'; c++) {
                identifierTransitions.put(c, "IDENTIFIER");
          }
          for (char c = 'A'; c <= 'Z'; c++) {
                identifierTransitions.put(c, "IDENTIFIER");
          }
          for (char c = '0'; c <= '9'; c++) {
                identifierTransitions.put(c, "IDENTIFIER");
          }
          identifierTransitions.put('_', "IDENTIFIER");
          transitionTable.put("IDENTIFIER", identifierTransitions);
    
          return new DFA(startState, acceptStates, transitionTable);
     }

     /**
      * Integer DFA
      * Pattern: [0-9]+
      * @return DFA for Integers
      */
        public static DFA createIntegerDFA() {
            String startState = "START";
            Set<String> acceptStates = new HashSet<>();
            acceptStates.add("INTEGER");

            DFA integerDFA = new DFA(startState, acceptStates, new HashMap<>());

            // From START start, digit goes to INTEGER state
            integerDFA.addTransitionRange("START", '0', '9', "INTEGER");

            // From INTEGER state, digit can continue with more digits
            integerDFA.addTransitionRange("INTEGER", '0', '9', "INTEGER");

            return integerDFA;
        }

        /** Create Real Number Literal DFA
         * Pattern: [0-9]+\.[0-9]+
         * @return DFA for Real Number Literals
         */

        public static DFA createRealDFA() {
            String startState = "START";
            Set<String> acceptStates = new HashSet<>();
            acceptStates.add("FRACTIONAL_PART"); // Only accept after decimal digits

            DFA realDFA = new DFA(startState, acceptStates, new HashMap<>());

            // From START state, digit goes to INTEGER_PART state
            realDFA.addTransitionRange("START", '0', '9', "INTEGER_PART");

            // From INTEGER_PART state, digit can continue with more digits
            realDFA.addTransitionRange("INTEGER_PART", '0', '9', "INTEGER_PART");

            // From INTEGER_PART state, '.' goes to DECIMAL_POINT state
            realDFA.addTransition("INTEGER_PART", '.', "DECIMAL_POINT");

            // From DECIMAL_POINT state, digit goes to FRACTIONAL_PART state
            realDFA.addTransitionRange("DECIMAL_POINT", '0', '9', "FRACTIONAL_PART");

            // From FRACTIONAL_PART state, digit can continue with more digits
            realDFA.addTransitionRange("FRACTIONAL_PART", '0', '9', "FRACTIONAL_PART");

            return realDFA;
        }

    /** Create Keyword DFA
     * @param keyword The keyword to be recognized by the DFA 
     * @return DFA for the specified keyword
    */
        public static DFA createKeywordDFA(String keyword){
            Set<String> acceptStates = new HashSet<>();
            acceptStates.add("ACCEPT");
            
            DFA keywordDFA = new DFA("START", acceptStates, new HashMap<>());

            String currentState = "START";
            for (int i = 0; i < keyword.length(); i++) {
                char c = keyword.charAt(i);
                String nextState = (i == keyword.length() - 1) ? "ACCEPT" : "STATE_" + (i + 1);
                keywordDFA.addTransition(currentState, c, nextState);
                currentState = nextState;

            }

            return keywordDFA;
            
        }

    /** Assignment Operator DFA
     * Pattern: :=
     * @return DFA for Assignment Operator
     */
    public static DFA createAssignmentOperatorDFA() {
        String startState = "START";
        Set<String> acceptStates = new HashSet<>();
        acceptStates.add("ASSIGNMENT_OPERATOR");

        DFA assignmentDFA = new DFA(startState, acceptStates, new HashMap<>());

        // From START state, ':' goes to COLON state
        assignmentDFA.addTransition("START", ':', "COLON");

        // From COLON state, '=' goes to ASSIGNMENT_OPERATOR state
        assignmentDFA.addTransition("COLON", '=', "ASSIGNMENT_OPERATOR");

        return assignmentDFA;
    }

    /** Operator DFA
     * Pattern: + - * / ^
     * @return DFA for Operators
     */
    public static DFA createOperatorDFA() {
        String startState = "START";
        Set<String> acceptStates = new HashSet<>();
        acceptStates.add("OPERATOR");

        DFA operatorDFA = new DFA(startState, acceptStates, new HashMap<>());

        // From START state, each operator goes to OPERATOR state
        operatorDFA.addTransition("START", '+', "OPERATOR");
        operatorDFA.addTransition("START", '-', "OPERATOR");
        operatorDFA.addTransition("START", '*', "OPERATOR");
        operatorDFA.addTransition("START", '/', "OPERATOR");
        operatorDFA.addTransition("START", '^', "OPERATOR");

        return operatorDFA;
    }

}
