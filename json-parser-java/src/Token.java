/**
 * A single lexical token produced by the JsonLexer.
 *
 * `value` holds the *decoded* literal value for STRING (escapes already
 * resolved) and NUMBER (kept as the raw text, parsed to a number by the
 * parser) tokens. For structural tokens it's just the raw character(s).
 */
public class Token {
    public final TokenType type;
    public final String value;
    public final int line;
    public final int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return type + "('" + value + "') at " + line + ":" + column;
    }
}
