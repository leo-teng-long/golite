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
	z.c = 'r'
}