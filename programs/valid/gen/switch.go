package main

func main() {
    i := 2

    switch i {
    case 1:
        println("one")
    case 2,3:
        println("twothree")
    case 4:
        println("four")
    }

    switch {
    case i < 12:
        println("I EAT CHEETOS!!!")
    default:
        println("I'm so alone :(")
    }

    switch {
    case i >= 12:
        println("I EAT CHEETOS!!!")
    default:
        println("I'm so alone :(")
    }
}