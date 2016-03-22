package main

func main() {
    i, j := 1, 2
    var k rune = 'a'
    /* Error: no new ids declared */
    i, j, k := 2, 3, 'b'
}
