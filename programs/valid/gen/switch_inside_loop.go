/* Switch inside a loop. */

package main

func main() {
	for i := 0; i < 3; i++ {
		switch i {
			case 0, 2:
				println("Even")
			case 1, 3:
				println("Odd")
		}
	}
}
