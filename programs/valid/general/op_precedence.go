/* Operator precedence */

package main

func main() {
	var x, y, z = 1, 2, 3
	x = x / y * z // Should be the same as (x / y) * z.
}
