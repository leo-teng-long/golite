/* Integer overflow in evaluating a simple expression. */

package main

func main() {
	x := 1
	for i := 0 ; i < 30; i++ {
		x *= 2
	}

	// All of these should yield the same result of 2147483647
	println(2 * x - 1)
	println(x + x - 1)
	println(x - 1 + x)
}
