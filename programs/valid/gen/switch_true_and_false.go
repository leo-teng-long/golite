/* Switch true and false. */

package main

var (
	tmp = true
	true = false
	false = tmp
)

func main() {
	// Should enter the if-block.
	if (false) {
		print("Switched true and false...very trixy")
	} else {
		print("You'd think this should get printed, but it shouldn't")
	}
}
