package main

func foo(day string) {
    switch day {
        case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday":
            println("Weekday")
        case "Saturday", "Sunday":
            println("Weekend")
        default:
            println("Invalid")
    }
}

func main() {
    foo("Saturday")
}
