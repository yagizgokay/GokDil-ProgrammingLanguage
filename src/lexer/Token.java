// === src/lexer/Token.java ===
package lexer;

public class Token {
    public final TokenType type;
    public final String value;
    public final int line;

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public String toString() {
        return "Token(" + type + ", '" + value + "', line " + line + ")";
    }
}
