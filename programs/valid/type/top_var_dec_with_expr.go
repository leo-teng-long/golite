/* Top variable declaration with expression */
package main

var (
    x, y = 10, 20
)

var z int = x + y

func main() {
    println(z)
}
