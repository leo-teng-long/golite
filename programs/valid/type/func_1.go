package main

type cat struct {
	fur bool
	stripes int
}

func foo() [100]cat {
	var x [100]cat
	return x
}

func main() {
	foo()
}
