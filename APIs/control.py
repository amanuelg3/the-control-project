#This is python3 only (Sorry!)
#We do aim to port it to 2.x!

import socket
import _thread

ENCODING = 'ascii'

class Controller_Instance():
	def __init__(self,socket,parent,id):
		self.cs = socket
		self.parent = parent
		self.layout = self.parent.layout
		self.id = id
	def run(self):
		try:
			msgs = self.cs.recv(4096).decode('ascii').strip().split('\n')
			while msgs:
				msg = msgs.pop(0)
				msgList = msg.split(':')
				if msg == "SENDLAYOUT":
					self.cs.send(bytes(str(self.layout.orientation),ENCODING))
					msg = self.cs.recv(4096).decode('ascii').strip()
					self.cs.send(bytes(str(self.layout),ENCODING))
					msg = self.cs.recv(4096).decode('ascii').strip()
					self.cs.send(bytes(str(self.layout.sensors),ENCODING))
					msg = self.cs.recv(4096).decode('ascii').strip()
				elif msgList[0] in {"Acc", "Gyro", "LinAcc"}:
					raise ValueError("Sensors not implimented")
				else:
					f = self.parent.layout.getFunc(msgList[0])
					f(self,msgList[1:])
				msgs+= self.cs.recv(4096).decode('ascii').strip().split('\n')
		except OSError:
			return
	def changeLayout(self,newLayout):
		self.layout = newLayout
		self.cs.send(bytes("-1",ENCODING))
	def close(self):
		self.cs.close()
			
class Controller():
	def __init__(self,name,layout):
		self.layout = layout
		inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		inform.connect((socket.gethostbyname(socket.gethostname()),3334))		
		inform.send(bytes(name,ENCODING))
		self.port = int(inform.recv(4096).decode('ascii').strip())
		inform.close()
		self.connections = []
	def close(self):
		self.s.close()
		for i in self.connections:
			i.close()
	def start(self):
		self.connections = []
		_thread.start_new_thread(self._start,(0,0))
	def _start(self,zero,zeroAgain):
		id = 0
		try:
			self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.s.bind((socket.gethostbyname(socket.gethostname()),self.port))
			self.s.listen(1)
			while True:
				(cs, socketAddress) = self.s.accept()
				_thread.start_new_thread(self._startControllerInstance,(cs,id))
				id+=1
		except OSError:
			return
	def _startControllerInstance(self,socket,id):
		x = Controller_Instance(socket,self,id)
		self.connections.append(x)
		x.run()

class Button():
	def __init__ (self, root, content, func, width=-2, height=-2, weight=1):
		self.parentId = 0
		self.content = content
		self.tag = 'Bnt'+str(root.next())+':'
		root.fd[self.tag[:-1]] = func		
		self.func = func
		self.width = width
		self.height = height
		self.weight = weight
	def __str__(self):
		return '0_{}_{}_{}_{}_{}_{}'.format(self.parentId, self.content, self.tag, self.width, self.height, self.weight)

BUTTON_ON = 1
BUTTON_OFF = 0

class Toggle_Button():
	def __init__ (self, root, contentOn, contentOff, startToggle, func, width=-2, height=-2, weight=1):
		self.parentId = 0
		self.startToggle = startToggle
		self.contentOn = contentOn
		self.contentOff = contentOff
		self.tag = 'TBnt'+str(root.next())+':'
		root.fd[self.tag[:-1]] = func		
		self.func = func
		self.width = width
		self.height = height
		self.weight = weight
	def __str__(self):
		return '3_{}_{}_{}_{}_{}_{}_{}_{}'.format(self.parentId, self.startToggle, self.contentOn, self.contentOff , self.tag, self.width, self.height, self.weight)

LABEL_BOLD = 1
LABEL_NORMAL = 0

class Label(): #8_parent_text_size_1=Bold_w_h_w
	def __init__ (self, root, content, size, bold, width=-2, height=-2, weight=1):
		self.parentId = 0
		self.size = size
		self.content = content
		self.bold = bold
		self.width = width
		self.height = height
		self.weight = weight
	def __str__(self):
		return '8_{}_{}_{}_{}_{}_{}_{}'.format(self.parentId, self.content, self.size, self.bold, self.width, self.height, self.weight)

class Seek_Bar():
	def __init__ (self, root, startPos, func, width=-1, height=-2, weight=1):
		self.parentId = 0
		self.startPos = startPos #Out of 1000
		self.tag = 'Bnt'+str(root.next())+':'
		root.fd[self.tag[:-1]] = func		
		self.func = func
		self.width = width
		self.height = height
		self.weight = weight
	def __str__(self):
		return '2_{}_{}_{}_{}_{}_{}'.format(self.parentId, self.startPos, self.tag, self.width, self.height, self.weight)

class Analog_Stick():
	def __init__ (self, root, func, width=-1, height=-1, weight=1):
		self.parentId = 0
		self.tag = 'Bnt'+str(root.next())+':'
		root.fd[self.tag[:-1]] = func		
		self.func = func
		self.width = width
		self.height = height
		self.weight = weight
	def __str__(self):
		return '4_{}_{}_{}_{}_{}'.format(self.parentId, self.tag, self.width, self.height, self.weight)

BOX_ACROSS = 0
BOX_VERTICAL = 1

class Box():
	def __init__(self, root, elementDirection, gravity, width=-1, height=-1, weight=1):
		self.id = root.next_box()
		self.parentId = 0
		self.elementDirection = elementDirection
		self.gravity = gravity
		self.width = width
		self.height = height
		self.weight = weight
		self.kids = []
	def add(self, x):
		x.parentId = self.id
		self.kids.append(x)
	def __str__(self):
		return '1_{}_{}_{}_{}_{}_{}~'.format(self.parentId,self.elementDirection,self.gravity,self.width,self.height,self.weight)+'~'.join([str(x) for x in self.kids])

SENSOR_ACC_X = 0
SENSOR_ACC_Y = 1
SENSOR_ACC_Z = 2
SENSOR_GYRO_X = 3
SENSOR_GYRO_Y = 4
SENSOR_GYRO_Z = 5
SENSOR_MOTION_X = 6
SENSOR_MOTION_Y = 7
SENSOR_MOTION_Z = 8

class Sensors():
	def __init__(self,sl):
		self.sensorsList = sl
	def __str__(self):
			string = list("0"*9)
			for i in self.sensorsList:
				string[i] = "1"
			return "".join(string)

ORIENTATION_PORTRAIT = 1
ORIENTATION_LANDSCAPE = 0

class Layout():
	def __init__(self,sensors,oreintation):
		self.sensors = sensors
		self.orientation = oreintation
		self.kids = []
		self.fd = {}
		self._next = 0
		self._next_box = 1
	def add(self, x):
		self.kids.append(x)
	def getFunc(self,tag):
		if tag in self.fd:
			return self.fd[tag]
		else:
			raise ValueError("The tag {} is not found in this Layout".format(tag))
	def next(self):
		n = self._next
		self._next+=1
		return n
	def next_box(self):
		n = self._next_box
		if (n == 32):
			raise ValueError("Too many boxes. Limit = 31")
		self._next_box+=1
		return n
	def __str__(self):
		return '~'.join([str(x) for x in self.kids])
