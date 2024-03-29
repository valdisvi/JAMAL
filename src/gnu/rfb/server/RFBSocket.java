package gnu.rfb.server;

import gnu.rfb.Colour;
import gnu.rfb.PixelFormat;
import gnu.rfb.Rect;
import gnu.rfb.rfb;
import gnu.vnc.GUI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ubicomp.rfb.server.RFBAuthenticator;



/**
 * Standard RFB client model using a simple {@link java.net.Socket}.
 **/

public class RFBSocket implements RFBClient, Runnable {

    //
    // Construction
    //

    private Socket socket;

    private static List<Socket> socketList = new ArrayList<Socket>();

    //
    // RFBClient
    //

    // Attributes

    private Thread cilpboardlistener;

    private boolean isLocal;

    private Constructor<?> constructor;

    private Object[] constructorArgs;

    private RFBHost host;

    private RFBAuthenticator authenticator;

    private RFBServer server = null;

    private DataInputStream input;

    private boolean mRunning;

    private boolean v38;

    // Messages from server to client

    private DataOutputStream output;

    private PixelFormat pixelFormat = null;

    private String protocolVersionMsg = "";

    private boolean shared = false;

    private int[] encodings = new int[0];

    // Operations

    private int preferredEncoding = rfb.EncodingHextile;

    boolean authComplete = false;
    private String name = " ";

    public RFBSocket(Socket socket, Constructor<?> constructor,
            Object[] constructorArgs, RFBHost host,
            RFBAuthenticator authenticator) throws IOException {
        this(socket, constructor, constructorArgs, host, authenticator, false);
    }

    public RFBSocket(Socket socket, Constructor<?> constructor,
            Object[] constructorArgs, RFBHost host,
            RFBAuthenticator authenticator, boolean v38) throws IOException {
       // System.out.println("Creating new socket for : " + socket.getInetAddress().getHostAddress());
        this.socket = socket;
        this.constructor = constructor;
        this.constructorArgs = constructorArgs;
        this.host = host;
        this.authenticator = authenticator;


        try{
        isLocal = socket.getLocalAddress().equals(socket.getInetAddress());
        name =  socket.getInetAddress().getHostAddress();
        }
        catch(Exception e){
            isLocal=false;
        }
        // Streams
        input = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(
                new BufferedOutputStream(socket.getOutputStream(), 16384));

        this.v38 = v38;

        socketList.add(socket);
        



    }
    
    public void setup(){
        if (!GUI.getGui().isEmpty()) {
            GUI.getGui().get(0).setText(socketList);
        }

        // Start socket listener thread
        new Thread(this,
                "RFBSocket-" + name)
                        .start();

        while(!authComplete)
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        
        //thread that listens to clipborad changes
        if(mRunning){
        cilpboardlistener= new Thread(new CilpboardListener(this));
        cilpboardlistener.start();
        }
        
    }

    @Override
    public synchronized void close() throws IOException {
        System.out.println("Closing client socket for : " + name);
        socketList.remove(socket);
        socket.close(); 
        stop();
        if (!GUI.getGui().isEmpty()) {
            GUI.getGui().get(0).setText(socketList);
        }
    }

    @Override
    public synchronized void flush() throws IOException {
        output.flush();
    }

    @Override
    public synchronized int[] getEncodings() {
        return encodings;
    }

    //
    // Runnable
    //

    @Override
    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Private

    @Override
    public synchronized PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    @Override
    public synchronized int getPreferredEncoding() {
        return preferredEncoding;
    }

    @Override
    public synchronized String getProtocolVersionMsg() {
        return protocolVersionMsg;
    }

    @Override
    public synchronized boolean getShared() {
        return shared;
    }

    public static List<Socket> getSocketList() {
        return socketList;
    }

    private void initServer() throws IOException {
        // We may already have a shared server
        if (shared)
            server = host.getSharedServer();

        if (server == null) {
            try {
                server = (RFBServer) constructor.newInstance(constructorArgs);
            } catch (InstantiationException x) {
                x.printStackTrace();
            } catch (InvocationTargetException x) {
                x.printStackTrace();
            } catch (IllegalAccessException x) {
                x.printStackTrace();
            }

            // Set shared server
            if (shared) {
                if (server.allowShared())
                    host.setSharedServer(server);
                else
                    shared = false;
            }
        }

        server.addClient(this);
        server.setClientProtocolVersionMsg(this, protocolVersionMsg);
        server.setShared(this, shared);
    }

    /**
     * 
     * 
     */
    @Override
    public synchronized void read(byte bytes[]) throws IOException {
        input.readFully(bytes);
    }

