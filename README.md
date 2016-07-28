#_JAMAL_ Remote Access System

##General info

That application is Java based remote access system that provides you with both server side and client side, it is based on [ubicomp_jvnc_server](https://github.com/capickett/ubicomp_jvnc_server) for our server side and [TightVNC](http://www.tightvnc.com/) for the client side.  Both applications were merged together with one interface and modified to fit our needs. _JAMAL_ doesn't conform to VNC standard and does not follow [Remote Framebuffer Protocol](https://en.wikipedia.org/wiki/RFB_protocol). 
To support any key combination (which can produce any character) on server side, client sends keycodes of the keyboard instead. This enables multilingual text input on server side and switching between different keyboard layouts.

JAMAL can be used as a simple executable .jar file, its main purpose is simplicity, only requirement on client and server machines is Java runtime. Generally _JAMAL_ is meant to be used in local networks. It can use windows and GNU/Linux operating systems as both server and client.

##Development

Project is developed in Eclipse, and its source code is naturally compatible with this environment.

##Changes for remote access server

1. New graphical interface has been provided to setup server side application.

2. The way how server interpret keys is changed. Now it expects [key code](https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html) rather than Unicode character, that distances our project from VNC standard and makes it partially incompatible with third party VNC clients.

3. Graphical feedback provided for user, regarding current server settings, its status and information on current connections.

4. Restart server option added, that allows client to stop and start server with new settings remotely.

5. Server now can exchange copied text from _JAML_ client.

6. Now it is possible to add optional server password. Everyone connecting to server, will have to provide valid password.

7. DES encryption in authentication phase changed to AES encryption.

##Changes for remote access client

1. Client now sends to server pressed key code not Unicode character for every character, therefore can’t be used with third party VNC servers.

2. DES encryption in authentication phase is changed to AES encryption.

3. Option added to toggle _ctrl+alt_ combination with keyboard by double clicking control button.

4. Minimizing client window or going away from it properly releases control buttons (if pushed) on server side.

##Known issues

1. On GNU/Linux in some programs and browsers (Firefox), pop-up and drop down menus may appear as black rectangles on client screen.

2. Frame rate is bearable but still leaves room for improvement. To solve that issue implementation of optimized implementation, dependent on operating system is needed. (Probably hardware support from Oracle FX. 

3. Mouse input currently doesn't support multiple buttons pressed at the same time.
