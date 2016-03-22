/* Array is not non-constant nor a function call. */

package main;

func main() {
    var a [1] int
    println(((a + 1))[0]) // Error: Array part can't be an expression like that.
}
