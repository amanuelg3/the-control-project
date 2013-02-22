echo "Please type in your password (nothing will show as you type)."
su -c "yum -y install python pybluez python-uinput beesu
mv ControlData/control.desktop /usr/share/applications
mv ControlData /etc/control 
chmod +x /etc/control/start.sh
chmod +x /usr/share/applications/control.desktop"
echo "Control Installed"

