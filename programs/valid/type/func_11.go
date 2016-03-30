package main

type cat struct {
	fur bool
	stripes int
}

func foocatarray(x int, g bool, z int, a, h float64, b rune, i float64, c, d string, e bool, l, m []int, n [5]float64, cats []cat, catarray [10]cat) [10]cat {
	return catarray	
}

func main() {
	x := 5
	var y, z []int
	var n [5]float64
	var c []cat
	var ca [10]cat
	h := foocatarray(1, true, 10+x, 5.4+float64(x), 6.3*2.0, 'c', 100.3, "hello", "world", 1 == 100, y, z, n, c, ca)
}
