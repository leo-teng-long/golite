package main

func main() {
	foo()
}

func foo() [100]cat {
	var x [100]cat
	return x
}

type cat struct {
	fur bool
	stripes int
}