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
import win32api
import win32con

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("Mouse")
msg = inform.recv(4096).decode('ascii').strip()
print "|"+msg+"|  <-- Port No."
inform.close()

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
s.listen(1)
while True:
        try:
                (cs, addr) = s.accept()
                cs.send("1")
                msg = cs.recv(4096).decode('ascii').strip()
                cs.send("0_0_\n\n\nLeft\n\n\n_L:_-2_-2_1~1_0_1_3_-2_-2_0.1~0_1_\n\n _U:_-1_-2_0~0_1_\n\n _D:_-1_-2_0~0_0_\n\n\nRight\n\n\n_R:_-2_-2_1")
                msg = cs.recv(4096).decode('ascii').strip()
                cs.send("110000")
                msg = cs.recv(4096).decode('ascii').strip()
                while not msg == "QUITCONTROLLER":
        		try:
                		if msg[:4] == "Acc:":
                        		l = msg.split(":")
                                	x, y = win32api.GetCursorPos()
                                        x+=int((-(float(l[1])))*1)
        				y+=int(float(l[2])*1)
                			win32api.SetCursorPos((x,y))
                		if msg[:2] == "L:":
        				x, y = win32api.GetCursorPos()
        				if msg[2:3] == "1":
                				win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN,x,y,0,0)
                        		if msg[2:3] == "0":
                                		win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP,x,y,0,0)
        			if msg[:2] == "R:":
                			x, y = win32api.GetCursorPos()
                        		if msg[2:3] == "1":
                                		win32api.mouse_event(win32con.MOUSEEVENTF_RIGHTDOWN,x,y,0,0)
                                        if msg[2:3] == "0":
                                                win32api.mouse_event(win32con.MOUSEEVENTF_RIGHTUP,x,y,0,0)
                        	if msg[:2] == "U:":
                                	x, y = win32api.GetCursorPos()
                                        win32api.mouse_event(win32con.MOUSEEVENTF_WHEEL,x,y,int(msg[2:3])*win32con.WHEEL_DELTA,0)
                                if msg[:2] == "D:":
                                        x, y = win32api.GetCursorPos()
                                        win32api.mouse_event(win32con.MOUSEEVENTF_WHEEL,x,y,-(int(msg[2:3])*win32con.WHEEL_DELTA),0)
                                msg = cs.recv(4096).decode('ascii').strip()
                        except ValueError:
                                msg = cs.recv(4096).decode('ascii').strip()
                                pass
        except IOError:
                cs.close()
        cs.close()

s.close()
