/* A global variable shadowed by a short declaration. */

package main

var x int = 1

func main() {
	x := 2

	print(x)
}
