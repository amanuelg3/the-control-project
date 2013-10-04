cd /etc/control
python2 RemoteServer.py &
beesu -c "python2 MouseControler.py & python2 GameControl.py & python2 MediaControler.py" ||  gksudo "python2 MouseControler.py & python2 GameControl.py & python2 MediaControler.py" ||  gksu "python2 MouseControler.py & python2 GameControl.py & python2 MediaControler.py"
