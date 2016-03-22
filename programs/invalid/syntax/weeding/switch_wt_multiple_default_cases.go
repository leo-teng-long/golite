/* Switch statement with multiple default cases. */

package main;

func main() {
	switch food := "dill pickle"; food {
		case food == "tomato":
			// Do something
		case food == "pepper":
			// Do something
		default:
			// Do something
		case food == "cucumber":
			// Do something
		default: // Error: Multiple default cases.
			// Do something
	}
}
