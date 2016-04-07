/* Simple function with a terminating return statement. */

package main

func notMain() int {
	x := 5
	y := 10
	return y
}

func main() {
	notMain()
}
