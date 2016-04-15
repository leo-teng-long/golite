/* Default value for a multi-dimensional rune array. */

package main

var dim1, dim2 = 10, 10
var matrix [10][10]rune

func main() {
	print(matrix[dim1 - 1][dim2 - 1])
}
