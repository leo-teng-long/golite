/* If statements */

package main

func main() {
	x := true
	y := false

	if x {
		//Do nothing
	}

	if x {
		//Do nothing
	} else {
		//Do nothing
	}

	if y {
		//Do nothing
	} else if x {
		//Do nothing
	} else {
		//Do nothing
	}

	if z:= true; z {
		println(z)
	}

	if z:= true; x {
		println(z)
	} else {
		//Do nothing
	}
	
	if z:= true; y {
		println(z)
	} else if z:= true; x {
		println(z)
	} else {
		//Do nothing
	}
}
