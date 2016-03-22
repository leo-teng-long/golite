package main

type a struct {
	b int
}

type c struct {
	b int
}

func main () {
	var i []a
    var j []c
    b := i == j
}