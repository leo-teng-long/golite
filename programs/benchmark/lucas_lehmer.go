/* Lucas-Lehmer test for testing the primality of Mersenne primes (i.e. Primes
 * of the form 2^p - 1 for some integer p). */

package main

// Calculate x^n.
func exp(x, n int) int {
	var r int = 1
	for i := 0 ; i < n ; i++ {
		r *= x
	}

	return r
}

// Apply the Lucas Lehmer test to check whether 2^p - 1 is prime or not.
func LucasLehmer(p int) bool {
	var s, m int = 4, exp(2, p) - 1

	for i := 0 ; i < p - 2 ; i++ {
		s = (exp(s, 2) - 2) % m
	}

	return s == 0
}

// Test whether 2^2018796950 is prime or not. Runs in approximately 14 sec.
// using Go.
func main() {
	if LucasLehmer(2018796950) {
		println("Is prime!")
	} else {
		println("Not prime :(")
	}
}
