package main

func main() {
	var c [5]cat
	var i [5][5]cat
	i = foocat(c)
}

func foocat(c [5]cat) [5]cat {
	return c
}

type cat struct {
	fur bool
	stripes int
}