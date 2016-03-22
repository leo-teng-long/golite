package main

var s []this

type this struct {
	a, b int
}

type that struct {
	a, b this
}

func main() {
	var e that
	var t this
	e.a = t
}