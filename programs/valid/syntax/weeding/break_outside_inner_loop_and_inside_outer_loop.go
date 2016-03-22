/* Break statement outside inner loop, but inside outer loop. */

package main

func main() {
	for i := 0; i < 10; i++ {
		for j := 0; j < 10; j++ {
		
		}

		break
	}
}
