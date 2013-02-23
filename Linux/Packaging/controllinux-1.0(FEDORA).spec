Summary: Control - an api that puts controllers on android devices - with gaming, media and mouse controllers

%define version 1.0

Group: Other

Name: Control

License: GPLv3

Release: 1

Source: controllinux-%{version}.tar.gz

URL: http://code.google.com/p/the-control-project/

Version: %{version}

Buildroot: /tmp/controlrpm
Buildarch: noarch

Requires: python-uinput
Requires: pybluez
Requires: beesu

%description

The Control Project brings an API to allow computer applications to generate controllers on the fly and see them on android devices. The Control client app is available for android on Google Play. The server app (this) includes 3 controllers: mouse, media and gaming. (NOTE: the included controllers require root to emulate devices)

Licenced under the GNU GPL v3

%prep

%setup

%build

rm -fr /tmp/controlrpm

mkdir /tmp/controlrpm

cp ./* /tmp/controlrpm

%install

mkdir $RPM_BUILD_ROOT/usr
mkdir $RPM_BUILD_ROOT/usr/share
mkdir $RPM_BUILD_ROOT/usr/share/applications

mv /tmp/controlrpm/control.desktop $RPM_BUILD_ROOT/usr/share/applications

mkdir $RPM_BUILD_ROOT/etc
mkdir $RPM_BUILD_ROOT/etc/control

mv /tmp/controlrpm/* $RPM_BUILD_ROOT/etc/control

%clean

rm -fr $RPM_BUILD_ROOT

%files

/usr/share/applications/control.desktop

/etc/control/*
