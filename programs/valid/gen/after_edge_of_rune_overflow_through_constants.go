/* After edge of integer overflow through addition of literals. */

package main

func main() {
	print(rune(2147483647) + rune(1))
}
