# DFA State and Character Mapping Guide

## Overview
This document explains how states and characters are mapped in the Deterministic Finite Automaton (DFA) implementation for the COSC 3127 Lexical Analyzer.

---

## Conceptual Structure

### The Transition Table: A Nested Map
The DFA uses a **nested map structure** to represent state transitions:

```
Map<String, Map<Character, String>> transitionTable
     │              │         │
     │              │         └─ Target State (String)
     │              └─────────── Input Character (Character)
     └────────────────────────── Current State (String)
```

This can be visualized as:

```
transitionTable = {
    "State1" -> { 'a' -> "State2", 'b' -> "State3" },
    "State2" -> { 'a' -> "State2", 'b' -> "State4" },
    "State3" -> { 'a' -> "State5", 'b' -> "State3" }
}
```

---

## Visual State Diagram Example

### Example 1: Simple Integer Recognition

Let's build a DFA that recognizes integers (sequences of digits):

```
     ┌─────┐    digit(0-9)    ┌─────┐
  ──>│ q0  │ ───────────────> │ q1  │
     │START│                  │ACCEPT│
     └─────┘                  └──┬──┘
                                 │
                                 │ digit(0-9)
                                 │
                                 └──┐
                                    │
                                    ▼
```

**State Transition Table:**
```
┌────────────┬─────────┬─────────┬─────────┬─────────┐
│ State/Char │    0    │    1    │   ...   │    9    │
├────────────┼─────────┼─────────┼─────────┼─────────┤
│    q0      │   q1    │   q1    │   q1    │   q1    │
│    q1      │   q1    │   q1    │   q1    │   q1    │
└────────────┴─────────┴─────────┴─────────┴─────────┘
```

**In Java Map Structure:**
```java
Map<String, Map<Character, String>> transitionTable = {
    "q0" -> { '0' -> "q1", '1' -> "q1", '2' -> "q1", ..., '9' -> "q1" },
    "q1" -> { '0' -> "q1", '1' -> "q1", '2' -> "q1", ..., '9' -> "q1" }
}

Set<String> acceptStates = { "q1" }
String startState = "q0"
```

**Example Trace:** Input = "123"
```
Step 0: currentState = "q0", read '1'
        → transitions = transitionTable.get("q0") = { '0'->"q1", '1'->"q1", ... }
        → transitions.get('1') = "q1"
        → currentState = "q1"

Step 1: currentState = "q1", read '2'
        → transitions = transitionTable.get("q1") = { '0'->"q1", '1'->"q1", ... }
        → transitions.get('2') = "q1"
        → currentState = "q1"

Step 2: currentState = "q1", read '3'
        → transitions = transitionTable.get("q1") = { '0'->"q1", '1'->"q1", ... }
        → transitions.get('3') = "q1"
        → currentState = "q1"

Final: currentState = "q1", is in acceptStates? YES ✓ → ACCEPT
```

---

## Example 2: Real Number Recognition

A more complex DFA that recognizes real numbers (e.g., "3.14", "0.5"):

```
               digit                    digit
     ┌─────┐   (0-9)   ┌─────┐   '.'   ┌─────┐   (0-9)   ┌─────┐
  ──>│ q0  │ ────────> │ q1  │ ──────> │ q2  │ ────────> │ q3  │
     │START│           │     │         │     │           │ACCEPT│
     └─────┘           └──┬──┘         └─────┘           └──┬──┘
                          │                                  │
                          │ digit                            │ digit
                          └──┐                               └──┐
                             │                                  │
                             ▼                                  ▼
```

**State Transition Table:**
```
┌────────────┬─────────┬─────────┬─────────┬─────────┬─────────┐
│ State/Char │    0    │    1    │   ...   │    9    │    .    │
├────────────┼─────────┼─────────┼─────────┼─────────┼─────────┤
│    q0      │   q1    │   q1    │   q1    │   q1    │    -    │
│    q1      │   q1    │   q1    │   q1    │   q1    │   q2    │
│    q2      │   q3    │   q3    │   q3    │   q3    │    -    │
│    q3      │   q3    │   q3    │   q3    │   q3    │    -    │
└────────────┴─────────┴─────────┴─────────┴─────────┴─────────┘
```
*(- means no transition, would reject)*

