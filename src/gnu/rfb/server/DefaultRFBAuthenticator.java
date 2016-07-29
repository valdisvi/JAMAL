package gnu.rfb.server;

import gnu.rfb.rfb;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import ubicomp.rfb.server.RFBAuthenticator;

/**
 * Free-access RFB authentication models.
 * 
 * Modified by Cameron Pickett 2012
 **/

public class DefaultRFBAuthenticator implements RFBAuthenticator {

    //
    // Construction
    //

    private static void addInetAddresses(Set<InetAddress> set, String string) {
        if (string == null)
            return;

        InetAddress[] addresses;
        for (StringTokenizer t = new StringTokenizer(string, ","); t
                .hasMoreElements();) {
            try {
                addresses = InetAddress.getAllByName(t.nextToken());
                for (int i = 0; i < addresses.length; i++) {
                    set.add(addresses[i]);
                }
            } catch (UnknownHostException x) {
            }
        }
    }

    protected String password;

    //
    // RFBAuthenticator
    //

    private boolean restrict;

    private boolean v38;

    ///////////////////////////////////////////////////////////////////////////////////////
    // Private

    private Set<InetAddress> restrictedTo = new HashSet<InetAddress>();
    private Set<InetAddress> noPasswordFor = new HashSet<InetAddress>();

    public DefaultRFBAuthenticator() {
        this(null, null, null, false);
    }

    public DefaultRFBAuthenticator(String password, String restrictedTo,
            String noPasswordFor) {
        this(password, restrictedTo, noPasswordFor, false);
    }

    public DefaultRFBAuthenticator(String password, String restrictedTo,
            String noPasswordFor, boolean v38) {
        restrict = (restrictedTo != null && restrictedTo.length() > 0);
        addInetAddresses(this.restrictedTo, restrictedTo);
        addInetAddresses(this.noPasswordFor, noPasswordFor);
        this.password = password;
        this.v38 = v38;
    }

    @Override
    public boolean authenticate(RFBClient client) throws IOException {
        System.out.println(
                "Authentification started");
        if (isRestricted(client)) {
            System.out.println("CLient blocked");
            client.writeConnectionFailed("Your address is blocked");
            return false;
        }
        if (password != null && password.length() > 0
                && isChallengeRequired(client)) {
            return challenge(client);
        } else {
            noChallenge(client);
            return true;
        }
    }

    private boolean challenge(RFBClient client) throws IOException {
        if (v38) {
            // Server requires authorization
            client.writeByte(1);
            client.writeByte(rfb.VncAuth);

            // Discard client reply

        } else {
            client.writeInt(rfb.VncAuth);
        }

        // Write 16 byte challenge
        byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
                (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };//
        byte[] challenge = new byte[16];
        new Random().nextBytes(challenge);
        client.writeBytes(challenge);
        client.flush();

        client.writeBytes(salt);//
        client.flush();//


        byte[] pre = new byte[1];
        client.read(pre);

        // Read 16 byte response
        byte[] response = new byte[32];
        client.read(response);

        Cipher AESencryption;//
        byte[] expectedResult = null;
        try {
            byte[] IV = new byte[16];

            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                    256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            AESencryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
            AESencryption.init(Cipher.ENCRYPT_MODE, secret,
                    new IvParameterSpec(IV));

            expectedResult = AESencryption.doFinal(challenge);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Compare ciphers

        if (expectedResult != null && Arrays.equals(expectedResult, response)) {
            client.writeSecurityResult(true, "");
            return true;
        } else {
            client.writeSecurityResult(false, "Password Incorrect!");
            return false;
        }
    }

    protected boolean isChallengeRequired(RFBClient client) {
        return !noPasswordFor.contains(client.getInetAddress());
    }

    protected boolean isRestricted(RFBClient client) {
        if (restrict)
            return !restrictedTo.contains(client.getInetAddress());
        else
            return false;
    }

    private void noChallenge(RFBClient client) throws IOException {
        if (v38) {
            client.writeByte(1);
            client.writeByte(rfb.NoAuth);
            client.flush();

            byte[] response = new byte[1];
            client.read(response);

            client.writeInt(rfb.VncAuthOK);
            client.flush();
        } else {
            client.writeInt(rfb.NoAuth);
            client.flush();
        }
    }

    @Override
    public void setVersion(boolean v38) {
        this.v38 = v38;
    }
}
