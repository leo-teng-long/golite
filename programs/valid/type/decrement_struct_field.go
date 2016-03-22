/* Test type check '--' to struct field */
package main

type Time struct {
    h, m int
}

func main() {
    var t Time
    t.h--
    println(t.h, t.m)
}
