/* Function with return statement in block. */

package main;

func foo(x int) int {
	if (x % 2 == 0) {
		return x;
	}

	{
		var y = x + 1;
		return y;
	}
}

func main() {
	// Do nothing.
}
