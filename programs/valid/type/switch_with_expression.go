/* Type check swtich statement #1 */
package main

func main() {
    i := 0
    println(i)

    var num string = "TWO"
    switch i := 0.0; num {
        case "ONE":
            println("Not prime")
        case "TWO", "THREE":
            println("Prime")
        default:
            i := 10
            println(i)
    }
}
