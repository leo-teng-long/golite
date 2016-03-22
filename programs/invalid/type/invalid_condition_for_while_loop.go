package main

func main() {
    i := 0
    /* Error: loop condition evaluated to int type */
    for i {
        println(i)
        i++
    }
}
