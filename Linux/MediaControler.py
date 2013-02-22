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

import socket
import uinput as u

events = (u.KEY_PLAYPAUSE,u.KEY_PREVIOUS,u.KEY_NEXT,u.KEY_FASTFORWARD,u.KEY_REWIND,u.KEY_STOP,u.KEY_VOLUMEUP,u.KEY_VOLUMEDOWN)
d = u.Device(events)

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("Media")
msg = inform.recv(4096).decode('ascii').strip()
print "|"+msg+"|  <-- Port No."
inform.close()

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
s.listen(1)
while True:
	SO = True
	(cs, addr) = s.accept()
	cs.send("1")
	msg = cs.recv(4096).decode('ascii').strip()
	cs.send("1_0_1_3_-1_-1_1~7_1_0_10_Play:_-1_-2_2~1_1_0_0_-1_-2_1~7_2_0_11_Prev:_-2_-2_1~7_2_0_15_Rewd:_-2_-2_1~7_2_0_2_Ffor:_-2_-2_1~7_2_0_5_Next:_-2_-2_1~1_1_0_0_-1_-2_2~7_3_0_19_VolD:_-2_-2_1~7_3_0_17_Stop:_-2_-2_1~7_3_0_20_VolU:_-2_-2_1")
	msg = cs.recv(4096).decode('ascii').strip()
	cs.send("000000")
	msg = cs.recv(4096).decode('ascii').strip()
	while not msg == "":
		try:
			print msg
			if msg[:5] == "Play:":
				d.emit(u.KEY_PLAYPAUSE,int(msg[5:6]))
			if msg[:5] == "Prev:":
				d.emit(u.KEY_PREVIOUS,int(msg[5:6]))
			if msg[:5] == "Next:":
				d.emit(u.KEY_NEXT,int(msg[5:6]))
			if msg[:5] == "Rewd:":
				d.emit(u.KEY_REWIND,int(msg[5:6]))
			if msg[:5] == "Ffor:":
				d.emit(u.KEY_FASTFORWARD,int(msg[5:6]))
			if msg[:5] == "VolD:":
				d.emit(u.KEY_VOLUMEDOWN,int(msg[5:6]))
			if msg[:5] == "VolU:":
				d.emit(u.KEY_VOLUMEUP,int(msg[5:6]))
			if msg[:5] == "Stop:":
				d.emit(u.KEY_STOP,int(msg[5:6]))
			msg = cs.recv(4096).decode('ascii').strip()
		except ValueError:
			msg = cs.recv(4096).decode('ascii').strip()
			pass	
	cs.close()

s.close()
