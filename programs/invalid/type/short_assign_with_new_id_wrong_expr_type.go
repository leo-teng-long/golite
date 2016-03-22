package main

func main() {
    i := (1 + 2)
    var j float64 = 2.2 - 1.2
    /* Error: mismatched expression type */
    i, j, k := 2.2, 1.0, 'a'
}
