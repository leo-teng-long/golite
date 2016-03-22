/* Function ending with an infinite loop but with a break statement inside. */

package main

func foo(x, y int) int {
    for {
    	break
    }
}

func main() {
    // Do nothing.
}
