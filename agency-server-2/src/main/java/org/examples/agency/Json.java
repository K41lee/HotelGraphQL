package org.examples.agency;

import java.util.*;

// Mini JSON util très simple pour notre protocole ligne/ligne (pas de dépendances externes)
public class Json {
  public static String ok(Object data) {
    return "{\"status\":\"ok\",\"data\":" + toJson(data) + "}";
  }
  public static String error(String msg) {
    return "{\"status\":\"error\",\"error\":" + toJson(msg) + "}";
  }

  @SuppressWarnings("unchecked")
  public static Map<String,Object> minParse(String s) {
    // Parse minimal: attend un objet de niveau 1 avec paires clé/valeur simples (string, number, null, object)
    // Pour garder la réponse courte, on gère ici un parseur très réduit suffisant à nos messages.
    Map<String,Object> m = new LinkedHashMap<>();
    s = s.trim();
    if (!s.startsWith("{") || !s.endsWith("}")) throw new IllegalArgumentException("invalid json");
    s = s.substring(1, s.length()-1).trim();
    if (s.isEmpty()) return m;
    int i=0;
    while (i < s.length()) {
      if (s.charAt(i)!='\"') throw new IllegalArgumentException("invalid key");
      int j = s.indexOf('"', i+1); if (j<0) throw new IllegalArgumentException("invalid key end");
      String key = s.substring(i+1, j);
      i = j+1; while (i<s.length() && (s.charAt(i)==' '||s.charAt(i)==':')) { if(s.charAt(i)==':') {i++; break;} i++; }
      // value can be string, null, number, object
      if (s.charAt(i)=='\"') {
        int k = s.indexOf('"', i+1); if (k<0) throw new IllegalArgumentException("invalid string");
        String val = s.substring(i+1, k);
        m.put(key, val);
        i = k+1;
      } else if (s.startsWith("null", i)) {
        m.put(key, null); i += 4;
      } else if (s.charAt(i)=='{' ) {
        int brace=1; int k=i+1; while (k<s.length() && brace>0){ char c=s.charAt(k); if(c=='{') brace++; else if(c=='}') brace--; k++; }
        if (brace!=0) throw new IllegalArgumentException("invalid object");
        String sub = s.substring(i, k);
        m.put(key, minParse(sub));
        i = k;
      } else {
        // number (int)
        int k=i; while (k<s.length() && "-0123456789".indexOf(s.charAt(k))>=0) k++;
        String num = s.substring(i, k);
        m.put(key, Integer.parseInt(num));
        i = k;
      }
      while (i<s.length() && (s.charAt(i)==' '||s.charAt(i)==',')) { if(s.charAt(i)==','){ i++; break;} i++; }
    }
    return m;
  }

  @SuppressWarnings("unchecked")
  public static String toJson(Object o) {
    if (o==null) return "null";
    if (o instanceof String) return '"' + ((String)o).replace("\\","\\\\").replace("\"","\\\"") + '"';
    if (o instanceof Number || o instanceof Boolean) return String.valueOf(o);
    if (o instanceof Map) {
      StringBuilder b = new StringBuilder("{"); boolean first=true;
      for (Map.Entry<?,?> e : ((Map<?,?>)o).entrySet()) {
        if (!first) b.append(','); first=false;
        b.append(toJson(String.valueOf(e.getKey()))).append(':').append(toJson(e.getValue()));
      }
      return b.append('}').toString();
    }
    if (o instanceof Iterable) {
      StringBuilder b = new StringBuilder("["); boolean first=true;
      for (Object e : (Iterable<?>)o) { if (!first) b.append(','); first=false; b.append(toJson(e)); }
      return b.append(']').toString();
    }
    return toJson(String.valueOf(o));
  }
}

