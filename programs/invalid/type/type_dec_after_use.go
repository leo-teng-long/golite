/* Type declaration after it's use. */

package main

func main() {
	var y t = t(0) // Error: Can't use a type before it's declared.
	type t int
}
