package org.example;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.Certificate;
public class BandwidthChecker {
    public static void main(String[] args) throws Exception {

        // Load client keystore
        String clientKeystorePath = "/home/dexter/Serts/PFX";
        String clientKeystorePassword = "gf6i7S*G#*dlsi@&*";
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        try (InputStream clientKeystoreStream = new FileInputStream(clientKeystorePath)) {
            clientStore.load(clientKeystoreStream, clientKeystorePassword.toCharArray());
        }

        // Load server keystore
        String serverKeystorePath = "/home/dexter/Serts/Trust";
        String serverKeystorePassword = "gf6i7S*G#*dlsi@&*";
        KeyStore serverStore = KeyStore.getInstance("JKS");
        try (InputStream serverKeystoreStream = new FileInputStream(serverKeystorePath)) {
            serverStore.load(serverKeystoreStream, serverKeystorePassword.toCharArray());
        }

        // Create key manager factory for client keystore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, clientKeystorePassword.toCharArray());

        // Create trust manager factory for server keystore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(serverStore);

        // Set SSL context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // Set default SSLSocketFactory and HostnameVerifier
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        // Take away redirect loop
        HttpURLConnection.setFollowRedirects(false);
        // Make HTTPS request
        String urlStr = "https://91.244.183.36:30012/files";
        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

        conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.connect();

        // Get the server certificates
        Certificate[] certs = conn.getServerCertificates();
        for (Certificate cert : certs) {
            System.out.println(cert);
        }

        // Get the input stream and start reading data
        InputStream in = conn.getInputStream();
        byte[] buffer = new byte[1024];
        long startTime = System.currentTimeMillis();
        long totalBytes = 0;
        int bytesRead = 0;
        while ((bytesRead = in.read(buffer)) != -1) {
            totalBytes += bytesRead;
        }
        long endTime = System.currentTimeMillis();
        in.close();
        conn.disconnect();

        // Calculate the bandwidth
        long duration = endTime - startTime;
        double bytesPerSecond = (double)totalBytes / duration * 1000;
        double kilobitsPerSecond = bytesPerSecond * 8 / 1000;

        System.out.println("Total bytes read: " + totalBytes);
        System.out.println("Duration: " + duration + " ms");
        System.out.println("Bandwidth: " + kilobitsPerSecond + " Kbps");
    }
}
