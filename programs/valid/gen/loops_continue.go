package main

var x, y = 0, 0

func main() {

	for i:=0; i < 3; i++ {
	
		for j := 0; j < 3; j++ {
			println("inside")
		}
		println("outside")
	}

	x = 0
	for i:=0; i < 3; i++ {
		for x<1 {
			x = 100
			println("inside")
		}
		println("outside")
	}

	y = 0
	for y<1 {
		y = 100
		for j := 0; j < 3; j++ {
			println("inside")
		}
		println("outside")
	}

	x = 0
	y = 0
	for y<1 {
		y = 100
		for x<1 {
			x = 100
			println("inside")
		}
		
		println("outside")
	}

	for i:=0; i < 3; i++ {
		continue
		for j := 0; j < 3; j++ {
			println("inside")
		}
		println("outside")
	}

	x = 0
	for i:=0; i < 3; i++ {
		continue
		for x<1 {
			x = 100
			println("inside")
		}
		println("outside")
	}

	y = 0
	for y<1 {
		y = 100
		continue
		for j := 0; j < 3; j++ {
			println("inside")
		}
		println("outside")
	}

	x = 0
	y = 0
	for y<1 {
		y = 100
		continue
		for x<1 {
			x = 100
			println("inside")
		}
		println("outside")
	}

	for i:=0; i < 3; i++ {
		for j := 0; j < 3; j++ {
			continue
			println("inside")
		}
		println("outside")
	}

	x = 0
	for i:=0; i < 3; i++ {
		for x<1 {
			x = 100
			continue
			println("inside")
		}
		println("outside")
	}

	y = 0
	for y<1 {
		y = 100
		for j := 0; j < 3; j++ {
			continue
			println("inside")
		}
		println("outside")
	}

	x = 0
	y = 0
	for y<1 {
		y = 100
		for x<1 {
			x = 100
			continue
			println("inside")
		}
		println("outside")
	}

	for i:=0; i < 3; i++ {
		for j := 0; j < 3; j++ {
			println("inside")
		}
		continue
		println("outside")
	}

	x = 0
	for i:=0; i < 3; i++ {
		for x<1 {
			x = 100
			println("inside")
		}
		continue
		println("outside")
	}

	y = 0
	for y<1 {
		y = 100
		for j := 0; j < 3; j++ {
			println("inside")
		}
		continue
		println("outside")
	}

	x = 0
	y = 0
	for y<1 {
		y = 100
		for x<1 {
			x = 100
			println("inside")
		}
		continue
		println("outside")
	}

}