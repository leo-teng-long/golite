/* Bubble sort. */

package main

/*
 * Performs in-place bubble sort on a list of integers
 *
 * @param list - Integer slice to sort
 * @param size - Size of list
 */
func sort(list []int, size int) {
	var swapped bool = true
 	
 	for swapped {
 		swapped = false

 		for i := 1; i < size; i++ {
 			if list[i - 1] > list[i] {
 				var tmp int = list[i - 1]
	 			list[i - 1] = list[i]
	 			list[i] = tmp

	 			swapped = true
 			}
 		}
 	}
}

// Seed for pseudo-random number generation. 
var seed int = 518
// Stores the last pseudo-random number generated.
var last int = seed

/* Generates a pseudo-random number between 0 and 10 inclusive. */
func rand() int {
	last = (7 * last + 9) % 11
	return last
}

func main() {
	var list []int
	var size int = 5786

	println("Generating list of", size, "random integers to sort...")

	for i := 0; i < size; i++ {
		list = append(list, rand())
	}

	println("Sorting the list of numbers...")

	sort(list, size)

	println("Sorted!")
}
