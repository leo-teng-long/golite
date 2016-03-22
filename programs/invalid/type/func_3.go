package main

func main() {
	var c [5]cat
	var i [10]cat
	i = foocat(c)
}

func foocat(c [5]cat) [5]cat {
	return c
}

type cat struct {
	fur bool
	stripes int
}