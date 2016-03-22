/* Function ending with an infinite loop with a return statement inside. */

package main

func foo(x, y int) int {
    for {
    	return x
    }
}

func main() {
    // Do nothing.
}
