#**_JAMAL_ Remote Access System**

##**General Info**

That application is Java based remote access system that provides you with both server side and client side, it was based on [ubicomp_jvnc_server](https://github.com/capickett/ubicomp_jvnc_server) for our server side and [TightVNC](http://www.tightvnc.com/) for the client side. Both applications were merged together with one interface and modified to fit our needs. _JAMAL_ doesn’t belong to VNC standard and does not follow Remote Framebuffer Protocol.

JAMAL can be deployed as a simple executable .jar file, its main purpose is simplicity, only thing required on host machine is JVM. Generally _JAMAL_ is meant to be used in local networks. It can use windows and GNU/Linux operating systems as both hosts and clients.

##**Development**

Project was delivered using Eclipse, and its source code is naturally compatible with this environment.

##**Changes for server side**

1. New interface has been provided to setup server side.

2. Changed the way how server interpret buttons, now he expects them as key code rather than Unicode, that distances our project from VNC standard and makes it partially incompatible with third party VNC clients.

3. Provided user feedback, regarding current server settings it status and information on current connections.

4. Added restart server options that allows client to stop and start server with new settings remotely.

5. Server now can exchange client cut text with _JAMAL_ client.

6. Now it is possible to add optional server password. Everyone connecting to will have to provide valid password

7. Changed DES encryption in authentication phase with AES encryption.

##**Changes for client side**

1. Client now sends server pressed key code not Unicode character for every character, therefore can’t be used with third party VNC servers.

2. Changed DES encryption in authentication phase with AES encryption.

3. Added option to toggle _ctrl+alt_ combination with keyboard by double clicking control button.

4. Now minimizing client window or going away from it properly releases buttons pushed on server side.

##**Issues**

1. On GNU/Linux in some programs and browsers (Firefox), pop-up and drop down menus may appear as black rectangles on client screen.

2. Frame rate is workable but still leaves room for improvement, most probably to resolve that issue we will need to implement system dependent solutions.

3. Mouse input currently doesn’t support multiple buttons pressed at the same time.
