import socket, select, threading, pyglet, math, random, thread, sys
from pyglet.gl import *
 
win = pyglet.window.Window(resizable=True,fullscreen=True,caption='Control Snake')
icon1 = pyglet.image.load('control32.png')
icon2 = pyglet.image.load('control512.png')
win.set_icon(icon1, icon2)


red = (1.0,0.0,0.0)
green = (0.0,1.0,0.0)
blue = (0.0,0.0,1.0)

yellow = (1.0,1.0,0.0)
orange = (1.0,0.5,0.0)
purple = (0.5,0.0,1.0)
pink = (1.0,0.0,1.0)

rainbow = (red,orange,yellow,green,blue,purple,pink)
rcc = (orange,yellow,green,blue,purple,pink)

ssize = 72
snake = [(1,3),(2,3)]
powerups = [(4,3,orange,pink)]
direction = (0, 1)
r = {'u':range(0,45)+range(-45,0),'l':range(-135,-45),'d':range(-180,-135)+range(135,180),'r':range(45,135)}
connected = False
paused = False
lost = False
level = 0
cd = 3
w = 640
h = 480

@win.event
def on_resize(width, height):
	global w, h
	w = width
	h = height

def draw_square(x,y,size,c):
	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
        glBegin(GL_TRIANGLES)
	glColor3f(c[0],c[1],c[2])
        glVertex2i(size+x,size+y)
        glVertex2i(size+x,y)
        glVertex2i(x,y)
        glVertex2i(x,y)
        glVertex2i(x,size+y)
        glVertex2i(size+x,size+y)
        glEnd()

def draw_powerup(x,y,size, c1, c2):
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
        glBegin(GL_TRIANGLES)
	glColor3f(c1[0],c1[1],c1[2])
        glVertex2i(size+x,size+y)
        glVertex2i(size+x,y)
        glVertex2i(x,y)
	glColor3f(c2[0],c2[1],c2[2])
        glVertex2i(x,y)
        glVertex2i(x,size+y)
        glVertex2i(size+x,size+y)
        glEnd()

def draw_bars(x,y,lines):
	#111
	#2 3
	#444
	#5 6
	#777
	ld = {7:[(0,0),(72,0),(72,10),(72,10),(0,10),(0,0)], 4:[(0,40),(72,40),(72,50),(72,50),(0,50),(0,40)], 1:[(0,80),(72,80),(72,90),(72,90),(0,90),(0,80)],  5:[(0,0),(14,0),(14,50),(14,50),(0,50),(0,0)], 2:[(0,40),(14,40),(14,90),(14,90),(0,90),(0,40)], 6:[(58,0),(72,0),(72,50),(72,50),(58,50),(58,0)], 3:[(58,40),(72,40),(72,90),(72,90),(58,90),(58,40)]}
	for j in lines:
		glColor3f(1.0,1.0,1.0)
		glBegin(GL_TRIANGLES)
		for i in ld[j]:
			glVertex2i(i[0]+x,i[1]+y)
		glEnd()

def draw_chrs(x,y,word):
	ld = {'1': [2,5], '2': [1,3,4,5,7], '3': [1,3,4,6,7], '4': [2,3,4,6], '5': [1,2,4,6,7], '6': [1,2,5,7,6,4], '7': [1,3,6], '8': [1,2,3,4,5,6,7], '9': [1,2,3,4,6], '0': [1,2,5,7,6,3], 'c': [1,2,5,7], 'o': [4,5,6,7], 'n': [4,5,6], 't': [3,4,6], 'r': [5,4], 'l': [2,5,7], 's': [1,2,4,6,7], 'e': [1,2,3,4,5,7], 'y': [2,3,4,6,7], 'u': [5,6,7], 'p': [1,2,3,4,5], 'a': [1,2,3,4,5,6], 'i': [1,3,6,7], ' ': []}
	for i in enumerate(word):
		draw_bars(x+(i[0]*80), y, ld[i[1]])

