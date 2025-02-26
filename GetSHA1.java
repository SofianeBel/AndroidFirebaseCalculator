import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class GetSHA1 {
    public static void main(String[] args) {
        try {
            String userHome = System.getProperty("user.home");
            System.out.println("User home directory: " + userHome);
            
            String keystorePath = userHome + "\\.android\\debug.keystore";
            System.out.println("Keystore path: " + keystorePath);
            
            File keystoreFile = new File(keystorePath);
            if (!keystoreFile.exists()) {
                System.out.println("Keystore file does not exist!");
                return;
            } else {
                System.out.println("Keystore file exists and is " + keystoreFile.length() + " bytes");
            }
            
            String keystorePassword = "android";
            String keyAlias = "androiddebugkey";
            
            System.out.println("Loading keystore...");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
            
            System.out.println("Checking if alias exists...");
            if (!keystore.containsAlias(keyAlias)) {
                System.out.println("Alias '" + keyAlias + "' not found in keystore!");
                System.out.println("Available aliases:");
                Enumeration<String> aliases = keystore.aliases();
                while (aliases.hasMoreElements()) {
                    System.out.println("- " + aliases.nextElement());
                }
                return;
            }
            
            System.out.println("Getting certificate...");
            Certificate cert = keystore.getCertificate(keyAlias);
            byte[] encoded = cert.getEncoded();
            
            System.out.println("Calculating SHA-1...");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(encoded);
            
            System.out.println("SHA-1 Fingerprint:");
            System.out.print("SHA1: ");
            for (int i = 0; i < digest.length; i++) {
                System.out.printf("%02X", digest[i]);
                if (i < digest.length - 1) {
                    System.out.print(":");
                }
            }
            System.out.println();
            
            // Also print SHA-256 for newer Firebase requirements
            System.out.println("Calculating SHA-256...");
            md = MessageDigest.getInstance("SHA-256");
            digest = md.digest(encoded);
            
            System.out.println("SHA-256 Fingerprint:");
            System.out.print("SHA256: ");
            for (int i = 0; i < digest.length; i++) {
                System.out.printf("%02X", digest[i]);
                if (i < digest.length - 1) {
                    System.out.print(":");
                }
            }
            System.out.println();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 