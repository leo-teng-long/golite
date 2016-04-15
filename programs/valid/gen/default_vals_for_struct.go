/* Default struct values. */

package main

var s struct {
	a, b int
	c float64
	d struct {
		e string
	}
	f [1]rune
}

func main() {
	println(s.a)
	println(s.b)
	println(s.c)
	println(s.d.e)
	println(s.f[0])
}
