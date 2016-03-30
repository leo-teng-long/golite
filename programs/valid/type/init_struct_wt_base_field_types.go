/* Initializing struct fields with base types. */

package main

type S struct {
	f, g int
	h float64
	i rune
	j, k string
}

func main() {
	var s S
  	s.f = 0
  	s.g = 1
  	s.h = 0.
  	s.i = 'a'
  	s.j = ""
  	s.k = ""
}
