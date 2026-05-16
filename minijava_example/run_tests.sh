#!/bin/bash

MAIN_CLASS="Main"

for file in ../minijava-examples-new/minijava-extra/*.java; do
    echo "Running $MAIN_CLASS with $file"

    java "$MAIN_CLASS" "$file"

    echo "-------------------"
done