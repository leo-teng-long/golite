package main

var s []int
var ts [][][]int

func main () {
	s = append(s, 0)
	s = append(s, 1)
	s = append(s, 2)
	s = append(s, 3)
	ts = append(ts, s)
}