@win.event
def on_draw():
	global w, h, level
	glClear(GL_COLOR_BUFFER_BIT)
	
	colour = 0
	for square in snake:
		#print(colour)
		draw_square(square[0]*ssize, square[1]*ssize, ssize, rainbow[colour%6])
		colour+=1
	
	for square in powerups:
		draw_powerup(square[0]*ssize+12, square[1]*ssize+12, 48, square[2], square[3])
	
	glColor3f(1.0,1.0,1.0)
	glBegin(GL_LINES)
	iy, ix = 0,0
	while iy < h:
		glVertex2i(w,iy)
		glVertex2i(0,iy)
		iy+=ssize
	while ix < w:
		glVertex2i(ix, 0)
		glVertex2i(ix, h)
		ix+=ssize
        glEnd()
	
	draw_chrs(w-(len(snake)/10*80)-80,h-110,str(len(snake)))

	if lost:
		glColor4f(1.0,0.5,0.0,0.25)
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
		glBegin(GL_TRIANGLES)
		glVertex2i(0,0)
		#background
		glVertex2i(0,h)
		glVertex2i(w,h)
		glVertex2i(w,h)
		glVertex2i(w,0)
		glVertex2i(0,0)
		glColor4f(0.0,0.0,0.5,0.25)
		glEnd()
		w2,h2=w/2,h/2
		draw_chrs(w2-320,h2+10,'you lost')
		draw_chrs(w2-440,h2-110,'restart in '+str(cd))
	if not connected: #show the control logo
		glColor4f(0.0,0.0,1.0,0.25)
		w2,h2=w/2,h/2
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
		glBegin(GL_TRIANGLES)
		glVertex2i(0,0)
		#background
		glVertex2i(0,h)
		glVertex2i(w,h)
		glVertex2i(w,h)
		glVertex2i(w,0)
		glVertex2i(0,0)
		#back square
		glColor4f(0.2,0.2,1.0,0.75)
		glVertex2i(w2-60,h2-80)
		glVertex2i(w2-60,h2+80)
		glVertex2i(w2+60,h2+80)
		glVertex2i(w2+60,h2+80)
		glVertex2i(w2+60,h2-80)
		glVertex2i(w2-60,h2-80)
		glColor4f(1.0,1.0,1.0,0.75)
		#topTri
		glVertex2i(w2-40,h2+20)
		glVertex2i(w2+40,h2+20)
		glVertex2i(w2,h2+60)
		#lowerTri
		glVertex2i(w2-40,h2-20)
		glVertex2i(w2+40,h2-20)
		glVertex2i(w2,h2-60)
		glEnd()
		draw_chrs(w2-276,120,'connect')
		draw_chrs(w2-276,10,'control')
		
def move(var):
	global snake, direction, powerups, connected, paused, rcc, lost, cd, level, w, h
	if (not paused) and (not lost) and connected:
		snake.pop(0)
		tryPos = (snake[-1][0]+direction[0],snake[-1][1]+direction[1])
		if tryPos[0] > (w/ssize):
				tryPos = (0,tryPos[1])
		if tryPos[0] < 0:
				tryPos = (w/ssize,tryPos[1])
		if tryPos[1] > (h/ssize):
				tryPos = (tryPos[0],0)
		if tryPos[1] < 0:
				tryPos = (tryPos[0],h/ssize)
		snake.append(tryPos)
		
		if len(snake) != len(set(snake)):
			lost = True
			cd = 6
		
		for pu in powerups:
			if pu[0] == snake[-1][0] and pu[1] == snake[-1][1]:
				snake.append((snake[-1][0]+direction[0],snake[-1][1]+direction[1]))
				powerups = []
				level+=1
				for i in range(2):
					powerups.append((((int(random.random()*(w/ssize)))+1), ((int(random.random()*(h/ssize)))+1), rcc[int(random.random()*6)], rcc[int(random.random()*6)]))
	if lost:
		cd-=1
	if lost and cd == 0:
		lost = False
		snake = [(1,3),(2,3)]
		powerups = [(4,3,orange,pink)]
		direction = (0, 1)

def connection(css, nothing_at_all):
		global direction, connected, cs, r, snake, paused, win
		try:
				print('accepted')
				css.recv(4096)
				css.send("1")
				css.recv(4096)
				css.send("1_0_1_48_-1_-1_1~8_1_^\nUp Is This Way\n\n\nSpin Your Phone To Control: Try Spinning Flat It On A Table_22_1_-1_-1_1~1_1_0_80_-1_-1_1~0_2_Quit_Q:_-2_-2_1~3_2_1_Pause_Play_PPB:_-2_-2_1")
				css.send("000001")
				css.recv(4096)
				print(css)
				connected = True
			
				deg = 0
				while True:
					msg = css.recv(4096).decode('UTF-8').strip()
					m = msg.split(":")
					#print(m[0])
					if msg[:4] == "PPB:":
						if "1" in m[1]:
							paused = True
						else:
							paused = False
					elif msg[:2] == "Q:":
						win.close()
						pyglet.clock.unschedule(move)
						sys.exit(0)
					else:
						try:
							deg = int(float(m[1]))
							if deg > -45 and 45 > deg: #up
								direction = (0,1)
							elif deg in r['d']: #down
								direction = (0,-1)
							elif deg in r['l']: #l
								direction = (-1,0)
							elif deg in r['r']: #r
								direction = (1,0)
							else:
								pass	
						except (ValueError,IndexError):
							pass
		except IOError:
				print("IOE")
				connection = False
				pass

class control_thread(threading.Thread):
	def run(self):
		global direction, connected, cs, r, snake
		inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		inform.connect((socket.gethostbyname(socket.gethostname()),3334))
		inform.send("Snake")
		msg = inform.recv(4096).decode('ascii').strip()
		inform.close()
		
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
		s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
		while True: 
			s.listen(1)
			(css, addr) = s.accept()
			thread.start_new_thread(connection,(css,0))
ct = control_thread()
ct.start()
pyglet.clock.schedule_interval(move, 1.1)
pyglet.app.run()
