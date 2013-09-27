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

time.sleep(2)

events = (u.REL_X,u.REL_Y,u.BTN_LEFT,u.BTN_RIGHT,u.REL_WHEEL)
d = u.Device(events)

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("Mouse")
msg = inform.recv(4096).decode('ascii').strip()
print "|"+msg+"|  <-- Port No."
inform.close()

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
s.listen(1)
def connection(cs, this_is_only_here_to_make_it_a_truple):
	msg = cs.recv(4096).decode('ascii').strip()	
	while not msg == "QUITCONTROLLER":
		try:
			#print(msg)
			if msg == "SENDLAYOUT":
				cs.send("1")
				msg = cs.recv(4096).decode('ascii').strip()
				cs.send("0_0_\n\n\nLeft\n\n\n_L:_-2_-2_1~1_0_1_3_-2_-2_0.1~0_1_\n\n _U:_-1_-2_0~0_1_\n\n _D:_-1_-2_0~0_0_\n\n\nRight\n\n\n_R:_-2_-2_1")
				msg = cs.recv(4096).decode('ascii').strip()
				cs.send("110000000")
				msg = cs.recv(4096).decode('ascii').strip()
			if "Acc:" in msg:
				print(msg)
				l = msg.split(":")
				d.emit(u.REL_X,int(-(float(l[1]))*2))
				d.emit(u.REL_Y,int((float(l[2]))*2))
			if msg[:2] == "L:":
				d.emit(u.BTN_LEFT,int(msg[2:3]))
			if msg[:2] == "R:":
				d.emit(u.BTN_RIGHT,int(msg[2:3]))
			if msg[:2] == "U:":
				d.emit(u.REL_WHEEL,int(msg[2:3]))
			if msg[:2] == "D:":
				d.emit(u.REL_WHEEL,-(int(msg[2:3])))
			if msg[:2] == "S:":
				su = int(msg[2:3]) == 1
			msg = cs.recv(4096).decode('ascii').strip()
		except ValueError:
			msg = cs.recv(4096).decode('ascii').strip()
			pass	
	cs.close()

while True:
	(cs, addr) = s.accept()
	thread.start_new_thread(connection,(cs,0))
s.close()
