# Contributing

Thanks for considering a contribution to this JSON parser!

## Getting set up

```bash
git clone https://github.com/USERNAME/REPO.git
cd REPO
mkdir -p out
javac -d out src/*.java
```

Or with Maven:

```bash
mvn compile
```

## Running the parser

```bash
java -cp out Main path/to/file.json
echo $?   # 0 = valid, 1 = invalid
```

## Running the tests

```bash
chmod +x run_tests.sh
./run_tests.sh
```

Every file in `tests/step*/` is checked automatically: files named
`valid*.json` must exit `0`, and files named `invalid*.json` must exit `1`.

## Adding a test case

1. Drop a `.json` file into the relevant `tests/stepN/` folder (or
   `tests/step5/` for general edge cases).
2. Name it `valid_<description>.json` or `invalid_<description>.json` —
   the test runner uses this prefix to know the expected exit code.
3. Run `./run_tests.sh` and confirm it passes.

## Submitting changes

1. Fork the repo and create a branch: `git checkout -b my-fix`
2. Make your changes, keeping `run_tests.sh` green
3. Add test cases for any new behavior
4. Open a pull request describing what changed and why

## Code style

- Plain Java, no external dependencies
- 4-space indentation (see `.editorconfig`)
- Keep error messages in `JsonParseException` human-readable — they're
  shown directly to the user
