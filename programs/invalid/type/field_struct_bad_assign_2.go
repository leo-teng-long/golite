package main

var s []this

type this struct {
	a, b int
}

type that struct {
	a, b this
}

type similar struct {
	a, b int
}

func main() {
	//var e that
	var e that
	var s similar
	e.b = s
}