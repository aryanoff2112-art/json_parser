/**
 * Thrown whenever the input is not valid JSON, at either the lexing or
 * parsing stage. The message is meant to be shown directly to the user.
 */
public class JsonParseException extends RuntimeException {
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, int line, int column) {
        super(message + " (line " + line + ", column " + column + ")");
    }
}
