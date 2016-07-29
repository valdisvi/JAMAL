package tests;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gnu.rfb.PixelFormat;
import gnu.rfb.server.DefaultRFBAuthenticator;
import gnu.rfb.server.RFBHost;
import gnu.rfb.server.RFBSocket;
import gnu.vnc.awt.VNCRobot;

import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Ignore;
import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestServerInOut{

    
    static RFBSocket client;
    static VNCRobot server;
    static RFBHost host;
    static ByteArrayInputStream byteArrayInputStream;
    static ByteArrayOutputStream byteArrayOutputStream;
    private static int width =100;
    private static int height =200;
    private static String desktopName = "displayName";
    private boolean down=false;
    
    @BeforeClass
    public static void setUpBeforeClass()  throws Exception {
        

       
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
        
        client.addServer(server);
                
       


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
    public void A_testWriteServerCutText() throws IOException {

        String testText = "Example";
        client.writeServerCutText(testText);
        ByteBuffer buffer = ByteBuffer.allocate(8+testText.length());;

        buffer.put((byte) 3);
        buffer.put((byte) 0);
        buffer.putShort((short) 0);
        buffer.putInt(testText.length());
        buffer.put(testText.getBytes());
        assertArrayEquals(buffer.array(),byteArrayOutputStream.toByteArray());
    }


    @Test
    public void B_testWriteConnectionFailed() throws IOException{
        
        String testText = "Example";
        client.writeConnectionFailed(testText);
        ByteBuffer buffer = ByteBuffer.allocate(5+testText.length());;

        buffer.put((byte) 0);
        buffer.putInt(testText.length());
        buffer.put(testText.getBytes());
        assertArrayEquals(buffer.array(),byteArrayOutputStream.toByteArray());
        
    }
    
    @Test
    public void C_testReadClientCutText() throws Exception{
       
        client.readInput();
        String data = (String) Toolkit.getDefaultToolkit()
                .getSystemClipboard().getData(DataFlavor.stringFlavor); 
        assertEquals(data,"trala la la lalala");
    }
    
    @Test
    public void D_testReadClientKeyEvent() throws Exception{
     
        
        client.readInput();

        client.readInput();
        //TODO check if key is pressed and released
    }

    

    @Test
    public void E_testReadMouseEvent() throws Exception{
       
        for(int i=0;i<3;i++){
        client.readInput();
        Thread.sleep(50);
        Point p =MouseInfo.getPointerInfo().getLocation();
        assertEquals(new Point(0,0),p);
        

        client.readInput();
        Thread.sleep(50);
        p =MouseInfo.getPointerInfo().getLocation();
        assertEquals(new Point(66,44),p);
        }
        
    }
    
    
 
    private static byte[] inputBytes(){
        String cuttext = "trala la la lalala";


        ByteBuffer buffer = ByteBuffer.allocate(cuttext.length()+8+8+8+12+24);

        //Cuttext
        buffer.put((byte) 6);
        buffer.put((byte) 0);
        buffer.putShort((short) 0);
        buffer.putInt(cuttext.length());
        buffer.put(cuttext.getBytes());
        
        //KeyEvent press
        buffer.put((byte) 4);
        buffer.put((byte)1);
        buffer.putShort((short) 0);
        buffer.putInt(KeyEvent.VK_CONTROL);
        //KeyEvent release
        buffer.put((byte) 4);
        buffer.put((byte)0);
        buffer.putShort((short) 0);
        buffer.putInt(KeyEvent.VK_CONTROL);
        //MouseEvent
        buffer.put((byte) 5);
        buffer.put((byte)1);
        buffer.putShort((short) 0);
        buffer.putShort((short) 0);
        //MouseEvent
        buffer.put((byte) 5);
        buffer.put((byte)0);
        buffer.putShort((short) 66);
        buffer.putShort((short) 44);
        //MouseEvent
        buffer.put((byte) 5);
        buffer.put((byte)2);
        buffer.putShort((short) 0);
        buffer.putShort((short) 0);
        //MouseEvent
        buffer.put((byte) 5);
        buffer.put((byte)0);
        buffer.putShort((short) 66);
        buffer.putShort((short) 44);
        //MouseEvent
        buffer.put((byte) 5);
        buffer.put((byte)4);
        buffer.putShort((short) 0);
        buffer.putShort((short) 0);
        //MouseEvent
        buffer.put((byte) 5);
        buffer.put((byte)0);
        buffer.putShort((short) 66);
        buffer.putShort((short) 44);
        
        return buffer.array();
    }






    
}
