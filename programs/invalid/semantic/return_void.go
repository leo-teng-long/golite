/* Returning void for a function. */

package main

func main() {
  foo()
}

// Error: void type undefined, void function should have no return type
func foo() void {

}
