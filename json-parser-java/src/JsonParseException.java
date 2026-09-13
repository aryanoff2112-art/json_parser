
public class JsonParseException extends RuntimeException {
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, int line, int column) {
        super(message + " (line " + line + ", column " + column + ")");
    }
}
