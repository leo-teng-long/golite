/* Function with a switch statement with default case, but for which one case
 * block doesn't return. */

package main;

func foo(x int) int {
	if (x % 2 == 0) {
		return x;
	} else {
		switch ; x {
			case 3:
				return x;
			case 5:
				return x;
			case 7:
				// Error: Missing return statement for this case block.
			// Default case.
			default:
				return x;
		}
 	}
}

func main() {
	// Do nothing.
}
