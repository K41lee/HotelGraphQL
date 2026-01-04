package org.examples.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AgencyTcpClient implements Closeable {
  private final String host;
  private final int port;
  private final String agencyName; // Nom de l'agence pour identification
  private Socket s;
  private BufferedReader in;
  private BufferedWriter out;

  public AgencyTcpClient(String host, int port, String agencyName) throws IOException {
    this.host = host;
    this.port = port;
    this.agencyName = agencyName;
    connect();
  }

  public String getAgencyName() {
    return agencyName;
  }

  public int getPort() {
    return port;
  }
  private void connect() throws IOException {
    closeQuiet();
    s = new Socket(host, port);
    s.setSoTimeout(15000);
    in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
    out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
  }
  private String callOnce(String json) throws IOException {
    out.write(json); out.write("\n"); out.flush();
    return in.readLine();
  }
  private String call(String json) throws IOException {
    try {
      String line = callOnce(json);
      if (line == null) { // serveur a fermé: tenter une reconnexion
        connect();
        return callOnce(json);
      }
      return line;
    } catch (IOException e) {
      // Broken pipe / reset: reconnect and retry once
      connect();
      return callOnce(json);
    }
  }
  public String getCatalog() throws IOException {
    return call("{\"op\":\"catalog.get\"}");
  }
  public String search(String ville, String a, String d, int nb, String agencyId) throws IOException {
    String payload = String.format(java.util.Locale.ROOT,
        "{\"ville\":\"%s\",\"arrivee\":\"%s\",\"depart\":\"%s\",\"nbPersonnes\":%d,\"agencyId\":\"%s\"}",
        escape(ville), a, d, nb, agencyId==null?"":escape(agencyId));
    String req = "{\"op\":\"offers.search\",\"payload\":" + payload + "}";
    return call(req);
  }
  public String reserve(String hotelCode, String offerId, String agencyId, String nom, String prenom, String carte, String arrivee, String depart) throws IOException {
    String payload = String.format(java.util.Locale.ROOT,
        "{\"hotelCode\":\"%s\",\"offerId\":\"%s\",\"agencyId\":\"%s\",\"nom\":\"%s\",\"prenom\":\"%s\",\"carte\":\"%s\",\"arrivee\":\"%s\",\"depart\":\"%s\"}",
        escape(hotelCode), escape(offerId), escape(agencyId), escape(nom), escape(prenom), escape(carte), escape(arrivee), escape(depart));
    String req = "{\"op\":\"reservation.make\",\"payload\":" + payload + "}";
    return call(req);
  }
  private static String escape(String s){ return s==null?"":s.replace("\\","\\\\").replace("\"","\\\""); }
  private void closeQuiet(){ try { if (s!=null) s.close(); } catch (Exception ignore) {} }
  @Override public void close() throws IOException { closeQuiet(); }
}
