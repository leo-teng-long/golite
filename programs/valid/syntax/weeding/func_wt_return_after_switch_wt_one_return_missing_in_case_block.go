/* Function with a switch statement with default case, but for which one case
 * block doesn't return, but there's a return statement right after the switch. */

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
				// Default case.
			default:
				return x;
		}

		return x;
 	}
}

func main() {
	// Do nothing.
}
