package main

func main() {
    switch i := "A"; {
        /* Error: case needs to be follow by boolean */
        case "A":
            println("B")
        case "B":
            println("C")
        default:
            println(i)
    }
}
