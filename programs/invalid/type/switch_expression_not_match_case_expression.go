package main

func main() {
    message := "A"
    switch message {
        /* Error: rune type not match type of message */
        case 'A':
            println(message)
    }
}
