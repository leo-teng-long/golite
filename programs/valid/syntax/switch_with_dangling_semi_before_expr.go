/* Switch statement with a dangling semi preceding an expression condition. */

package main

func main() {
	x := 0

	switch ; x {
		case 1:
			// Do something
		case 2:
			// Do something
		case 3:
			// Do something
		default:
			// Do something
	}

}
