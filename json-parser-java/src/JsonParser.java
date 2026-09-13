import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class JsonParser {
    private final List<Token> tokens;
    private int pos = 0;

    public JsonParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /** Parses the whole token stream and returns the resulting object tree. */
    public Object parse() {
        Token first = peek();
        if (first.type != TokenType.LEFT_BRACE && first.type != TokenType.LEFT_BRACKET) {
            throw new JsonParseException(
                    "A JSON document must start with '{' or '['", first.line, first.column);
        }

        Object value = parseValue();

        Token next = peek();
        if (next.type != TokenType.EOF) {
            throw new JsonParseException(
                    "Unexpected trailing content after JSON value", next.line, next.column);
        }
        return value;
    }

    private Object parseValue() {
        Token token = peek();
        switch (token.type) {
            case LEFT_BRACE:
                return parseObject();
            case LEFT_BRACKET:
                return parseArray();
            case STRING:
                advance();
                return token.value;
            case NUMBER:
                advance();
                return parseNumber(token.value);
            case TRUE:
                advance();
                return Boolean.TRUE;
            case FALSE:
                advance();
                return Boolean.FALSE;
            case NULL:
                advance();
                return null;
            default:
                throw new JsonParseException(
                        "Unexpected token, expected a value", token.line, token.column);
        }
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> result = new LinkedHashMap<>();
        expect(TokenType.LEFT_BRACE);

        if (peek().type == TokenType.RIGHT_BRACE) {
            advance();
            return result;
        }

        while (true) {
            Token keyToken = peek();
            if (keyToken.type != TokenType.STRING) {
                throw new JsonParseException(
                        "Expected string key in object", keyToken.line, keyToken.column);
            }
            advance();
            String key = keyToken.value;

            expect(TokenType.COLON);

            Object value = parseValue();
            result.put(key, value);

            Token next = peek();
            if (next.type == TokenType.COMMA) {
                advance();
                // A trailing comma must be followed by another member, not '}'
                if (peek().type == TokenType.RIGHT_BRACE) {
                    throw new JsonParseException(
                            "Trailing comma not allowed in object", peek().line, peek().column);
                }
                continue;
            } else if (next.type == TokenType.RIGHT_BRACE) {
                advance();
                break;
            } else {
                throw new JsonParseException(
                        "Expected ',' or '}' in object", next.line, next.column);
            }
        }
        return result;
    }

    private List<Object> parseArray() {
        List<Object> result = new ArrayList<>();
        expect(TokenType.LEFT_BRACKET);

        if (peek().type == TokenType.RIGHT_BRACKET) {
            advance();
            return result;
        }

        while (true) {
            Object value = parseValue();
            result.add(value);

            Token next = peek();
            if (next.type == TokenType.COMMA) {
                advance();
                if (peek().type == TokenType.RIGHT_BRACKET) {
                    throw new JsonParseException(
                            "Trailing comma not allowed in array", peek().line, peek().column);
                }
                continue;
            } else if (next.type == TokenType.RIGHT_BRACKET) {
                advance();
                break;
            } else {
                throw new JsonParseException(
                        "Expected ',' or ']' in array", next.line, next.column);
            }
        }
        return result;
    }

    private Object parseNumber(String text) {
        if (text.indexOf('.') < 0 && text.indexOf('e') < 0 && text.indexOf('E') < 0) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                return Double.parseDouble(text);
            }
        }
        return Double.parseDouble(text);
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token advance() {
        Token t = tokens.get(pos);
        if (pos < tokens.size() - 1) pos++;
        return t;
    }

    private void expect(TokenType type) {
        Token t = peek();
        if (t.type != type) {
            throw new JsonParseException("Expected " + type, t.line, t.column);
        }
        advance();
    }
}
