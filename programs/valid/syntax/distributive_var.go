/* "Distributive" var declarations. */

package main

var ( a bool = true )

var (
        b rune
        c1, c2 string )

var (
        d int
        e int = 0)

var (
        x int
        y float64 = x
        z1, z2 string
)

func main() {
	// Do nothing
}
