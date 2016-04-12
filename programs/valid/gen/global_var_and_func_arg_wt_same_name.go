/* Global variable and function argument with the same name. */

package main

var x int = 1

func f(x int) int {
	x += 1
	return x
}

func main() {
	print(f(2));
}
