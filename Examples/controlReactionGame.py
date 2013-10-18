from control import *
import time, random

st = time.time()
isTesting = False
running = True

def react(ci, data):
	global isTesting
	if data[0] == "1":
		if isTesting:
			print("You took {0:.2f} seconds".format(time.time()-st))
			isTesting = False
		else:
			pass
def quit(ci,data):
	global c, running
	c.close()
	running = False

s = Sensors([])
l = Layout(s,ORIENTATION_PORTRAIT)
box = Box(l,BOX_VERTICAL,GRAVITY_NONE)
l.add(box)
b = Button(l, "Press to react", react, width=WIDTH_FILL, height=HEIGHT_FILL)
box.add(b)
box2 = Box(l,BOX_ACROSS,GRAVITY_BOTTOM)
box.add(box2)
qb = Button(l, "Quit (NOOOO!)", quit)
box2.add(qb)
c = Controller("Reaction Test", l)
c.start()

while running:
	time.sleep(int(random.random()*7))
	st = time.time()
	isTesting = True
	print("""  _____                        _   _               
 |  __ \                      | \ | |              
 | |__) | __ ___  ___ ___     |  \| | _____      __
 |  ___/ '__/ _ \/ __/ __|    | . ` |/ _ \ \ /\ / /
 | |   | | |  __/\__ \__ \    | |\  | (_) \ V  V / 
 |_|   |_|  \___||___/___/    |_| \_|\___/ \_/\_/""")
	while True:
		if isTesting == False or running == False:
			break