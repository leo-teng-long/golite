/* Equality of two simple structs with defaults. */

package main

var x struct {
	a, b int
	c float64
}

var y struct {
	a, b int
	c float64
}

func main() {
	print(x == y)
}
