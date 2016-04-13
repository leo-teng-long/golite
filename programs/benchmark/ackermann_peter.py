# Compute the Ackermann-Peter function (Iteratively using a stack so as not to
# exceed the max. recursion depth).
#
# (See: http://www.math.tau.ac.il/~nachumd/verify/ackermann.html)

# Pre-condition: m, n >= 0.
def AckermannPeter(m, n):
	# Stores intermediate values of the function.
	stack = []

	# Initialize the stack with m.
	stack.append(m)

	# Iterate until the stack is empty.
	while len(stack) > 0:
		# Pop the stack.
		m = stack[-1]
		stack = stack[:-1]

		# A(0, n) = n + 1
		if m == 0:
			n += 1
		# A(m, 0) = A(m - 1, 1)
		elif n == 0:
			n = 1

			stack.append(m - 1)
		# A(m - 1, A(m, n - 1))
		else:
			n -= 1

			stack.append(m - 1)
			stack.append(m)

	return n

# Compute the Ackermann-Peter function at m = 3 and n = 8 and print the
# result (Runs for approx. 40 sec. on Mimi).
def main():
	print "Ackermann-Peter(3, 8) = ", AckermannPeter(3, 8)

if __name__ == '__main__':
	main()
