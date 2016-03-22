package main

var s []int
var ds [][]int
var ts [][][]int

func main () {
	append(s, 0)
	append(s, 1)
	append(s, 2)
	append(s, 3)
	append(ds, s)
	append(ts, ds)
}