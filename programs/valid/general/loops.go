/* Loops */

package main

func main() {
	sum := 0
	for i := 0; i < 10; i++ {
		sum += 1
	}
	for i := 0; i < 10; i++ {
		sum -= 1
	}
	for i := 0; i < 10; i++ {
		sum++
	}
	for i := 0; i < 10; i++ {
		sum--
	}
	for {
		break
	}
	for sum < 10 {
		sum++
	}
	for ; sum > 0; {
		sum--
	}
}
