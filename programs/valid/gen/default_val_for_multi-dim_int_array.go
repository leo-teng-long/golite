/* Default value for a multi-dimensional int array. */

package main

var dim1, dim2 = 10, 10
var matrix [10][10]int

func main() {
	print(matrix[dim1 - 1][dim2 - 1])
}
