#!/bin/bash

MAIN_CLASS="Main"

for file in ../minijava-examples-new/*.java; do
    echo "Running $MAIN_CLASS with $file"

    stdout=$(mktemp)
    stderr=$(mktemp)

    java "$MAIN_CLASS" "$file" >"$stdout" 2>"$stderr"
    exit_code=$?

    if [[ "$file" == *-error* ]]; then
        # These files are EXPECTED to fail
        if [[ $exit_code -ne 0 ]]; then
            echo "PASS: error file failed as expected"
        else
            echo "FAIL: error file succeeded unexpectedly"
        fi
    else
        # These files are EXPECTED to succeed
        if [[ $exit_code -eq 0 ]]; then
            echo "PASS: normal file succeeded"
        else
            echo "FAIL: normal file failed unexpectedly"
        fi
    fi

    echo "--- STDOUT ---"
    cat "$stdout"

    echo "--- STDERR ---"
    cat "$stderr"

    rm "$stdout" "$stderr"

    echo "-------------------"
done