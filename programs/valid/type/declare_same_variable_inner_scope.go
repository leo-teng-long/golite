/* Type check shadowing variables */
package main

var i int = 0

func main()  {
    i := 0.0
    println(i)
}
