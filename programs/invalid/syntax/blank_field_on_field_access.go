/* Blank field on field access. */

package main

type t struct {
	_ int
}

func main() {
	var x t
    var a = x._
}
