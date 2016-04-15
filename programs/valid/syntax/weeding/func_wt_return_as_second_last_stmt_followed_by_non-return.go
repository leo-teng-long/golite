/* Function with a return statement as the second-last statement followed by
 * something non-returning. */

package main;

func foo(x int) int {
	return x;
	x++;
}

func main() {
	// Do nothing.
}
