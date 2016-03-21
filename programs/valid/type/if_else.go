/* Type check if-else */
package main

func main() {
    if i := 10; i % 2 == 0 {
        println(i)
        println("Even")
    } else {
        i := 0
        println(i)
    }
}
