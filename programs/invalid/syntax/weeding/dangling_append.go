/* "Dangling" append. */

package main

func main() {
	var slice [] int
	append(slice, 0) // Error: Can't have "dangling" append.
}
