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

import socket, time, threading, select
q = [["What is the capital of Australia?","Sydney","Canberra","Melborne",2],["2+2","2","4","6",2]]
qn = 0
ans = []
npn = 1

class FuncThread(threading.Thread):
    def __init__(self, target, *args):
        self._target = target
        self._args = args
        threading.Thread.__init__(self)
 
    def run(self):
        self._target(*self._args)

def connection(cs,pn):
	global q, qn, ans
	try:
		lqn = qn
		lsqn = None
		msg = cs.recv(4096).decode('ascii').strip()
		while True:
			read, write, err = select.select([cs],[],[],1)
			if cs in read:
				try:
					msg = cs.recv(4096).decode('ascii').strip()
					if msg == "SENDLAYOUT":
						cs.send("1")
						msg = cs.recv(4096).decode('ascii').strip()
						cs.send("1_0_1_3_-1_-1_1~0_1_"+q[qn][1]+"_A1:_-1_-1_0~0_1_"+q[qn][2]+"_A2:_-1_-1_0~0_1_"+q[qn][3]+"_A3:_-1_-1_0")
						msg = cs.recv(4096).decode('ascii').strip()
						cs.send("000000")
						msg = cs.recv(4096).decode('ascii').strip()
						lqn = qn
					if msg[:3] == "A1:":
						if lsqn == lqn:
							answer.append({pn:1})
							lsqn = lqn
					if msg[:3] == "A2:":
						if lsqn == lqn:
							answer.append({pn:2})
							lsqn = lqn
					if msg[:3] == "A3:":
						if lsqn == lqn:
							answer.append({pn:3})
							lsqn = lqn
				
				except ValueError:
					pass
			if not lqn == qn:
				lqn = qn
				cs.send("RBL")
	except IOError:
		print("IOE")	
	cs.close()
def nextQuestion():
	global q, qn
	while True:
		print("Question #"+str(qn+1)+" :"+q[qn][0])
		for i in range(20):
			time.sleep(1)
			print(str(i)+" seconds to go!")
		print("Times up: The correct answer is "+q[qn][int(q[qn][4])])
		qn+=1
		time.sleep(2)

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("Triva")
msg = inform.recv(4096).decode('ascii').strip()
inform.close()

t = FuncThread(nextQuestion)
t.start()

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
s.listen(1)
while True:
	(cs, addr) = s.accept()
	t = FuncThread(connection, cs, npn)
	npn+=1
	t.start()
s.close()
