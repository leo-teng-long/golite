package main

var s []int
var ds [][]int
var ts [][][]int

func main () {
	s = append(s, 0)
	s = append(s, 1)
	s = append(s, 2)
	s = append(s, 3)
	ds = append(ds, s)
	ts = append(ts, ds)
}
