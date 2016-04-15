package main

func multiply(x []int, y int, z bool) []int {
	if (z){
		for i:=0; i<y; i++ {
			x[i]=x[i]*x[i]
		}
	} else {
		for i:=0; i<y; i++ {
			x[i]=x[i]/x[i]
		}
	}
	return x
}

func main() {
	var x [] int
	x = append(x, 1)
	x = append(x, 2)
	x = append(x, 3)
	x = append(x, 4)
	x = append(x, 5)
	print(x[0])
	print(x[3])
	print(multiply(x, 5, true)[0])
	print(multiply(x, 5, false)[3])
}