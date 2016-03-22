/* Field access on a constant-like expression. */

package main;

type t struct {
	f struct {
		g int
	}
}

func main() {
    var o t
    (o.f + 1).g = 0
}
