package org.example;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

public class StressTestA {

        public static void main(String[] args) throws Exception {
            // Load client certificate
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream keyStoreStream = new FileInputStream("/home/dexter/Serts/Internship program Infotecs 2023.pfx");
            keyStore.load(keyStoreStream, "gf6i7S*G#*dlsi@&*".toCharArray());

            // Load server CA certificate
            KeyStore trustStore = KeyStore.getInstance("JKS");
            FileInputStream trustStoreStream = new FileInputStream("/home/dexter/Serts/Trust");
            trustStore.load(trustStoreStream, "gf6i7S*G#*dlsi@&*".toCharArray());

            // Create key manager and trust manager factories
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "gf6i7S*G#*dlsi@&*".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Create SSL context with client certificate and server CA certificate
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Create HttpClient with custom SSL context
            HttpClient client = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://91.244.183.36:30012/files"))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:110.0) Gecko/20100101 Firefox/110.0")
                    .build();

            int numRequests = 100;

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < numRequests; i++) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Request " + i + " status code: " + response.statusCode());
                TimeUnit.MILLISECONDS.sleep(10);
            }
            long endTime = System.currentTimeMillis();

            System.out.println("Total time: " + (endTime - startTime) + " ms");
            System.out.println("Requests per second: " + ((double) numRequests / (double) (endTime - startTime)) * 1000);
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("/home/dexter/results.txt")), true));

        }
        }




