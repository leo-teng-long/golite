/* Type casting with an alias. */

package main

type t int
var x t = t(1)

func main() {
	// Unfortunately, printing a variable of alias type is disallowed. 
	print(0)
}
