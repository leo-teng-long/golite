/* Function with a bunch of if-statments, all with a return statement. */

package main

func foo(x int) int {
	if (x % 2 == 0) {
		return x;
	} else {
		if (x > 2) {
			if (x > 3) {
				if (x > 5) {
					{
						return x;
					}
				}

				return x;
			} else {
				return x;
			}
		} else {
			{
				x++;
				return x;
			}
		}
	}
}

func main() {
	// Do nothing.
}
