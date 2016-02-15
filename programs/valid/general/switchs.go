/* Switch statements. */

package main

func main() {
	switch food := "dill pickle"; food {
		case "tomato":
			// Do something	
		case "pepper":
			// Do something
			fallthrough
		case "cucumber":
			// Do something
		default:
			// Do something
	}
	
	switch food := "dill pickle"; {
		case "tomato":
			// Do something	
		case "pepper":
			// Do something
			fallthrough
		case "cucumber":
			// Do something
		default:
			// Do something
	}
	food := "dill pickle"
	switch {
		case food == "tomato":
			// Do something	
		case food == "pepper":
			// Do something
			fallthrough
		case food == "cucumber":
			// Do something
	}
}
