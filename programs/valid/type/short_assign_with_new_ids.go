/* Test short assign ':=' with new ids */
package main

func main() {
    var i int = (1 + 2)
    j := 2.2 - 1.0
    println(i, j)
    i, j, k := 12, 1.2, 'a'
    println(i, j, k)
}
