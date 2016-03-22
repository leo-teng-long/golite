/* Field access with intermediate parenthisization. */

package main;

type t struct {
	f struct {
		g int
	}
}

func main() {
    var o t
    ((o.f)).g = 0
}
