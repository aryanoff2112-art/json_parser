import java.util.ArrayList;
import java.util.List;

public class JsonLexer {
    private final String source;
    private int pos = 0;
    private int line = 1;
    private int column = 1;

    public JsonLexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        skipWhitespace();
        while (!isAtEnd()) {
            tokens.add(nextToken());
            skipWhitespace();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private Token nextToken() {
        int startLine = line;
        int startColumn = column;
        char c = peek();

        switch (c) {
            case '{':
                advance();
                return new Token(TokenType.LEFT_BRACE, "{", startLine, startColumn);
            case '}':
                advance();
                return new Token(TokenType.RIGHT_BRACE, "}", startLine, startColumn);
            case '[':
                advance();
                return new Token(TokenType.LEFT_BRACKET, "[", startLine, startColumn);
            case ']':
                advance();
                return new Token(TokenType.RIGHT_BRACKET, "]", startLine, startColumn);
            case ':':
                advance();
                return new Token(TokenType.COLON, ":", startLine, startColumn);
            case ',':
                advance();
                return new Token(TokenType.COMMA, ",", startLine, startColumn);
            case '"':
                return readString(startLine, startColumn);
            case 't':
                return readLiteral("true", TokenType.TRUE, startLine, startColumn);
            case 'f':
                return readLiteral("false", TokenType.FALSE, startLine, startColumn);
            case 'n':
                return readLiteral("null", TokenType.NULL, startLine, startColumn);
            default:
                if (c == '-' || Character.isDigit(c)) {
                    return readNumber(startLine, startColumn);
                }
                throw new JsonParseException("Unexpected character '" + c + "'", startLine, startColumn);
        }
    }

    private Token readLiteral(String literal, TokenType type, int startLine, int startColumn) {
        for (int i = 0; i < literal.length(); i++) {
            if (isAtEnd() || peek() != literal.charAt(i)) {
                throw new JsonParseException(
                        "Invalid literal, expected '" + literal + "'", startLine, startColumn);
            }
            advance();
        }
        return new Token(type, literal, startLine, startColumn);
    }

    private Token readString(int startLine, int startColumn) {
        advance(); // consume opening quote
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (isAtEnd()) {
                throw new JsonParseException("Unterminated string", startLine, startColumn);
            }
            char c = advance();
            if (c == '"') {
                break;
            }
            if (c == '\\') {
                if (isAtEnd()) {
                    throw new JsonParseException("Unterminated escape sequence", startLine, startColumn);
                }
                char esc = advance();
                switch (esc) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        sb.append(readUnicodeEscape(startLine, startColumn));
                        break;
                    default:
                        throw new JsonParseException(
                                "Invalid escape sequence '\\" + esc + "'", line, column);
                }
            } else if (c < 0x20) {
                throw new JsonParseException(
                        "Control character in string must be escaped", line, column);
            } else {
                sb.append(c);
            }
        }
        return new Token(TokenType.STRING, sb.toString(), startLine, startColumn);
    }

    private char readUnicodeEscape(int startLine, int startColumn) {
        if (pos + 4 > source.length()) {
            throw new JsonParseException("Invalid unicode escape", startLine, startColumn);
        }
        String hex = source.substring(pos, pos + 4);
        try {
            int code = Integer.parseInt(hex, 16);
            for (int i = 0; i < 4; i++) advance();
            return (char) code;
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid unicode escape '\\u" + hex + "'", startLine, startColumn);
        }
    }

    private Token readNumber(int startLine, int startColumn) {
        int start = pos;

        if (peek() == '-') {
            advance();
        }

        if (isAtEnd() || !Character.isDigit(peek())) {
            throw new JsonParseException("Invalid number: expected digit", startLine, startColumn);
        }

        if (peek() == '0') {
            advance();
            // JSON forbids leading zeros like "01"
            if (!isAtEnd() && Character.isDigit(peek())) {
                throw new JsonParseException("Invalid number: leading zeros not allowed", startLine, startColumn);
            }
        } else {
            while (!isAtEnd() && Character.isDigit(peek())) {
                advance();
            }
        }

        if (!isAtEnd() && peek() == '.') {
            advance();
            if (isAtEnd() || !Character.isDigit(peek())) {
                throw new JsonParseException("Invalid number: expected digit after '.'", startLine, startColumn);
            }
            while (!isAtEnd() && Character.isDigit(peek())) {
                advance();
            }
        }

        if (!isAtEnd() && (peek() == 'e' || peek() == 'E')) {
            advance();
            if (!isAtEnd() && (peek() == '+' || peek() == '-')) {
                advance();
            }
            if (isAtEnd() || !Character.isDigit(peek())) {
                throw new JsonParseException("Invalid number: expected digit in exponent", startLine, startColumn);
            }
            while (!isAtEnd() && Character.isDigit(peek())) {
                advance();
            }
        }

        String text = source.substring(start, pos);
        return new Token(TokenType.NUMBER, text, startLine, startColumn);
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                advance();
            } else {
                break;
            }
        }
    }

    private boolean isAtEnd() {
        return pos >= source.length();
    }

    private char peek() {
        return source.charAt(pos);
    }

    private char advance() {
        char c = source.charAt(pos);
        pos++;
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }
}
