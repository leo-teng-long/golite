package main

var s []this

type this struct {
	a, b int
}

type that struct {
	a, b this
}

type other struct {
	a, b struct {
		a, b int
	}
}

type another struct {
	a, b struct {
		a, b float64
	}
}

type similar struct {
	a, b int
}

type quatchi struct {
	a, b float64
}


func main() {
	var e that
	var s similar
	e.b = s
	//e.b = s
	//a.a = s
}