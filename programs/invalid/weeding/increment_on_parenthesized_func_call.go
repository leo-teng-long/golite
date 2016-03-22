/* Increment on parenthesized function call. */

package main;

func foo(x int) int {
	return x;
}

func main() {
	((foo()))++
}
