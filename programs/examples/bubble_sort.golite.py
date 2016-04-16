'''

Presented by [The Heapsters]:

	@the Generator: Long, Teng
	@the PrettyPrinter: Macdonald, Ethan
	@the Stoner: Vala, Hardik

'''

from __future__ import print_function

twoExp31, twoExp32 = 2 ** 31, 2 ** 32
normalize = lambda x : (x + twoExp31) % twoExp32 - twoExp31

true_0, false_0 = True, False

#########################################################
###### The miracle from GoLite to Python2.7 begins ######
#########################################################

array_1 = []

def printArray_1(array_2, size_2):
	global true_0, false_0, array_1
	i_3 = 0
	while (i_3 < size_2):
		print(array_2[i_3])
		i_3 = i_3 + 1

def bubbleSort_1(array_2, size_2):
	global true_0, false_0, array_1
	i_3 = 0
	while (i_3 < (size_2 - 1)):
		swapped_4 = false_0
		j_5 = 0
		while (j_5 < ((size_2 - 1) - i_3)):
			if (array_2[j_5] > array_2[(j_5 + 1)]):
				temp_8 = array_2[j_5]
				array_2[j_5] = array_2[(j_5 + 1)]
				array_2[(j_5 + 1)] = temp_8
				swapped_4 = true_0
			j_5 = j_5 + 1
		if swapped_4:
			i_3 = i_3 + 1
			continue
		i_3 = i_3 + 1

def main_1():
	global true_0, false_0, array_1
	i_3 = 10
	while (i_3 > 0):
		array_1.append(i_3)
		i_3 = i_3 - 1
	print("Before:")
	printArray_1(array_1, 10)
	bubbleSort_1(array_1, 10)
	print("Sorted:")
	printArray_1(array_1, 10)

#######################################################
###### The miracle from GoLite to Python2.7 ends ######
#######################################################

if __name__ == '__main__':
	main_1()
