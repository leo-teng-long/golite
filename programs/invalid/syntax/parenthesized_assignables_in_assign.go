/* Parenthesizing assignables. */

package main;

type t struct {
	f struct {
		g int
	}
}

func main() {
    var o t
    var i int
    ((o.f + 1).g), (((i))) = 0, 0
}
