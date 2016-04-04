/* Lucas-Lehmer test for testing the primality of Mersenne primes. */

func exp(x, n int) int {
	var r int = 0
	for i := 0 ; i < n ; i++ {
		r *= x
	}

	return r
}

func LucasLehmer(int p) bool {
	s = 0
	m = exp(2, p) - 1

	for i := 0 ; i < p - 2 ; i++ {
		s = (exp(s, 2) - 2) % m
	}

	return s == 0
}

func main() {
	println(LucasLehmer(74207281))
}
