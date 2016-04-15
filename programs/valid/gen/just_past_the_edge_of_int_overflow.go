/* Just past the edge of integer overflow. */

package main

func main() {
	x := 1
	for i := 0 ; i < 31; i++ {
		x *= 2
	}

	print(x)
}
