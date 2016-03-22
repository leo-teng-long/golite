 /* Non-constant array bound. */

package main

var foo = 3;
var a [(foo + 3)] int // Error: Non-constant array bound.

func main() {
	// Do nothing
}
