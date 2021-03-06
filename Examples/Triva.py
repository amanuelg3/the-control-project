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

import socket, time, thread, select
q = [["What is the capital of Australia?","Sydney","Canberra","Melborne",2],["2+2","2","4","6",2]]
qn = 0
ans = []
npn = 1

def connection(cs,pn):
	global q, qn, ans
	try:
		lqn = qn
		msg = cs.recv(4096).decode('ascii').strip()
		while True:
			read, write, err = select.select([cs],[],[],1)
			if cs in read:
				try:
					msg = cs.recv(4096).decode('ascii').strip()
					if msg == "SENDLAYOUT":
						cs.send("1")
						msg = cs.recv(4096).decode('ascii').strip()
						cs.send("1_0_1_3_-1_-1_1~0_1_"+q[qn][1]+"_A1:_-2_-2_0.2~0_1_"+q[qn][2]+"_A2:_-2_-2_0.2~0_1_"+q[qn][3]+"_A3:_-2_-2_0.2~0_1_You Are Player "+pn+"_PNB:_-2_-2_0.2")
						msg = cs.recv(4096).decode('ascii').strip()
						cs.send("000000")
						msg = cs.recv(4096).decode('ascii').strip()
						lqn = qn
					if msg[:3] == "A1:":
						answer[qn][pn] = 1
					if msg[:3] == "A2:":
						answer[qn][pn] = 2
					if msg[:3] == "A3:":
						answer[qn][pn] = 3
				except ValueError:
					pass
			if not lqn == qn:
				lqn = qn
				cs.send("-1")
	except IOError:
		print("IOE")	
	cs.close()
def nextQuestion():
	global q, qn
	while True:
		print("Question #"+str(qn+1)+" :"+q[qn][0])
		for i in range(20):
			time.sleep(1)
			print(str(i-20)+" seconds to go!")
		print("Times up: The correct answer is "+q[qn][q[qn][4]])
		for i in range(1,npn-1):
			if answer[qn][i] == q[qn][4]:
				print "Player " + str(i) + " got the right answer!"
		qn+=1
		time.sleep(2)

inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
inform.connect((socket.gethostbyname(socket.gethostname()),3334))
inform.send("Triva")
msg = inform.recv(4096).decode('ascii').strip()
inform.close()

thread.start_new_thread(nextQuestion,())

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostbyname(socket.gethostname()),int(msg)))
s.listen(1)
while True:
	(cs, addr) = s.accept()
	thread.start_new_thread(connection, (cs,npn))
	npn+=1
s.close()
