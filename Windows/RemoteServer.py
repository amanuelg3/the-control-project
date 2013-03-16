#This file is part of Control.

#Control is free software: you can redistribute it and/or modify
#it under the terms of the GNU General Public License as published by
#the Free Software Foundation, either version 3 of the License, or
#(at your option) any later version.

#Control is distributed in the hope that it will be useful,
#but WITHOUT ANY WARRANTY; without even the implied warranty of
#MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#GNU General Public License for more details.

#You should have received a copy of the GNU General Public License
#along with Control.  If not, see <http://www.gnu.org/licenses/>.

import bluetooth as bt
import threading, socket, select

localApps = []

class localThread(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)
		
	def run(self):
		nextPort = 3335
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.bind((socket.gethostbyname(socket.gethostname()),3334))
		s.listen(1)
		while True:
			(cs, addr) = s.accept()
			msg = cs.recv(4096).decode('ascii').strip()
			cs.send(str(nextPort))
			localApps.append((msg,nextPort))
			print(str((msg,nextPort)))
			nextPort+=1
	def stop(self):
		s.close()
        	self._stop.set()

thread = localThread()
thread.start()
#Start the blutooth
bs=bt.BluetoothSocket( bt.RFCOMM )
bs.bind(("",bt.PORT_ANY))
bs.listen(1)
port = bs.getsockname()[1]
uuid = "0003"
bt.advertise_service( bs, "ControlServerWindows", service_id = uuid, service_classes = [ uuid, bt.SERIAL_PORT_CLASS ], profiles = [ bt.SERIAL_PORT_PROFILE ])
print("Now listing")

while True: # Main server loop
	(cs, info) = bs.accept() #cs = cient socket
	print("Accepted connection from "+str(info)+" with the content:")
	msg = cs.recv(4096).decode('UTF-8').strip()
	try:
		if msg == "CL":
			msg = ""
			msg2 = ""
			for i in localApps:
				msg = msg + i[0] + "~"
				msg2 = msg2 + str(i[1]) + "~"
			cs.send((msg+"_"+msg2+"@").encode('UTF-8'))
			print(msg+"_"+msg2+"@")
			cs.close()
		elif msg[0:3] == "GL:":
			s = socket.socket(socket.AF_INET, 	socket.SOCK_STREAM)
			s.connect((socket.gethostbyname	(socket.gethostname()), int(msg[3:])))
	
			for i in range(3):
				msg = s.recv(4096).decode	('UTF-8').strip()
				print("@"+msg+"@")
				cs.send("@"+msg+"@")
				msg = cs.recv(4096).decode	('UTF-8').strip()
				print(msg)
				s.send(msg)		
				
			while True:
				read, write, err = select.select([s,cs],[],[],60)
				if cs in read:
					msg = cs.recv(4096).decode('UTF-8').strip()
					s.send(msg)
				if s in read:
					msg = s.recv(4096).decode('UTF-8').strip()
					if not msg == "":
						cs.send("@"+msg+"@")
					
	except IOError:
		print "IOE: Stopping Client"
	cs.close()
	try:
		s.close()
	except NameError:
		pass

bs.close() #Close the socket
thread.stop()
