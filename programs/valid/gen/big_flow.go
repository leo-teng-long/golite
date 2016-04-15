/* Test big flow multiplication. */

package main

func main() {
	var x float64 = 1.0
	for i := 0 ; i < 128; i++ {
		x *= 2.5
	}

	println(x)
}
