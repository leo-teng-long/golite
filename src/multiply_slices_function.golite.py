'''

Presented by [The Heapsters]:

	@ Long, Teng
	@ Macdonald, Ethan
	@ Vala, Hardik

'''

from __future__ import print_function

twoExp31, twoExp32 = 2 ** 31, 2 ** 32
normalize = lambda x : (x + twoExp31) % twoExp32 - twoExp31

true_0, false_0 = True, False

#########################################################
###### The miracle from GoLite to Python2.7 begins ######
#########################################################

def multiply_1(x_2, y_2, z_2):
	global true_0, false_0
	if z_2:
		i_5 = 0
		while (normalize(i_5) < normalize(y_2)):
			x_2[i_5] = normalize((normalize(x_2[i_5]) * normalize(x_2[i_5])))
			i_5 += 1
			i_5 = normalize(i_5)
	else:
		i_5 = 0
		while (normalize(i_5) < normalize(y_2)):
			x_2[i_5] = normalize((normalize(x_2[i_5]) / normalize(x_2[i_5])))
			i_5 += 1
			i_5 = normalize(i_5)
	return x_2

def main_1():
	global true_0, false_0
	x_2 = []
	x_2.append(1)
	x_2.append(2)
	x_2.append(3)
	x_2.append(4)
	x_2.append(5)
	print(str(x_2[0]), end = '')
	print(str(x_2[3]), end = '')
	print(str(multiply_1(x_2, 5, true_0)[0]), end = '')
	print(str(multiply_1(x_2, 5, false_0)[3]), end = '')

#######################################################
###### The miracle from GoLite to Python2.7 ends ######
#######################################################

if __name__ == '__main__':
	main_1()
