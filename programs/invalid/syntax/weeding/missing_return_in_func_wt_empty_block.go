/* Function with no return statement in block. */

package main;

func foo(x int) int {
	if (x % 2 == 0) {
		return x;
	}

	{
		// Error: Block must have return statement.
	}
}

func main() {
	// Do nothing.
}
