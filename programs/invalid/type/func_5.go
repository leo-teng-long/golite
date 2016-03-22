package main

func main() {
	var c [5]cat
	var i [5][5]cat
	i = foocat(c, true)
}

func foocat(c cat, b bool) cat {
	return b
}

type cat struct {
	fur bool
	stripes int
}