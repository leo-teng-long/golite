package main

func main() {
	x := 5
	var y, z []int
	var n [5]float64
	var c []cat
	var ca [10]cat
	foo(1, true, 5.4, 6.3, 'c', 100.3, "hello", "world", true)
}

func foo(x int, g bool, z int, a, h float64, b rune, i float64, c, d string, e bool) {
}

type cat struct {
	fur bool
	stripes int
}