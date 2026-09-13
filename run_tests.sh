#!/usr/bin/env bash
# Runs the JSON parser against every test file in tests/stepN and checks
# that files named valid*.json exit 0 and invalid*.json exit 1.

set -u
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_DIR="$SCRIPT_DIR/out"
TESTS_DIR="$SCRIPT_DIR/tests"

pass=0
fail=0

for step_dir in "$TESTS_DIR"/step*; do
    step_name=$(basename "$step_dir")
    echo "== $step_name =="
    for file in "$step_dir"/*.json; do
        [ -e "$file" ] || continue
        base=$(basename "$file")

        if [[ "$base" == valid* ]]; then
            expected=0
        elif [[ "$base" == invalid* ]]; then
            expected=1
        else
            echo "  SKIP $base (name doesn't start with valid/invalid)"
            continue
        fi

        output=$(java -cp "$OUT_DIR" Main "$file" 2>&1)
        actual=$?

        if [ "$actual" -eq "$expected" ]; then
            echo "  PASS $base (exit $actual): $output"
            pass=$((pass+1))
        else
            echo "  FAIL $base (expected exit $expected, got $actual): $output"
            fail=$((fail+1))
        fi
    done
done

echo
echo "Total: $((pass+fail))  Passed: $pass  Failed: $fail"
[ "$fail" -eq 0 ]
