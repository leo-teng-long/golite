#!/bin/bash

### Installation script ###

SABLECC_NAME=sablecc-3.7

JUNIT_FNAME=junit-4.12.jar
HAMCREST_FNAME=hamcrest-core-1.3.jar

# Colour control commands.
GREEN=`tput setaf 2`
YELLOW=`tput setaf 3`
RESET=`tput sgr 0`

# Install SableCC if not already done so.
if [ ! -d $SABLECC_NAME ]
then
	echo -e "\n${YELLOW}Installing SableCC...${RESET}\n"

	curl -L https://sourceforge.net/projects/sablecc/files/SableCC/3.7/sablecc-3.7.zip/download?use_mirror=iweb > $SABLECC_NAME.zip
	unzip $SABLECC_NAME.zip
	rm -f $SABLECC_NAME.zip
fi

# Install JUnit if not already done so.
if [ ! -f "jars/$JUNIT_FNAME" ]
then
	echo -e "\n${YELLOW}Installing JUnit...${RESET}\n"

	curl -L http://search.maven.org/remotecontent\?filepath\=junit/junit/4.12/junit-4.12.jar > jars/$JUNIT_FNAME
fi

# Install Hamcrest Core if not already done so.
if [ ! -f "jars/$HAMCREST_FNAME" ]
then
	echo -e "\n${YELLOW}Installing Hamcrest Core...${RESET}\n"

	curl -L http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar > jars/$HAMCREST_FNAME
fi

echo -e "\n${GREEN}Installation successful!${RESET}"