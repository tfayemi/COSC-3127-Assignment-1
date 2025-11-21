# Mini Compiler - Lexical Analyzer (Phase 1)

**COSC 3127 Programming Languages - Assignment 1**  
**Group Project: Mini Language Compiler**

---

## Project Overview

This is **Phase 1** of a multi-phase group project to build a complete compiler for the "Mini" programming language. This phase implements the **Lexical Analyzer (Lexer)** component.

### Language Specification
The Mini language supports:
- **Data Types**: `integer` and `real`
- **Assignment Operator**: `:=`
- **Arithmetic Operators**: `+`, `-`, `*`, `/`, `^`
- **Identifiers**: Variable names following standard conventions

---

## Project Structure

```
Assignment-1/
├── README.md                      # This file
├── Regular_Grammars.html          # Complete grammar documentation (HTML)
├── convert_to_pdf.py              # Utility script to convert HTML to PDF
├── .venv/                         # Python virtual environment (for PDF generation)
└── mini/                          # Main Java package
    ├── DFA.java                   # Deterministic Finite Automaton implementation
    ├── Lexer.java                 # Lexical analyzer (tokenizer)
    ├── Token.java                 # Token class definition
    ├── DFA_State_Mapping_Guide.md # Visual guide to DFA state transitions
    └── Regular_Grammars.pdf       # Grammar documentation (PDF format)
```

---

## What Has Been Implemented

### 1. **DFA (Deterministic Finite Automaton) - `DFA.java`**
A flexible, reusable DFA implementation that powers all token recognition.

**Features:**
- Core DFA operations (`run()`, `matchLength()`)
- Dynamic transition addition (`addTransition()`, `addTransitionRange()`)
- Maximal munch tokenization support

**Factory Methods:**
- `createIdentifierDFA()` - Recognizes identifiers: `[a-zA-Z_][a-zA-Z0-9_]*`
- `createIntegerDFA()` - Recognizes integers: `[0-9]+`
- `createRealDFA()` - Recognizes real numbers: `[0-9]+\.[0-9]+`
- `createOperatorDFA()` - Recognizes operators: `+`, `-`, `*`, `/`, `^`
- `createAssignmentOperatorDFA()` - Recognizes assignment: `:=`
- `createKeywordDFA(String keyword)` - Generic keyword recognizer (for future use)

**Key Methods:**
```java
public boolean run(String input)
// Returns true if the entire input string is accepted by the DFA

public int matchLength(String input, int startIndex)
// Returns the length of the longest accepted prefix starting at startIndex
// Returns -1 if no match found
// Critical for lexer's token extraction
```

### 2. **Token - `Token.java`**
Represents a lexical token with type, value, and position information.

**Token Types:**
- `IDENTIFIER` - Variable names
- `INTEGER` - Whole numbers
- `REAL` - Decimal numbers
- `OPERATOR` - Arithmetic operators
- `ASSIGNMENT` - Assignment operator `:=`
- `KEYWORD` - Reserved words (placeholder for future use)

**Token Structure:**
```java
Token(Type type, String value, int line, int column)
```

### 3. **Lexer - `Lexer.java`**
The main lexical analyzer that tokenizes source code.

**Features:**
- Whitespace skipping
- Line and column tracking for error reporting
- DFA-based token recognition with precedence ordering
- Maximal munch principle (longest match)
- Comprehensive error messages

**Usage:**
```java
Lexer lexer = new Lexer(sourceCode);
List<Token> tokens = lexer.tokenize();
```

**Token Recognition Order:**
1. Identifiers (checked first to avoid keyword conflicts)
2. Real numbers (checked before integers to avoid partial matches)
3. Integers
4. Assignment operator
5. Arithmetic operators

### 4. **Documentation**
- **`Regular_Grammars.html`** - Interactive HTML documentation with:
  - Right-linear grammars for all token types
  - Regular expressions
  - State diagrams
  - Examples and explanations
  - Color-coded grammar notation

- **`DFA_State_Mapping_Guide.md`** - Detailed visual guide showing:
  - How the nested Map structure represents DFAs
  - Step-by-step execution traces
  - Multiple worked examples
  - Conceptual diagrams

---

## What Works

✓ **Token Recognition:**
- Identifiers: `x`, `myVar`, `total_sum`, `_count`
- Integers: `0`, `42`, `1234`
- Real numbers: `3.14`, `0.5`, `99.99`
- Assignment: `:=`
- Operators: `+`, `-`, `*`, `/`, `^`

✓ **Error Handling:**
- Detects illegal characters
- Reports exact line and column of errors
- Clear error messages

✓ **Position Tracking:**
- Tracks line and column for each token
- Useful for error reporting in later phases

---

## How to Use the Lexer

### Compiling:
```bash
cd mini
javac *.java
```

### Example Usage:
```java
import mini.Lexer;
import mini.Token;
import java.util.List;

public class TestLexer {
    public static void main(String[] args) {
        String sourceCode = "x := 42 + 3.14";
        
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();
        
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
```

