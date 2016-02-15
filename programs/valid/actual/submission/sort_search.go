/* First sort a given array using selection sort, then do binary search on the
 * sorted array to find the index of the target. */

package main

// Array to be sorted.
var nums []int = []int{10, 9, 8, 7, 6, 5, 4, 3, 2, 1}

/* Main */
func main() {
  selectionSort(nums, 10)
  println(binarySearch(nums, 10, 10) >= 0)
}

/* Selection Sort */
func selectionSort(nums []int, size int) {
  for i := 0; i < size; i++ {
    min := i
    for j := i + 1; j < size; j++ {
      if nums[min] > nums[j] {
        min = j
      }
    }

    temp := nums[i]
    nums[i] = nums[min]
    nums[min] = temp
  }
}

/* Binary Search */
func binarySearch(nums []int, size int, target int) int {
  low := 0
  high := size - 1

  for low <= high {
    mid := (low + high) / 2
    if nums[mid] < target {
      low = mid + 1
    } else if nums[mid] > target {
      high = mid - 1
    } else {
      return mid
    }
  }

  return -1
}
