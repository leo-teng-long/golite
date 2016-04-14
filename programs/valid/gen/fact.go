package main

var N = 1000

func main() {
	var fact = 0
	for i := 0; i < N; i++ {
		fact *= i
  	}
  	print(fact)
}