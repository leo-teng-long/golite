/* Top-level function declaration after it's use. */

package main

func main() {
	f()	// Error: Can't call a function before it's declared.
}

func f() {

}
