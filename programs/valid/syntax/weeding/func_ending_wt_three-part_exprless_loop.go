/* Function ending with a three-part expressionless loop. */

package main

func foo(x, y int) int {
    for i := 0 ; ; i++ {
    	x++
    	y++
    }
}

func main() {
    // Do nothing.
}
