/* Switch with blank condition. */

package main;

func main() {
    switch _ { // Error: Blank here is invalid.
		case 0: 
			// Do nothing.
		default:
			// Do nothing.
	}
}
