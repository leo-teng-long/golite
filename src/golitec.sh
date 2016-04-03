#!/bin/bash

#################
# Runner script #
#################


usage() {
	echo -e "Usage: $0 <scan | tokens | parse | pretty | type | dumpsymtab | pptype | gen | help> filepath"
}

# Compiler option.
OPT=$1
# Program filepath.
PROG_PATH=$2

if [[ $OPT != "scan" && $OPT != "tokens" && $OPT != "parse" && $OPT != "pretty" && $OPT != "type" && $OPT != "dumpsymtab" && $OPT != "pptype" && $OPT != "gen" && $OPT != "help" ]]
then
	usage
	exit -1
fi

if [[ $OPT != "help" && -z $PROG_PATH ]]
then
	usage
	exit -1
fi

if [[ $OPT != "help" && -d $PROG_PATH ]]
then
	echo -e "ERROR: Expecting a file, not a directory"
	exit -1
fi

if [[ $OPT != "help" && ! -f $PROG_PATH ]]
then
	echo -e "ERROR: $PROG_PATH does not exist"
	exit -1
fi

java -cp jars/commons-cli-1.3.1.jar: golite.Main -$OPT $PROG_PATH
