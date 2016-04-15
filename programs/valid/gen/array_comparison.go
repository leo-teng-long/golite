package main

func main(){
	var x [10]int
	var y [10]int
	println(x == y)
	x[3] = 2
	println(x == y)
	var a [10]rune
	var b [10]rune
	println(a == b)
	b[7] = 'k'
	println(a == b)
}