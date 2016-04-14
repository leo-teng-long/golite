# Bubble sort.

def sort(l):
	"""
	Performs in-place bubble sort on a list of integers

	@param list - Integer slice to sort
	"""
	swapped = True
 	
 	while swapped:
 		swapped = False

 		for i in range(1, len(l)):
 			if l[i - 1] > l[i]:
 				tmp = l[i - 1]
	 			l[i - 1] = l[i]
	 			l[i] = tmp

	 			swapped = True

# Seed for pseudo-random number generation. 
seed = 518
# Stores the last pseudo-random number generated.
last = seed

# Generates a pseudo-random number between 0 and 10 inclusive.
def rand():
	global last
	last = (7 * last + 9) % 11
	return last

# Runs for about 4 sec. on Mimi.
def main():
	size = 5786

	print "Generating list of", size, "random integers to sort..."

	l = [rand() for _ in range(size)]

	print "Sorting the list of numbers..."

	sort(l)

	print "Sorted!"

if __name__ == '__main__':
	main()
