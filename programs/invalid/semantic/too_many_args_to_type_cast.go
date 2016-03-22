/* Too many arguments to conversion. */

package main

type point struct {
	x, y float64
}

var x float64 = 3.0
var y float64 = float64(x)
var z point = point(x, y) // Error: too many arguments to conversion to point

func main() {
	// Do nothing
}