**Expected Output:**
```
Token[type=IDENTIFIER, value=x, line=1, column=1]
Token[type=ASSIGNMENT, value=:=, line=1, column=3]
Token[type=INTEGER, value=42, line=1, column=6]
Token[type=OPERATOR, value=+, line=1, column=9]
Token[type=REAL, value=3.14, line=1, column=11]
```

---

## What's NOT Implemented (Future Work)

### For the Next Phase (Syntax Analyzer):

1. **Keywords/Reserved Words**
   - The infrastructure exists (`createKeywordDFA()`) but no specific keywords defined
   - Common keywords to add: `if`, `else`, `while`, `for`, `int`, `real`, `return`, etc.
   - Need to modify lexer to check identifiers against keyword list

2. **Comments**
   - No comment handling (single-line `//` or multi-line `/* */`)
   - Will require special handling outside DFA framework

3. **Parentheses and Delimiters**
   - No recognition of: `(`, `)`, `{`, `}`, `[`, `]`, `,`, `;`
   - Easy to add as single-character operators

4. **Comparison Operators**
   - No support for: `==`, `!=`, `<`, `>`, `<=`, `>=`
   - Requires lookahead for two-character operators

5. **String Literals**
   - No string support (if needed for your language)
   - Would require special DFA or custom handling

6. **Whitespace Tokens**
   - Currently skipped; some parsers may need them

7. **EOF Token**
   - No explicit end-of-file token
   - May be needed for parser

---

## Notes for Syntax Analyzer Team

### Interface to Use:
```java
// Input: Source code string
String sourceCode = "...your program...";

// Output: List of tokens
Lexer lexer = new Lexer(sourceCode);
List<Token> tokens = lexer.tokenize();
```

### Token Properties:
```java
token.getType()   // Returns Token.Type enum
token.getValue()  // Returns the actual text (lexeme)
token.getLine()   // Returns line number (1-indexed)
token.getColumn() // Returns column number (1-indexed)
```

### Error Handling:
The lexer throws `RuntimeException` with detailed messages for illegal characters. You may want to implement a custom exception type.

### Extending the Lexer:

**To add a new token type:**

1. Add the type to `Token.Type` enum in `Token.java`
2. Create a DFA factory method in `DFA.java`
3. Initialize the DFA in `Lexer` constructor
4. Add token matching call in `Lexer.tokenize()` method

**Example - Adding parentheses:**
```java
// In DFA.java
public static DFA createParenthesisDFA() {
    Set<String> acceptStates = new HashSet<>();
    acceptStates.add("ACCEPT");
    DFA dfa = new DFA("START", acceptStates, new HashMap<>());
    dfa.addTransition("START", '(', "ACCEPT");
    dfa.addTransition("START", ')', "ACCEPT");
    return dfa;
}

// In Token.java
public enum Type {
    IDENTIFIER, INTEGER, REAL, OPERATOR, 
    ASSIGNMENT, KEYWORD, PARENTHESIS  // Add this
}

// In Lexer.java constructor
this.parenDFA = DFA.createParenthesisDFA();

// In Lexer.tokenize() method
if (token == null) {
    token = tryMatchDFA(parenDFA, Token.Type.PARENTHESIS, startIdx, startLine, startCol);
}
```

---

## Testing Recommendations

### Test Cases to Verify:

1. **Simple expressions**: `x := 42`
2. **Mixed types**: `result := 3.14 + 2`
3. **Complex identifiers**: `_var1`, `myVariable_2`
4. **All operators**: `a + b - c * d / e ^ f`
5. **Real vs Integer**: `3.14` vs `314`
6. **Multi-line input** with proper line/column tracking
7. **Error cases**: illegal characters, invalid tokens

### Known Limitations:

- Real numbers must have digits on both sides of decimal: `3.14` ✓, `.14` ✗, `3.` ✗
- No negative number literals (handle `-` as operator)
- Identifiers cannot start with digits
- Case-sensitive matching

---

## Documentation Files

- **`DFA_State_Mapping_Guide.md`**: Visual guide to understanding the DFA implementation
- **`Regular_Grammars.html`**: Complete grammar documentation with examples
- **`Regular_Grammars.pdf`**: PDF version of the grammar documentation

---

## Next Steps (Syntax Analyzer Phase)

The syntax analyzer should:

1. **Input**: Take the list of tokens from this lexer
2. **Parse**: Build a parse tree or abstract syntax tree (AST)
3. **Grammar**: Define context-free grammar for Mini language
4. **Validate**: Check syntactic correctness (matching parentheses, proper statement structure, etc.)
5. **Output**: AST or parse tree for semantic analysis phase

**Recommended Parser Approach:**
- Recursive Descent Parser (simple, matches grammar directly)
- LL(1) Parser (table-driven)
- LR/LALR Parser (more powerful, can use tools like YACC/Bison)

---

## Contributors

- **Phase 1 (Lexical Analyzer)**: [Your Name/Team Members]
- **Phase 2 (Syntax Analyzer)**: TBD
- **Phase 3 (Semantic Analyzer)**: TBD
- **Phase 4 (Code Generation)**: TBD

---

## License

This is an academic project for COSC 3127 Programming Languages.

---

**Last Updated**: November 21, 2025  
**Status**: Phase 1 (Lexical Analysis) - Complete
