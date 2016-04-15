package main

type Cat struct {
	x, y int
}

func main() {
	var cat_array [2]Cat
	cat_array[0].x = 5
	cat_array[0].y = 3
	cat_array[1].x = int('a')
	cat_array[1].y = int('k')
	println(cat_array[0].x, cat_array[0].y)
	println(cat_array[1].x, cat_array[1].y)

	var struct_array [2] struct {
		s string
	}
	
	struct_array[0].s = "interp"
	struct_array[1].s = `raw`
	println(struct_array[0].s)
	println(struct_array[1].s)
}