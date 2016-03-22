 /* Short assignment in the last part of a three-part loop. */

package main;

func main() {
    for i := 1 ; true ; j := 1 { // Error: Can't have a short assignment in the last part.

    }
}
