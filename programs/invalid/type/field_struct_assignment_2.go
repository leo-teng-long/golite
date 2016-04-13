package main

type another struct {
	a, b struct {
		a, b float64
	}
}

type quatchi struct {
	a, b float64
}


func main() {
	var a another
	var k quatchi
	a.a = k
}
