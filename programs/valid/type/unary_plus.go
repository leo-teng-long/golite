/* Type checking unary plus */
package main

func main() {
    x, y, z := + 10, + 10.0, + 'a'
    println(+ x, + y, + z)
}