**In Java Map Structure:**
```java
Map<String, Map<Character, String>> transitionTable = {
    "q0" -> { '0' -> "q1", '1' -> "q1", ..., '9' -> "q1" },
    "q1" -> { '0' -> "q1", '1' -> "q1", ..., '9' -> "q1", '.' -> "q2" },
    "q2" -> { '0' -> "q3", '1' -> "q3", ..., '9' -> "q3" },
    "q3" -> { '0' -> "q3", '1' -> "q3", ..., '9' -> "q3" }
}

Set<String> acceptStates = { "q3" }  // Only q3 is accepting
String startState = "q0"
```

**Example Trace:** Input = "3.14"
```
Step 0: currentState = "q0", read '3'
        → transitions = transitionTable.get("q0")
        → transitions.get('3') = "q1"
        → currentState = "q1"

Step 1: currentState = "q1", read '.'
        → transitions = transitionTable.get("q1")
        → transitions.get('.') = "q2"
        → currentState = "q2"

Step 2: currentState = "q2", read '1'
        → transitions = transitionTable.get("q2")
        → transitions.get('1') = "q3"
        → currentState = "q3"

Step 3: currentState = "q3", read '4'
        → transitions = transitionTable.get("q3")
        → transitions.get('4') = "q3"
        → currentState = "q3"

Final: currentState = "q3", is in acceptStates? YES ✓ → ACCEPT
```

**Example Rejection:** Input = "3."
```
Step 0: currentState = "q0", read '3' → currentState = "q1"
Step 1: currentState = "q1", read '.' → currentState = "q2"

Final: currentState = "q2", is in acceptStates? NO ✗ → REJECT
```

---

## Example 3: Assignment Operator ":="

For recognizing the assignment operator ":=":

```
     ┌─────┐   ':'   ┌─────┐   '='   ┌─────┐
  ──>│ q0  │ ──────> │ q1  │ ──────> │ q2  │
     │START│         │     │         │ACCEPT│
     └─────┘         └─────┘         └─────┘
```

**State Transition Table:**
```
┌────────────┬─────────┬─────────┐
│ State/Char │    :    │    =    │
├────────────┼─────────┼─────────┤
│    q0      │   q1    │    -    │
│    q1      │    -    │   q2    │
│    q2      │    -    │    -    │
└────────────┴─────────┴─────────┘
```

**In Java Map Structure:**
```java
Map<String, Map<Character, String>> transitionTable = {
    "q0" -> { ':' -> "q1" },
    "q1" -> { '=' -> "q2" },
    "q2" -> { }  // No transitions from accepting state
}

Set<String> acceptStates = { "q2" }
String startState = "q0"
```

---

## Example 4: Identifier Recognition

Recognizes identifiers (e.g., "x", "var1", "total_sum"):

```
                letter                    letter/digit/_
     ┌─────┐  (a-z,A-Z)  ┌─────┐
  ──>│ q0  │ ──────────> │ q1  │
     │START│             │ACCEPT│
     └─────┘             └──┬──┘
                            │
                            │ letter/digit/_
                            │
                            └──┐
                               │
                               ▼
```

**State Transition Table:**
```
┌────────────┬─────────┬─────────┬─────────┬─────────┬─────────┐
│ State/Char │    a    │   ...   │    z    │    0    │   ...   │
├────────────┼─────────┼─────────┼─────────┼─────────┼─────────┤
│    q0      │   q1    │   q1    │   q1    │    -    │    -    │
│    q1      │   q1    │   q1    │   q1    │   q1    │   q1    │
└────────────┴─────────┴─────────┴─────────┴─────────┴─────────┘
```

**In Java Map Structure:**
```java
Map<String, Map<Character, String>> transitionTable = {
    "q0" -> { 'a' -> "q1", 'b' -> "q1", ..., 'z' -> "q1",
              'A' -> "q1", 'B' -> "q1", ..., 'Z' -> "q1" },
    "q1" -> { 'a' -> "q1", ..., 'z' -> "q1",
              'A' -> "q1", ..., 'Z' -> "q1",
              '0' -> "q1", ..., '9' -> "q1",
              '_' -> "q1" }
}

Set<String> acceptStates = { "q1" }
String startState = "q0"
```

