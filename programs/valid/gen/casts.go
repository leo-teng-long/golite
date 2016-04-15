package main

type Cat int

func main() {
	a := int(4.0)
	b := float64(8)
	c := rune(1830)
	var d, e, f, g = int(4), float64(374.23), rune('k'), bool(true)
	var h = Cat(1029)
	i := Cat(7)
	println(a, b, c, d, e, f, g, h, i, bool(false))
}