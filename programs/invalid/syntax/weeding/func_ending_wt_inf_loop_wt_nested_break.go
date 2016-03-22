/* Function ending with an infinite loop but with a nested break statement
 * inside. */

package main

func foo(x, y int) int {
    for {
    	if (x < y) {
    		break
    	}
    }
}

func main() {
    // Do nothing.
}
