#!/bin/bash

valid_progs=$(find valid -regex ".*/[^/]*.go")
invalid_progs=$(find invalid -regex ".*/[^/]*.go")
total=$(find . -regex ".*/[^/]*.go")
valid_prog_cnt=$(find valid -regex ".*/[^/]*.go" | wc -l | tr -d '[[:space:]]')
invalid_prog_cnt=$(find invalid -regex ".*/[^/]*.go" | wc -l | tr -d '[[:space:]]')
total_cnt=$((valid_prog_cnt + invalid_prog_cnt))
valid="VALID"
invalid="INVALID"
valid_ok=0
valid_bad=0
invalid_ok=0
invalid_bad=0
total_ok=0
total_bad=0

export CLASSPATH=$CLASSPATH:../src/

calljava()
{	if [[ $f1 =~ "/parse/" ]]; then
		output=$(java golite/Main -parse $f1)
	elif [[ $f1 =~ "/weeding/" ]]; then
		output=$(java golite/Main -weed $f1)
	elif [[ $f1 =~ "/type/" ]]; then
		output=$(java golite/Main -type $f1)
	elif [[ $f1 =~ "/actual/" ]]; then
		output=$(java golite/Main -type $f1)
	elif [[ $f1 =~ "/general/" ]]; then
		output=$(java golite/Main -type $f1)
	else
		output=$(java golite/Main -type $f1)
	fi
	echo $output
}

for f1 in $valid_progs
do
	output=$(calljava)
	if [[ $output = "VALID" ]]
	then
		((valid_ok++))
		((total_ok++))
	else
		echo $f1
		echo $output
		((valid_bad++))
		((total_bad++))
	fi
done

for f2 in $invalid_progs
do
	output=$(calljava)
	if [[ $output = "INVALID" ]]
	then
		((invalid_ok++))
		((total_ok++))
	else
		echo $f2
		echo $output
		((invalid_bad++))
		((total_bad++))
	fi
done

echo "Valid OK: $valid_ok/$valid_prog_cnt"
echo "Valid BAD: $valid_bad/$valid_prog_cnt"
echo "Invalid OK: $invalid_ok/$invalid_prog_cnt"
echo "Invalid BAD: $invalid_bad/$invalid_prog_cnt"
echo "Total OK: $total_ok/$total_cnt"
echo "Total BAD: $total_bad/$total_cnt"

