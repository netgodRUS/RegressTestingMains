package org.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TLSClientAuthenticationStressTestA {

        public static void main(String[] args) throws Exception {
            int numThreads = 10000;
            int numRequestsPerThread = 10000;
            String url = "https://91.244.183.36:30012/";

            KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
            FileInputStream clientKeyStoreFile = new FileInputStream("C:\\certs\\Internship program Infotecs 2023.pfx");
            clientKeyStore.load(clientKeyStoreFile, "gf6i7S*G#*dlsi@&*".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(clientKeyStore, "gf6i7S*G#*dlsi@&*".toCharArray());

            InputStream is = new FileInputStream("C:\\certs\\ca3.cer.der");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate)cf.generateCertificate(is);

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null); // You don't need the KeyStore instance to come from a file.
            ks.setCertificateEntry("caCert", caCert);

            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);

//            CloseableHttpClient httpClient = HttpClients.custom()
//                    .setSslcontext(sslContext)
//                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSslcontext(sslContext)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setSocketTimeout(10000)
                            .setConnectTimeout(10000)
                            .build())
                    .build();

            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads * numRequestsPerThread);

            for (int i = 0; i < numThreads; i++) {
                executorService.submit(() -> {
                    for (int j = 0; j < numRequestsPerThread; j++) {
                        try {
                            HttpGet httpGet = new HttpGet(url);
                            HttpResponse response = httpClient.execute(httpGet);
                            int statusCode = response.getStatusLine().getStatusCode();
                            System.out.println("Thread " + Thread.currentThread().getId() +
                                    " completed " + numRequestsPerThread +
                                    " request " + j + " response status code: " + statusCode+ "  All threads completed." );
                        } catch (Exception e) {
                            System.out.println("Thread " + Thread.currentThread().getId() +
                                    " request " + j + " failed: " + e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }

            executorService.shutdown();
            latch.await();

            httpClient.close();
        }
    }

