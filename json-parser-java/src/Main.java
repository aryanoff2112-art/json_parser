import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Command-line entry point for the JSON parser, built for the
 * Coding Challenges "Write your own JSON parser" exercise.
 *
 * Usage: java -cp out Main <path-to-json-file>
 *
 * Exit codes:
 *   0 - the file contains valid JSON
 *   1 - the file contains invalid JSON, or another error occurred
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: json-parser <file>");
            System.exit(1);
            return;
        }

        String path = args[0];
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Invalid JSON: could not read file '" + path + "' (" + e.getMessage() + ")");
            System.exit(1);
            return;
        }

        try {
            List<Token> tokens = new JsonLexer(content).tokenize();
            Object result = new JsonParser(tokens).parse();
            System.out.println("Valid JSON");
            System.exit(0);
        } catch (JsonParseException e) {
            System.out.println("Invalid JSON: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Invalid JSON: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            System.exit(1);
        }
    }
}
