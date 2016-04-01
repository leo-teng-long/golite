/* Print struct variable. */

package main

func main() {
	var s struct {

	}

	print(s) // Error: Can't print struct.
}
