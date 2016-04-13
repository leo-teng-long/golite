/* Equality of two structs with empty nested structs. */

package main

var x struct {
	a, b int
	c struct {

	}
}

var y struct {
	a, b int
	c struct {

	}
}

func main() {
	x.a = 1
	x.b = 2

	y.a = 1
	y.b = 2

	print(x == y)
}
