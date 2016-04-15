/* Assign a void function call. */

package main

func voidf() {

}

var x int = voidf() // Error: Can't assign a void function call

func main() {
	// Do nothing.
}
