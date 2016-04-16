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

def binarySearch_1(array_2, size_2, target_2):
	global true_0, false_0, array_1
	low_2 = 0
	high_2 = (size_2 - 1)
	while (low_2 <= high_2):
		mid_4 = ((low_2 + high_2) / 2)
		if (array_2[mid_4] < target_2):
			low_2 = (mid_4 + 1)
		else:
			if (array_2[mid_4] > target_2):
				high_2 = (mid_4 - 1)
			else:
				return mid_4
	return (- 1)

def main_1():
	global true_0, false_0, array_1
	i_3 = 1
	while (i_3 <= 10):
		array_1.append(i_3)
		i_3 = i_3 + 1
	print("Index of 10:", binarySearch_1(array_1, 10, 10))

#######################################################
###### The miracle from GoLite to Python2.7 ends ######
#######################################################

if __name__ == '__main__':
	main_1()
