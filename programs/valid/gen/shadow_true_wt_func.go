/* Shadow true with a 'true' function. */

package main


func true() bool {
	return false
}

func main() {
	if (!true()) {
		print("0")	
	}
}
