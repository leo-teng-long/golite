package main

var s []this

type this struct {
	a, b int
}

type that struct {
	a, b float64
}

func main () {
	var e that
	s = append(s, e)
}