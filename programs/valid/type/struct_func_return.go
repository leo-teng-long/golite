package main

type a struct {
	b, c int
	d rune
	e struct {
		f int
		g float64
	}
}

func foo() a {
	var y a
	return y
}

func main() {
	var x a = foo()
}
