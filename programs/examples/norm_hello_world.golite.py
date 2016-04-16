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

def main_1():
	global true_0, false_0
	print("Hello world!")
	print(str("Hello") + str(" "), end = '')
	print(str("world") + str("!"), end = '')

#######################################################
###### The miracle from GoLite to Python2.7 ends ######
#######################################################

if __name__ == '__main__':
	main_1()
