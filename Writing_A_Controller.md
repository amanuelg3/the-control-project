This is a tutorial for making a controller in python. This can be adapted for other programming languages because it is all just a socket based program.

> ## 1. Contact the control server and get your port number: ##

```
import socket, thread
inform = socket.socket(socket.AF_INET, socket.SOCK_STREAM) #Start a new normal socket
inform.connect((socket.gethostbyname(socket.gethostname()),3334)) #Connect to the control server
inform.send("This txet will show up in the list of controller") #Tell the server your controllers name
msg = inform.recv(4096).decode('ascii').strip() #get back the id of the port to bind your server to
inform.close() #say this is the end of part one
```

Nothing that interesting: just connecting to localhost:3334 and setting up the connection.

> ## 2. Start your controllers server and wait for a connection ##

```
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) #create a new socket
s.bind((socket.gethostbyname(socket.gethostname()),int(msg))) #bind it to localhost : your port number
s.listen(1) #start waiting for connections
while True:
  (cs, addr) = s.accept() #accept the new connection
  thread.start_new_thread(connection, (cs, 0)) #start a new thread with the socket
```

Use the port number assigned above and  wait for connections. The sockets are connected to the phone through the Control server. One thing to note is that if you use an `@` sign in your message the Control app will cut your message short. **Do not use the `@` sign in any messages.**

> ## 3. Send the layout information ##

```
def connection(cs, z):
  msg = cs.recv(4096).decode('ascii').strip() #get the first message
  try: #to stop crashes
    if msg == "SENDLAYOUT": #When the layout is requested send...
      cs.send("1") #Orientation: 1 for portrait and 0 for landscape
      msg = cs.recv(4096).decode('ascii').strip() #wait until you get the message that control has finished dealing with the orientation
      cs.send("0_0_Press Me_b:_-2_-2_1") #send the layout code
      msg = cs.recv(4096).decode('ascii').strip() #wait again
      cs.send("110000") #send the information about the sensors
      #continued below
```

For more info about the layout stuff have a look at sensors and layout\_code .

> ## 4. Deal with the user interaction ##

```
  #continuing in the connection def
  msg = cs.recv(4096).decode('ascii').strip() #get the first message
  while not msg == "QUITCONTROLLER": #while   Control is connected
    try: #to stop crashes
      if msg[:2] == "b:": #if the user presses the button with the b: tag
        print "You pressed the button!"
      msg = cs.recv(4096).decode('ascii').strip() #get the message
    except ValueError: #if the message gets mixed around
      msg = cs.recv(4096).decode('ascii').strip() #get the message
      pass
```

Control sends back interaction in a **TAG** VALUE format so remember that control does not add separating characters. For more on the way the data is sent back have a look at the layout\_code page.

> ## 5. Ending the communication with control ##

At the end of the communication def

```
  cs.close()
```

At the end of the program

```
s.close()
```

**That's it: your first controller**

Form here there are lots of things to look at. Control is capable of multiple controllers; all that you need to do is multithread the socket system of your app. Also there are many more view components!