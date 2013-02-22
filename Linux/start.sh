cd /etc/control
python RemoteServer.py &
beesu -c "python MouseControler.py & python GameControl.py & python MediaControler.py" ||  gksudo "python MouseControler.py & python GameControl.py & python MediaControler.py" ||  gksu "python MouseControler.py & python GameControl.py & python MediaControler.py"
