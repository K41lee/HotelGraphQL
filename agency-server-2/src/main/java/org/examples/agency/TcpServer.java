package org.examples.agency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@Component
public class TcpServer {
  private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

  @Value("${agency.tcp.port:7070}")
  private int port;

  private ServerSocket server;
  private ExecutorService pool;
  private volatile boolean running = true;

  @Autowired
  private AgencyService agencyService;


  public TcpServer() {
    // Constructor sans arguments pour Spring
  }

  @PostConstruct
  public void start() {
    try {
      server = new ServerSocket(port);
      pool = Executors.newCachedThreadPool();
      Thread acceptor = new Thread(this::acceptLoop, "agency-acceptor");
      // Important: ne pas mettre en daemon pour empêcher l'arrêt du processus Spring Boot
      acceptor.setDaemon(false);
      acceptor.start();

      log.info("[AGENCY-INIT] TCP server listening on port {} using gRPC backend", port);
    } catch (IOException e) {
      throw new RuntimeException("Failed to bind TCP port " + port, e);
    }
  }

  private void acceptLoop() {
    while (running) {
      try {
        Socket s = server.accept();
        s.setSoTimeout(15000);
        pool.submit(() -> handle(s));
      } catch (IOException e) {
        if (running) log.warn("[AGENCY] accept error: {}", e.toString());
      }
    }
  }

  private void handle(Socket s) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = in.readLine()) != null) {
        String resp;

        // Utiliser AgencyService (avec gRPC)
        if (agencyService != null) {
          resp = agencyService.handleRequest(line);
        } else {
          resp = "{\"status\":\"error\",\"message\":\"No service available\"}";
          log.error("[AGENCY] No service available");
        }

        out.write(resp);
        out.write("\n");
        out.flush();
      }
    } catch (IOException e) {
      log.debug("[AGENCY] client closed: {}", e.toString());
    } finally {
      try { s.close(); } catch (IOException ignore) {}
    }
  }

  @PreDestroy
  public void stop() {
    running = false;
    try { if (server != null) server.close(); } catch (IOException ignore) {}
    if (pool != null) pool.shutdownNow();
    log.info("[AGENCY-INIT] TCP server stopped");
  }
}