    private synchronized void readClientCutText() throws IOException {
        input.readUnsignedByte(); // padding
        input.readUnsignedShort(); // padding
        int length = input.readInt();
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        String text = new String(bytes);

        // Delegate to server
        server.clientCutText(this, text);
    }

    private synchronized void readClientInit() throws IOException {
        shared = input.readUnsignedByte() == 1;
    }

    private synchronized void readFixColourMapEntries() throws IOException {
        input.readUnsignedByte(); // padding
        int firstColour = input.readUnsignedShort();
        int nColours = input.readUnsignedShort();
        Colour colourMap[] = new Colour[nColours];
        for (int i = 0; i < nColours; i++)
            colourMap[i].readData(input);

        // Delegate to server
        server.fixColourMapEntries(this, firstColour, colourMap);
    }

    private synchronized void readFrameBufferUpdateRequest()
            throws IOException {
        boolean incremental = (input.readUnsignedByte() == 1);
        int x = input.readUnsignedShort();
        int y = input.readUnsignedShort();
        int w = input.readUnsignedShort();
        int h = input.readUnsignedShort();

        // Delegate to server
        server.frameBufferUpdateRequest(this, incremental, x, y, w, h);
    }

    private synchronized void readKeyEvent() throws IOException {
        boolean down = (input.readUnsignedByte() == 1);

        input.readUnsignedShort(); // padding
        int key = input.readInt();

        // Delegate to server
        server.keyEvent(this, down, key);
    }

    private synchronized void readPointerEvent() throws IOException {
        int buttonMask = input.readUnsignedByte();
        int x = input.readUnsignedShort();
        int y = input.readUnsignedShort();

        // Delegate to server
        server.pointerEvent(this, buttonMask, x, y);
    }

    private synchronized void readProtocolVersionMsg() throws IOException {
        byte[] b = new byte[12];
        input.readFully(b);
        protocolVersionMsg = new String(b);
        v38 = protocolVersionMsg.equals(rfb.ProtocolVersionMsgv38);
    }

    private synchronized void readSetEncodings() throws IOException {
        input.readUnsignedByte(); // padding
        int nEncodings = input.readUnsignedShort();
        encodings = new int[nEncodings];
        for (int i = 0; i < nEncodings; i++)
            encodings[i] = input.readInt();

        preferredEncoding = Rect.bestEncoding(encodings);

        // Delegate to server
        server.setEncodings(this, encodings);
    }

    private synchronized void readSetPixelFormat() throws IOException {
        input.readUnsignedByte(); // padding
        input.readUnsignedShort(); // padding
        pixelFormat = new PixelFormat(input);
        input.readUnsignedByte(); // padding
        input.readUnsignedShort(); // padding

        // Delegate to server
        server.setPixelFormat(this, pixelFormat);
    }

    // Handshaking

    @Override
    public void run() {
        try {
            System.out.println("Started client socket thread");
            // Handshaking
            writeProtocolVersionMsg();

            readProtocolVersionMsg();
            authenticator.setVersion(v38);

            if (!authenticator.authenticate(this)){

                mRunning = false;
                authComplete = true;
                close();
                stop();
            }
            else{

                readClientInit();
                initServer();
                writeServerInit();
                authComplete = true;
                mRunning = true;
            }
                //

            
            
            while (mRunning) {
                int b = input.readUnsignedByte();
                switch (b) {
                case rfb.SetPixelFormat:
                    readSetPixelFormat();
                    break;
                case rfb.FixColourMapEntries:
                    readFixColourMapEntries();
                    break;
                case rfb.SetEncodings:
                    readSetEncodings();
                    break;
                case rfb.FrameBufferUpdateRequest:
                    readFrameBufferUpdateRequest();
                    if (isLocal)
                        // We add a small delay for local connections, because viewers are sometimes
                        // "too fast" and end up spending all their CPU cycles on socket communication,
                        // the result being that the user interface gets extremely sluggish.
                        Thread.sleep(50); // TODO Try changing this
                    break;
                case rfb.KeyEvent:
                    readKeyEvent();
                    break;
                case rfb.PointerEvent:
                    readPointerEvent();
                    break;
                case rfb.ClientCutText:
                    readClientCutText();
                    break;
                default:
                    System.err.println(b);
                }
            }
        } catch (Throwable x) {
        } finally {
            if (server != null)
                server.removeClient(this);

            try {
                close();
            } catch (IOException x) {
            }
        }
    }

