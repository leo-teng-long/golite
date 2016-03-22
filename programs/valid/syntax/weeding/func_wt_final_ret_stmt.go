/* Function with final return statement. */

package main;

func foo(x int) int {
	if (x % 2 == 0) {
		return x;
	} else {
		x++;
	}

	return x;
}

func main() {
	// Do nothing.
}
