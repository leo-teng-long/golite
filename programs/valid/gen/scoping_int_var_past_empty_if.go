/* Scoping a variable past an empty if-statement. */

package main

var x int = 0

func main() {
	var x int = 1
	
	if true {

	}

	print(x);
}
