package main

func main() {
	var c cat
	var b notcat
	i := foocat(c, b)
}

func foocat(c cat, b notcat) notcat {
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