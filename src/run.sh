#!/bin/bash

###
Runs the scanner through all programs in the programs folder.
###

for prog in $(find ../programs -name "*.go" )
do
	echo "Testing $prog..."
	java golite.Main $prog
done
