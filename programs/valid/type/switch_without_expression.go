/* Type check switch statement #2 */
package main

func main() {
    switch i := 6; {
        case i % 2 != 0:
            println("Odd")
        case i % 2 == 0:
            println("Even")
    }
}
