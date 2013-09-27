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

import socket, win32api, win32con, thread

def keyPress(key,msg):
        if msg[5:6] == "1":
                win32api.keybd_event(key,0,0,0)
        else:
               win32api.keybd_event(key,0,win32con.KEYEVENTF_KEYUP,0) 

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("Media")
msg = inform.recv(4096).decode('ascii').strip()
print "|"+msg+"|  <-- Port No."
inform.close()


def connection(cs, this_is_only_here_for_no_reason):
	try:
                SO = True
                while not msg == "QUITCONTROLLER":
                        try:
				if msg == "SENDLAYOUT":
					cs.send("1")
                			msg = cs.recv(4096).decode('ascii').strip()
                			cs.send("1_0_1_3_-1_-1_1~7_1_0_10_Play:_-1_-2_2~1_1_0_0_-1_-2_1~7_2_0_11_Prev:_-2_-2_1~7_2_0_5_Next:_-2_-2_1~1_1_0_0_-1_-2_2~7_3_0_19_VolD:_-2_-2_1~7_3_0_17_Stop:_-2_-2_1~7_3_0_20_VolU:_-2_-2_1")
                			msg = cs.recv(4096).decode('ascii').strip()
               				cs.send("000000")
                			msg = cs.recv(4096).decode('ascii').strip()
                                if msg[:5] == "Play:":
                                        keyPress(win32con.VK_MEDIA_PLAY_PAUSE,msg)
                                if msg[:5] == "Prev:":
                                        keyPress(win32con.VK_MEDIA_PREV_TRACK,msg)
                                if msg[:5] == "Next:":
                                	keyPress(win32con.VK_MEDIA_NEXT_TRACK,msg)
                                if msg[:5] == "VolD:":
                        		keyPress(win32con.VK_VOLUME_DOWN,msg)
                        	if msg[:5] == "VolU:":
                        		keyPress(win32con.VK_VOLUME_UP,msg)
                        	if msg[:5] == "Stop:":
                        		keyPress(178,msg)
                        	msg = cs.recv(4096).decode('ascii').strip()
                        except ValueError:
                        	msg = cs.recv(4096).decode('ascii').strip()
                        	pass
        except IOError:
                cs.close()
	cs.close()

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
s.listen(1)
while True:
        (cs, addr) = s.accept()
	thread.start_new_thread(connection,(cs,0))

s.close()
