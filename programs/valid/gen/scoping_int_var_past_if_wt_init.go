/* Scoping a variable past an if-statement with an initial statement. */

package main

var x int = 0

func main() {
	var x int = 1
	
	if x := 2; true {
		var x int = 3
	}

	print(x);
}
