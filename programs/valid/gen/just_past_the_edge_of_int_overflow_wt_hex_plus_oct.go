/* Just after the edge of integer overflow through addition of a hexidecimal and
 * and octal. */

package main

func main() {
	x := 0x40000000 + 010000000000
	print(x)
}