---

## How the Code Uses This Structure

### 1. Initialization
```java
DFA dfa = new DFA(startState, acceptStates, transitionTable);
```

### 2. Looking Up Transitions
```java
// Get all transitions from current state
Map<Character, String> transitions = transitionTable.get(currentState);

// Check if this state exists and has transitions
if (transitions == null) {
    // Invalid state - no transitions defined
    return false;
}

// Look up where to go based on input character
String nextState = transitions.get(inputChar);

// Check if this transition exists
if (nextState == null) {
    // No transition for this character - reject
    return false;
}

// Move to next state
currentState = nextState;
```

### 3. Adding Transitions Dynamically
```java
// Add single transition
dfa.addTransition("q0", 'a', "q1");
// transitionTable: { "q0" -> { 'a' -> "q1" } }

// Add range of transitions (e.g., all digits)
dfa.addTransitionRange("q0", '0', '9', "q1");
// transitionTable: { "q0" -> { '0'->"q1", '1'->"q1", ..., '9'->"q1" } }
```

---

## Complete Example: Building a Simple Lexer DFA

Here's how you might build a DFA for your lexical analyzer:

```java
// Create empty structures
Set<String> acceptStates = new HashSet<>();
Map<String, Map<Character, String>> transitionTable = new HashMap<>();

// Define states
acceptStates.add("INTEGER");
acceptStates.add("REAL");
acceptStates.add("ASSIGN");
acceptStates.add("PLUS");
acceptStates.add("IDENTIFIER");

// Create DFA
DFA dfa = new DFA("START", acceptStates, transitionTable);

// Add transitions for integers
dfa.addTransitionRange("START", '0', '9', "INTEGER");
dfa.addTransitionRange("INTEGER", '0', '9', "INTEGER");
dfa.addTransition("INTEGER", '.', "DOT");

// Add transitions for real numbers
dfa.addTransitionRange("DOT", '0', '9', "REAL");
dfa.addTransitionRange("REAL", '0', '9', "REAL");

// Add transitions for assignment operator
dfa.addTransition("START", ':', "COLON");
dfa.addTransition("COLON", '=', "ASSIGN");

// Add transitions for operators
dfa.addTransition("START", '+', "PLUS");
dfa.addTransition("START", '-', "MINUS");
dfa.addTransition("START", '*', "MULT");
dfa.addTransition("START", '/', "DIV");
dfa.addTransition("START", '^', "POWER");

// Add transitions for identifiers
dfa.addTransitionRange("START", 'a', 'z', "IDENTIFIER");
dfa.addTransitionRange("START", 'A', 'Z', "IDENTIFIER");
dfa.addTransitionRange("IDENTIFIER", 'a', 'z', "IDENTIFIER");
dfa.addTransitionRange("IDENTIFIER", 'A', 'Z', "IDENTIFIER");
dfa.addTransitionRange("IDENTIFIER", '0', '9', "IDENTIFIER");
```

**Resulting State Diagram:**
```
                    0-9           0-9
        START ──────────> INTEGER ────────┐
          │                 │             │
          │                 │ .           ▼
        a-z,A-Z             ▼        0-9
          │            DOT ────────> REAL
          │                           │ 0-9
          ▼                           ▼
      IDENTIFIER                      
          │ a-z,A-Z,0-9
          └─────┐
                ▼

        START ─────:────> COLON ─────=────> ASSIGN
        
        START ─────+────> PLUS
        START ─────-────> MINUS
        START ─────*────> MULT
```

---

## Key Takeaways

1. **Nested Map = State Machine**: The outer map represents states, inner maps represent transitions
2. **Character-driven**: Each character in the input determines the next state
3. **Deterministic**: For any (state, character) pair, there's exactly 0 or 1 transitions
4. **Accept States**: Only certain states indicate successful recognition
5. **Rejection**: No transition or ending in non-accept state = reject input

This structure makes it easy to:
- Add new token types dynamically
- Query transitions efficiently (O(1) lookups)
- Trace execution for debugging
- Extend the lexer without modifying core logic
