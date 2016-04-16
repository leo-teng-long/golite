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

def foo_1(day_2):
	global true_0, false_0
	if (day_2 == "Monday") or (day_2 == "Tuesday") or (day_2 == "Wednesday") or (day_2 == "Thursday") or (day_2 == "Friday"):
		print("Weekday")
	elif (day_2 == "Saturday") or (day_2 == "Sunday"):
		print("Weekend")
	else:
		print("Invalid")

def main_1():
	global true_0, false_0
	foo_1("Saturday")

#######################################################
###### The miracle from GoLite to Python2.7 ends ######
#######################################################

if __name__ == '__main__':
	main_1()
