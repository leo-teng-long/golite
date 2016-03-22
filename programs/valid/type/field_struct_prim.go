package main

var s []this

type this struct {
	a int
	b float64
	c string
	d bool
	e rune
}

func main() {
	var z this
	z.a = 1
	z.b = 1.0
	z.c = "hello"
	z.c = `hello`
	z.d = true
	z.e = 'e'
}