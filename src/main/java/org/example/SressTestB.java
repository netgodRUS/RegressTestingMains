package org.example;

import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

public class SressTestB {

        public static void main(String[] args) throws Exception {
            // Load client certificate and private key
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            char[] clientPassword = "gf6i7S*G#*dlsi@&*".toCharArray();
            FileInputStream clientStream = new FileInputStream("/home/dexter/Serts/Internship program Infotecs 2023.pfx");
            clientStore.load(clientStream, clientPassword);

            // Load server CA certificate
            KeyStore serverStore = KeyStore.getInstance("JKS");
            char[] serverPassword = "gf6i7S*G#*dlsi@&*".toCharArray();
            FileInputStream serverStream = new FileInputStream("/home/dexter/Serts/Trust");
            serverStore.load(serverStream, serverPassword);

            // Set up SSL context with client and server certificates
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(clientStore, clientPassword)
                    .loadTrustMaterial(serverStore, null)
                    .build();

            // Create HTTP client with SSL context
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .sslContext(sslContext)
                    .build();

            // Set up HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://91.244.183.36:30012/files"))
                    .header("User-Agent", "Java HttpClient")
                    .GET()
                    .build();

            // Send HTTP requests in a loop
            int numRequests = 1000;
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < numRequests; i++) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Request " + i + " status code: " + response.statusCode());
                TimeUnit.MILLISECONDS.sleep(10);
            }

            long endTime = System.currentTimeMillis();

            System.out.println("Total time: " + (endTime - startTime) + " ms");
            System.out.println("Requests per second: " + ((double) numRequests / (double) (endTime - startTime)) * 1000);
        }
    }


