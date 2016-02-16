#!/bin/bash

###
Runs the parser through all programs in the programs folder.
###

for prog in $(find ../programs -name "*.go" )
do
	echo "Testing $prog..."
	java golite.Main -parse $prog
done
