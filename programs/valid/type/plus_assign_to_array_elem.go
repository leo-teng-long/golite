/* Test type check '+=' to array element */
package main

var array [2]int

func main() {
    array[0] += 2
    println(array[0])
}
