/* Compute the Ackermann-Peter function (Iteratively using a stack so as not to
 * exceed the max. recursion depth).
 *
 * (See: http://www.math.tau.ac.il/~nachumd/verify/ackermann.html) */

package main

// Pre-condition: m, n >= 0.
func AckermannPeter(m, n int) int {
	// Stores intermediate values of the function.
	var stack []int
	// Stores the size of the stack.
	var size int = 0

	// Initialize the stack with m.
	stack = append(stack, m)
	size++

	// Iterate until the stack is empty.
	for size > 0 {
		// Pop the stack.
		m = stack[size - 1]
		
		var tmp []int
		for i := 0 ; i < size - 1; i++ {
			tmp = append(tmp, stack[i])
		}

		stack = tmp
		size--

		// A(0, n) = n + 1
		if m == 0 {
			n += 1
		// A(m, 0) = A(m - 1, 1)
		} else if n == 0 {
			n = 1

			stack = append(stack, m - 1)
			size++
		// A(m - 1, A(m, n - 1))
		} else {
			n -= 1

			stack = append(stack, m - 1)
			size++
			stack = append(stack, m)
			size++
		}
	}

	return n
}

// Compute the Ackermann-Peter function at m = 4 and n = 2 and print the result.
func main() {
	println("A(4, 2) = ", AckermannPeter(4, 2))
}
