package main

var array []int

func binarySearch(array []int, size, target int) int {
    low := 0
    high := size - 1

    for low <= high {
        mid := (low + high) / 2
        
        if (array[mid] < target) {
            low = mid + 1
        } else if (array[mid] > target) {
            high = mid - 1
        } else {
            return mid
        }
    }

    return -1
}

func main() {
    for i := 1; i <= 10; i++ {
        array = append(array, i)
    }

    println("Index of 10:", binarySearch(array, 10, 10))
}
