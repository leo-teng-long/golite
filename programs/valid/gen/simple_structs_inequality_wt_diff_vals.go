/* Equality of two simple structs with different values. */

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
	y.a = 1
	print(x == y)
}
