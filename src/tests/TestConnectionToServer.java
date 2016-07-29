package tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.nio.ByteBuffer;

import gnu.rfb.PixelFormat;
import gnu.rfb.server.DefaultRFBAuthenticator;
import gnu.rfb.server.RFBHost;
import gnu.rfb.server.RFBSocket;
import gnu.vnc.awt.VNCRobot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ubicomp.rfb.server.RFBAuthenticator;

import static org.mockito.Mockito.*;


public class TestConnectionToServer {

    
    static RFBSocket client;
    static VNCRobot server;
    static RFBHost host;
    static ByteArrayInputStream byteArrayInputStream;
    static ByteArrayOutputStream byteArrayOutputStream;
    private static int width =100;
    private static int height =200;
    private static String desktopName = "displayName";
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        

       
        final Socket socket = mock(Socket.class);
        
        byteArrayOutputStream = new ByteArrayOutputStream();
        
        
        when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);
        

        byteArrayInputStream = new ByteArrayInputStream(inputBytes());
        when(socket.getInputStream()).thenReturn(byteArrayInputStream);
        //when(byteArrayInputStream.read()).thenReturn(null);
        

        
        server = new VNCRobot(0, desktopName,  width,height);
        Class<VNCRobot> serverClass = VNCRobot.class;
        try {
            host = new RFBHost(0,desktopName , serverClass, width,
                    height, new DefaultRFBAuthenticator(null,
                            null, null));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        host.setSharedServer(server);

        client =new RFBSocket (socket,null,null,host,new DefaultRFBAuthenticator(null,null, null));
        
                
      


    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        //client.stop();
    }

    @Before
    public void setUp() throws Exception {
        byteArrayOutputStream.reset();
    }

    @After
    public void tearDown() throws Exception {
    }

   
   
    @Test 
    public void testSocketConnection() throws IOException{
       
        client.setup();
       // client.close();

        assertArrayEquals(outputBytes(),byteArrayOutputStream.toByteArray());


    }
    
    private static byte[] inputBytes(){
        String protocol = "RFB 003.008\n";
        byte resp = 0;
        byte init = 1;
        ByteBuffer buffer = ByteBuffer.allocate(protocol.length()+2);
        buffer.put(protocol.getBytes());
        buffer.put(resp);
        buffer.put(init);
        
        
        return buffer.array();
    }
    private static byte[] outputBytes(){
        String protocol = "RFB 003.008\n";
        byte auth = 1;
        int authOK = 0;
        
        ByteBuffer buffer = ByteBuffer.allocate(protocol.length()+desktopName.length()+30);
        buffer.put(protocol.getBytes());
        buffer.put((byte)1);
        buffer.put(auth);
        buffer.putInt(authOK);
   
        buffer.putShort((short) server.getFrameBufferWidth(client));
        buffer.putShort((short) server.getFrameBufferHeight(client));
        buffer.put(PixelFormat.RGB888.toByteArray());
        buffer.put((byte)0);
        buffer.putShort((short) 0);
        buffer.putInt( desktopName.length());
        buffer.put(desktopName.getBytes());
        
        return buffer.array();
    }
}
