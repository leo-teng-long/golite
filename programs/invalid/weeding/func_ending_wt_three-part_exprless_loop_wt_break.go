/* Function ending with a three-part expressionless loop with a break statement
 * inside. */

package main

func foo(x, y int) int {
    for i := 0 ; ; i++ {
    	x++
    	break
    	y++
    }
}

func main() {
    // Do nothing.
}
