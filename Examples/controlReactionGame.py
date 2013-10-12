from control import *
import time, random

startTime = time.time()
isTesting = False
running = True

def react(ci, data):
	global isTesting, startTime
	if data[0] == "0":
		if isTesting:
			print("You took {0:.2f} seconds to press the button".format(time.time()-startTime))
			istTesting = False

def quit(ci, data):
	global controller, running
	controller.close()
	running = False

s = Sensors([])
layout = Layout(s,ORIENTATION_PORTRAIT)
box1 = Box(layout, BOX_VERTICAL, GRAVITY_NONE)
b1 = Button(layout, "Press to react!", react, width=WIDTH_FILL, height=HEIGHT_FILL)
box1.add(b1)
layout.add(box1)
box2 = Box(layout, BOX_ACROSS, GRAVITY_BOTTOM)
b2 = Button(layout, "Quit, (NOOOOOOO)!", quit)
box2.add(b2)
box1.add(box2)

controller = Controller("Reaction Game", layout)
controller.start()
pressNow = """  _____                        _   _               
 |  __ \                      | \ | |              
 | |__) | __ ___  ___ ___     |  \| | _____      __
 |  ___/ '__/ _ \/ __/ __|    | . ` |/ _ \ \ /\ / /
 | |   | | |  __/\__ \__ \    | |\  | (_) \ V  V / 
 |_|   |_|  \___||___/___/    |_| \_|\___/ \_/\_/"""

while running:
	time.sleep(int(random.random()*7))
	isTesting = True
	startTime = time.time()
	print(pressNow)
	while True:
		if isTesting == False or running == False:
			break
