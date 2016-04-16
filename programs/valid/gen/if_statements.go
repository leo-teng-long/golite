package main

func main() {
	if (true) {
		println("true")
	} else {
		println("false")
	}


	if (false) {
		println("false")
	} else {
		println("true")
	}


	if (false) {
		println("false")
	} else if (true) {
		println("true")
	} else {
		println("false")
	}

	if (false) {
		println("false")
	} else if (false) {
		println("false")
	} else {
		println("true")
	}

	if (true) {
		println("true")
	}


	if (false) {
		println("false")
	}


	if (false) {
		println("false")
	} else if (true) {
		println("true")
	} 

	if (false) {
		println("false")
	} else if (false) {
		println("false")
	} 



	for i:=0; i<10; i++ {
		if i<5 {
			continue
		}
		println(i)
	}



}