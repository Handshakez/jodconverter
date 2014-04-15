# MyDocket PDF Server
# ==================

Original code forked from [JODConverte](https://github.com/mirkonasato/jodconverter).

Goal: convert MS Office docs into thumbnails and full pdfs.


## Table Of Contents:

* Installation
* Architecture
* Protocol


## Installation
## ============

This project requires a java JRE, open office, imagemagick, and the hyperic "sigar" .dll.  Building it also requires maven and a JDK.

This portion of the document assumes that you have already checked this project out locally into `$SERVERHOME`

### Get java

Java 1.6+ is required.  If just runnning, a JRE is find.  If building, a JDK is necessary.  If you are installing one go ahead and get the JDK.

*MacOs* has a JDK already installed (I hope!)
*Ubuntu* `apt-get install default-jdk`

You can determine if you have a jdk with the commands `java` and `javac`.  The former will be in both a JRE and a JDK.  The latter is only available in the JDK.o

### Get Open Office

Download OpenOffice 4+ from http://www.openoffice.org/download/other.html .

*MacOs* 
 - Open the dmg, copy OpenOffice into your Applications directory.  
 - Update the value `office.home` under `$SERVERHOME/pdfserver/src/test/resources/test.properties` to `/Applications/OpenOffice.app/Contents/`

*Ubuntu*
Difficulty: you may already have LibreOffice installed. Or you may already have an old version of OpenOffice installed.

http://askubuntu.com/questions/116590/how-do-i-install-openoffice-org-instead-of-libreoffice
http://www.liberiangeek.net/2013/08/apache-openoffice-4-0-releasedheres-how-to-install-it-in-ubuntu/
 - First `soffice -version`.  If this says 'not found', you need to install. If it says 'OpenOffice 4', you are good.  If you see anything else you will need to uninstall.
 - If purging, start with `apt-get remove --purge libreoffice-core`.  The program `aptitude` may be helpful.
 - Download the .deb version of the the office installer to /tmp
```
$ cd /tmp
$ tar xzf <the tarball>
$ cd <the tar directory>
$ cd en-US/DEBS
$ sudo dpkg -i *deb
```
 - if `soffice -version` is still not telling you anything, you may have to link it by hand `sudo ln -s /opt/openoffice4/bin/soffice /usr/local/bin/soffice`.
 - Update the value `office.home` under `$SERVERHOME/pdfserver/src/test/resources/test.properties` to the approriate value.

### Install Hyperic SIGAR
 - download the hyperic binaries http://sourceforge.net/projects/sigar/files/latest/download?source=files
 - unzip the binaries into /tmp
 - cd `/tmp/hyperic-sigar-1.6.4/sigar-bin`
 - *MacOs* Copy `libsigar-universal64-macosx.dylib` to the Java library path, e.g`/usr/lib/java`
 - *Ubuntu* Copy `libsigar-amd64-linux.so` to `/usr/lib`
 - *Both* Probably need to adjust permissions: `chmod 555 <the shared lib>`

### Install regular old dependencies
*MacOs* `brew install imagemagick gs libtool`
*Ubuntu* `apt-get install imagemagick`

If building, also install maven and ant either via `brew` or `apt-get`

## Building

There are three projects in here.  jod-core and pdfserver are the ones we care about.  Either due to Maven's stupidness or my ignorance, the projects build independently of each other.  The poorly named script `both` in the root directory will build all three projects.

Pro tip: to get (most of)  the sources for the jars, so you can hook them up in your IDE, run `mvn dependency:sources`.  This will then copy the jars into your ~/.m2 directory (e.g. `~/.m2/repository/io/netty//netty-all//4.0.17.Final/` now has both `netty-all-4.0.17.Final.jar` and `netty-all-4.0.17.Final-sources.jar`.

## Architecture

An open office server is spun up in the background.  A 'netty' server is also spun up and listens on a port for incoming traffic.

The port takes a remote request to transform a file.  Once the file comes in ove r the write, the server attempts to
- import the file into open office
- export a single page pdf of the file
- convert the single page pdf into a png thumbnail (via imagemagick)
- export the entire file into a larger pdf
- return both the thumbnail and the full pdf back to the client


## Protocol

The protocol consists of two types of messages. Some of them are new line (chr(13)) delimited strings.  Others are arrays of bytes specified by a size.

Client sends:
PUSH\n
<file size in bytes as a string>\n
<file contents>

Server responds either with failure:
1\n
<rest of connection is error message>

Or with successs:
0\n
<thumbnail size in bytes are string>\n
<thumbnail data>
0\n
<full file size in bytes are string>\n
<full file data>

NB that the procedure may fail after the thumbnail.  In which case the client will received a "1" and a string containing the error message.

## Special Server Side Install Considerations

(work in progress as of 4/xi157/2014)

### Binding to port 80
Under Unix, ports underneath 1024 are restricted.  They can only be bound by users with root privs. However, our AWS config only has a couple of ports open to the outside world, and these are all in the restricted areas.  Some solutions to this are

- run the server as root - security riddled
- start the server as root, then drop privs to another user - requires application support (which I have not written)
- proxy requests via nginx - requires that the requests be (GASP) http, which I have avoided
- forward requests coming in on port 80 to a different port via `iptables` - lets any user listen in on the unrestricted port

I have chosen the latter.  The `iptables` configuration is taken from http://serverfault.com/questions/112795/how-can-i-run-a-server-on-linux-on-port-80-as-a-normal-user.  It is not obvious.

```
# iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 7001
# iptables -t nat -I OUTPUT -p tcp -d 127.0.0.1 --dport 80 -j REDIRECT --to-ports 7001
```

You can list it all out under

```
# iptables -t nat --line-numbers -n -L
```

If you want to delete them, use the line numbers and table names obtained from the previous command.  To delete line 2 from the PREROUTING table, do:
```
# iptables -t nat -D PREROUTING 2
```

### Starting on system boot

- Build the server code via maven as described above.  Then create the run time directory:

```
$ cd $SERVERHOME/pdfserver
$ ant deploy
```

This will copy the jar files into `/home/ubuntu/shank/lib`.

- install jsvc

```
$ sudo apt-get install jsvc
```

- Create the `init.d` script link and set to run on start

```
$ sudo ln -s $SERVERHOME/src/main/unix/init.d/shank /etc/init.d/shank
$ sudo update-rc.d shank defaults
```

- Update `$javahome` in `/etc/init.d/shank` to reflect your JDK install.
- The service will try to run as `www-data`.  Update the script if you desire to run as a different user.
- Start the service and cat the output files to ensure all was well
```
$ sudo service shank start
$ sudo cat /var/log/shank/*
```