    @Override
    public synchronized void setPreferredEncoding(int encoding) {
        if (encodings.length > 0) {
            for (int i = 0; i < encodings.length; i++) {
                if (encoding == encodings[i]) {
                    // Encoding is supported
                    preferredEncoding = encoding;
                    return;
                }
            }
        } else {
            // No list
            preferredEncoding = encoding;
        }
    }

    @Override
    public synchronized void writeInt(int integer) throws IOException {
        output.writeInt(integer);
    }

    @Override
    public synchronized void writeByte(int integer) throws IOException {
        output.writeByte(integer);
    }

    @Override
    public synchronized void writeBytes(byte bytes[]) throws IOException {
        output.write(bytes);
    }

    // Messages from server to client

    @Override
    public synchronized void writeBell() throws IOException {
        writeServerMessageType(rfb.Bell);
    }

    // Messages from client to server

    @Override
    public synchronized void writeConnectionFailed(String text)
            throws IOException {
        if (v38)
            output.writeInt(rfb.ConnFailed);
        else
            output.writeByte(rfb.ConnFailed);
        output.writeInt(text.length());
        output.writeBytes(text);
        output.flush();
    }

    @Override
    public synchronized void writeSecurityResult(boolean ok, String text)
            throws IOException {
        if (ok) {
            output.writeInt(rfb.VncAuthOK);
        } else {
            output.writeInt(rfb.VncAuthFailed);
            if (v38) {
                output.writeInt(text.length());
                output.writeBytes(text);
            }
        }
        output.flush();
    }

    @Override
    public synchronized void writeFrameBufferUpdate(Rect rects[])
            throws IOException {
        writeServerMessageType(rfb.FrameBufferUpdate);
        output.writeByte(0); // padding

        // Count rects
        int count = 0;
        int i;
        for (i = 0; i < rects.length; i++)
            count += rects[i].count;
        output.writeShort(count);

        for (i = 0; i < rects.length; i++)
            rects[i].writeData(output);

        output.flush();
    }

    private synchronized void writeProtocolVersionMsg() throws IOException {
        output.writeBytes(rfb.ProtocolVersionMsg);
        output.flush();
    }

    @Override
    public synchronized void writeServerCutText(String text)
            throws IOException {
        //Copy clipboard from server to client//MYCODE

        writeServerMessageType(rfb.ServerCutText);
        output.writeByte(0); // padding
        output.writeShort(0); // padding
        output.writeInt(text.length());
        output.writeBytes(text);
        //  output.writeByte(0);
        output.flush();
    }

    private synchronized void writeServerInit() throws IOException {
        output.writeShort(server.getFrameBufferWidth(this));
        output.writeShort(server.getFrameBufferHeight(this));
        server.getPreferredPixelFormat(this).writeData(output);
        output.writeByte(0); // padding
        output.writeByte(0); // padding
        output.writeByte(0); // padding
        String desktopName = server.getDesktopName(this);
        output.writeInt(desktopName.length());
        output.writeBytes(desktopName);
        output.flush();
    }

    private synchronized void writeServerMessageType(int type)
            throws IOException {
        output.writeByte(type);
    }

    @Override
    public synchronized void writeSetColourMapEntries(int firstColour,
            Colour colours[]) throws IOException {
        writeServerMessageType(rfb.SetColourMapEntries);
        output.writeByte(0); // padding
        output.writeShort(firstColour);
        output.writeShort(colours.length);
        for (int i = 0; i < colours.length; i++) {
            output.writeShort(colours[i].r);
            output.writeShort(colours[i].g);
            output.writeShort(colours[i].b);
        }
        output.flush();
    }

    @SuppressWarnings("deprecation")
    public synchronized void stop() {
        mRunning = false;
        authComplete=true;
        try{
        cilpboardlistener.stop();
        }
        catch(Exception e){
            
        }
    }
    
    
    public void readInput() throws IOException{
        int b = input.readUnsignedByte();
        switch (b) {
        case rfb.SetPixelFormat:
            readSetPixelFormat();
            break;
        case rfb.FixColourMapEntries:
            readFixColourMapEntries();
            break;
        case rfb.SetEncodings:
            readSetEncodings();
            break;
        case rfb.FrameBufferUpdateRequest:
            readFrameBufferUpdateRequest();
            break;
        case rfb.KeyEvent:
            readKeyEvent();
            break;
        case rfb.PointerEvent:
            readPointerEvent();
            break;
        case rfb.ClientCutText:
            readClientCutText();
            break;
        default:
            System.err.println(b);
        }
    }
    public void addServer(RFBServer server){
        this.server=server;
    }
    
}
