package return_while

func main() {
    plus(1,2)
}

func plus(a, b int) int {
    for {
        return 1
    }
    // ;;;;; // Error: Can't end non-void function with a bunch of empty statements.
}
