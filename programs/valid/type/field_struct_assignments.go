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
	var t this
	e.a = t
	var s similar
	//e.b = s
	var a another
	var k quatchi
	a.a = k
	//e.b = s
	//a.a = s
}