package main

func main() {
	var c cat
	var i notcat
	i = foocat(c)
}

func foocat(c cat) cat {
	return c
}

type notcat struct {
	fur bool
	stripes int
}

type cat struct {
	fur bool
	stripes int
}