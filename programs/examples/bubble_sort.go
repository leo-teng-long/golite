package main

var array []int

func printArray(array []int, size int) {
    for i := 0; i < size; i++ {
        println(array[i])
    }
}

func bubbleSort(array []int, size int)  {
    for i := 0; i < size - 1; i++ {
        swapped := false

        for j := 0; j < size - 1 - i; j++ {
            if array[j] > array[j + 1] {
                temp := array[j]
                array[j] = array[j + 1]
                array[j + 1] = temp
                swapped = true
            }
        }

        if swapped {
            continue
        }
    }
}

func main() {
    for i := 10; i > 0; i-- {
        array = append(array, i)
    }

    println("Before:")
    printArray(array, 10)

    bubbleSort(array, 10)

    println("Sorted:")
    printArray(array, 10)
}
