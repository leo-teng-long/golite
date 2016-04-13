package main

type cats int
type foo float64
type c bool
type d rune

func main () {
	var i = cats(1)
	j := foo(5.4)
	var k = c(true)
	l := d('c')
}
