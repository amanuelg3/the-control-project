import socket, time, thread
import uinput as u

time.sleep(1)

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("WSAD")
msg = inform.recv(4096).decode('ascii').strip()
print "|"+msg+"|  <-- Port No."
inform.close()

def connection(cs,z):
  SO = True
  cs.send("0")
  msg = cs.recv(4096).decode('ascii').strip()
  cs.send("1_0_0_77_-1_-1_1~0_1_A_L:_-1_-1_0.33~1_1_1_0_-1_-1_0.33~0_2_W_U:_-1_-1_0.5~0_2_S_D:_-1_-1_0.5~0_1_D_R:_-1_-1_0.33")
  msg = cs.recv(4096).decode('ascii').strip()
  cs.send("110000")
  msg = cs.recv(4096).decode('ascii').strip()
  events = (u.KEY_W,u.KEY_S,u.KEY_A,u.KEY_D)
  d = u.Device(events)
  while not msg == "QUITCONTROLLER":
  	try:
        	if msg[:2] == "L:":
                	d.emit(u.KEY_A,int(msg.split(":")[1]))
                if msg[:2] == "R:":
                        d.emit(u.KEY_D,int(msg.split(":")[1]))			
		if msg[:2] == "U:":
                        d.emit(u.KEY_W,int(msg.split(":")[1]))			
		if msg[:2] == "D:":
                        d.emit(u.KEY_S,int(msg.split(":")[1]))
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
  thread.start_new_thread(connection, (cs,0))
s.close()
