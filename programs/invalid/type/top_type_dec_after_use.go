/* Top-level type declaration after it's use. */

package main

var y t = t(0) // Error: Can't use a type before it's declared.
type t int

func main() {
	// Do nothing.
}
