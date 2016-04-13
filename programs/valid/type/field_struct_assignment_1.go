package main

type this struct {
	a, b int
}

type that struct {
	a, b this
}

var s []this

func main() {
	var e that
	var t this
	e.a = t
}
