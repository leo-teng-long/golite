/* Scoping a variable past an if-statement. */

package main

var x int = 0

func main() {
	var x int = 1
	
	if true {
		var x int = 3
	}

	print(x);
}
