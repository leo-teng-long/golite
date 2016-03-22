package main

func main() {
	var c [5]cat
	var b [5]notcat
	i := foocat(c, b)
}

func foocat(b [5]notcat) [5]notcat {
	return b
}

type notcat struct {
	fur bool
	stripes int
}

type cat struct {
	fur bool
	stripes int
}