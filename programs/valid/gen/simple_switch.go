/* Switch */

package main

func main() {
	switch food := "dill pickle"; food {
		case "tomato", "lettuce":
			// Do nothing
		case "pepper", "cucumber":
			// Do nothing
		case "dill pickle":
			print("dill pickle")
		default:
			// Do nothing
	}
}
