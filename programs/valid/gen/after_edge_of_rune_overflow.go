/* After edge of integer overflow. */

package main

func main() {
	x := '\n'/'\n'
	for i := 0 ; i < 32; i++ {
		x *= '\n'/'\n' + '\n'/'\n'
	}

	print(x)
}
