/* Variable declaration after it's use. */

package main

func main() {
	var y int = x // Error: Can't use a variable before it's declared.
}

var x = 0
