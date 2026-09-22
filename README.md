# JSON Parser (Java) — Coding Challenges solution

![CI](https://github.com/aryanoff2112-art/REPO/actions/workflows/ci.yml/badge.svg)


A hand-written recursive-descent JSON parser with no external dependencies:
just a lexer, a parser, and a small CLI

## Files

- `src/TokenType.java` — enum of token kinds
- `src/Token.java` — a token with line/column info (for error messages)
- `src/JsonLexer.java` — turns raw text into tokens (handles strings with
  escapes/`\uXXXX`, numbers with negatives/fractions/exponents, `true`,
  `false`, `null`, and structural characters)
- `src/JsonParser.java` — recursive-descent parser turning tokens into a
  Java object tree (`LinkedHashMap`, `List`, `String`, `Long`/`Double`,
  `Boolean`, `null`). Enforces that the top-level value is an object or
  array, matching the official json.org JSON_checker test suite.
- `src/JsonParseException.java` — exception with a human-readable message
  and line/column
- `src/Main.java` — CLI: reads a file, prints "Valid JSON" / "Invalid
  JSON: <reason>", exits 0 (valid) or 1 (invalid)
- `tests/step1` .. `tests/step5` — test fixtures matching each step of the
  challenge (step5 has extra edge cases: unicode escapes, surrogate
  pairs, leading zeros, trailing commas, top-level scalars, etc.)
- `run_tests.sh` — runs the CLI against every file in `tests/*`, checking
  that `valid*.json` exits 0 and `invalid*.json` exits 1
- `pom.xml` — optional Maven build, if you'd rather not invoke `javac`
  by hand
- `.editorconfig` — consistent indentation/line-endings across editors
- `.github/workflows/ci.yml` — GitHub Actions workflow that builds and
  tests on every push/PR
- `.github/ISSUE_TEMPLATE.md`, `.github/PULL_REQUEST_TEMPLATE.md` —
  templates for issues and pull requests
- `CONTRIBUTING.md` — how to build, test, and submit changes
- `LICENSE` — MIT

## Getting the code

```bash
git clone https://github.com/aryanoff2112-art/REPO.git
cd REPO
```

## Building

### Option A: plain `javac`

Needs a JDK (`javac`) — any JDK 8+ works.

```bash
mkdir -p out
javac -d out src/*.java
```

### Option B: Maven

```bash
mvn compile
# or, to build a runnable jar:
mvn package
```

## Running

With plain `javac`:

```bash
java -cp out Main path/to/file.json
echo $?   # 0 = valid, 1 = invalid
```

With the Maven-built jar:

```bash
java -jar target/json-parser-1.0.0.jar path/to/file.json
```

## Testing

```bash
chmod +x run_tests.sh
./run_tests.sh
```

Currently: **41/41 tests passing** across steps 1–5.

## Trying it against the official json.org test suite

Download http://www.json.org/JSON_checker/test.zip, unzip it, and run
each `fail*.json`/`pass*.json` file through the CLI. Two things to note
about that suite if you do:

1. It expects the parser to accept an arbitrarily deep nesting test
   (`fail18.json`'s deep array is actually meant to *fail* only under a
   depth limit — this implementation has no artificial depth limit, so
   treat that one as a judgment call).
2. It expects top-level JSON text to be an object or array (not a bare
   string/number) — this parser already enforces that (see
   `invalid_bare_string_top_level.json` / `invalid_bare_number_top_level.json`
   in `tests`).

## Continuous Integration

A GitHub Actions workflow (`.github/workflows/ci.yml`) compiles the
project with `javac` and runs `run_tests.sh` on every push and pull
request against `main`.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for build/test instructions and
how to add new test cases or submit a pull request.

## License

[MIT](LICENSE)