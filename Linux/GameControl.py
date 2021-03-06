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

import socket, time, thread
import uinput as u

time.sleep(1)

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("Gaming")
msg = inform.recv(4096).decode('ascii').strip()
print "|"+msg+"|  <-- Port No."
inform.close()

def connection(cs, z):
	SO = True
	Ix = 1
	events = (u.ABS_X,u.ABS_Y,u.BTN_1,u.BTN_2,u.BTN_3,u.BTN_4,u.BTN_5,u.BTN_6)
	d = u.Device(events)
	msg = cs.recv(4096).decode('ascii').strip()
	while not msg == "QUITCONTROLLER":
		try:
			if msg == "SENDLAYOUT":
				#msg = cs.recv(4096).decode('ascii').strip()
				cs.send("0")
				msg = cs.recv(4096).decode('ascii').strip()
				cs.send("1_0_1_3_-2_-1_1~3_1_1_Sensor On_Sensor Off_S:_-2_-2_0~3_1_1_Motion Inverted_Invert Motion_I:_-2_-2_0~4_1_DPad:_-1_-1_1~1_0_0_5_-2_-1_1.5~1_2_1_17_-1_-1_1~1_2_1_17_-1_-1_1~0_4_1_BTN1:_-1_-1_1~0_4_2_BTN2:_-1_-1_1~0_4_3_BTN3:_-1_-1_1~0_3_4_BTN4:_-1_-1_1~0_3_5_BTN5:_-1_-1_1~0_3_6_BTN6:_-1_-1_1")
				msg = cs.recv(4096).decode('ascii').strip()
				cs.send("110000")
			if msg[:3] == "BTN":
				d.emit(events[int(msg[3:4])+1],int(msg.split(":")[1]))
			if msg[:5] == "DPad:":
				l = msg.split(":")
				d.emit(u.ABS_X,int(l[1])*50*Ix)
				d.emit(u.ABS_Y,int(l[2])*50*Ix)
			if msg[:4] == "Acc:" and SO == True:
				l = msg.split(":")
				print int(float(l[1])*10000)
				d.emit(u.ABS_X,int(float(l[1])*100)*Ix)
				d.emit(u.ABS_Y,int(float(l[2])*100)*Ix)
			if msg[:2] == "S:":
				SO = msg[2:] == "1"
				d.emit(u.ABS_X,0)
				d.emit(u.ABS_Y,0)
			if msg[:2] == "I:":
				I = msg[2:] == "0"
				if I:
					Ix = -1
				else:
					Ix = 1
			msg = cs.recv(4096).decode('ascii').strip()
		except ValueError:
			msg = cs.recv(4096).decode('ascii').strip()
			pass 
	cs.close()
	del d

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
s.listen(1)
while True:
	(cs, addr) = s.accept()
	thread.start_new_thread(connection, (cs, 0))
s.close()
