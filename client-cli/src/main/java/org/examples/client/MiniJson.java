package org.examples.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilitaire simple pour parser le JSON sans dépendance externe
 */
public class MiniJson {
    
    public static String getString(String json, String key) {
        String pat = "\"" + key + "\":\"";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        i += pat.length();
        int j = json.indexOf('"', i);
        if (j < 0) return null;
        return json.substring(i, j);
    }
    
    public static Integer getInt(String json, String key) {
        String pat = "\"" + key + "\":";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        i += pat.length();
        int j = i;
        while (j < json.length() && "0123456789".indexOf(json.charAt(j)) >= 0) j++;
        try {
            return Integer.parseInt(json.substring(i, j));
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Double getDouble(String json, String key) {
        String pat = "\"" + key + "\":";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        i += pat.length();
        int j = i;
        // Parser les nombres décimaux (inclure le point et les chiffres)
        while (j < json.length() && "0123456789.".indexOf(json.charAt(j)) >= 0) j++;
        try {
            return Double.parseDouble(json.substring(i, j));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrait un booléen JSON
     * Par exemple: "success":true → true
     */
    public static Boolean getBoolean(String json, String key) {
        String pat = "\"" + key + "\":";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        i += pat.length();
        // Sauter les espaces
        while (i < json.length() && json.charAt(i) == ' ') i++;
        // Chercher "true" ou "false"
        if (json.startsWith("true", i)) {
            return true;
        } else if (json.startsWith("false", i)) {
            return false;
        }
        return null;
    }

    /**
     * Extrait un objet JSON imbriqué
     * Par exemple: "room":{"numero":101,"nbLits":2}
     * Retourne: {"numero":101,"nbLits":2}
     */
    public static String getObject(String json, String key) {
        String pat = "\"" + key + "\":{";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        i += pat.length() - 1; // Garde le {

        int bracketDepth = 0;
        int objectStart = i;

        for (int pos = i; pos < json.length(); pos++) {
            char c = json.charAt(pos);
            if (c == '{') {
                bracketDepth++;
            } else if (c == '}') {
                bracketDepth--;
                if (bracketDepth == 0) {
                    return json.substring(objectStart, pos + 1);
                }
            }
        }
        return null;
    }

    public static String getArray(String json, String key) {
        String pat = "\"" + key + "\":[";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        i += pat.length();
        int j = json.indexOf(']', i);
        if (j < 0) return null;
        return json.substring(i, j);
    }
    
    public static List<String> getStringArray(String json, String key) {
        String arr = getArray(json, key);
        if (arr == null) return Collections.emptyList();

        // Si le tableau contient des objets (commence par '{'), utiliser getObjectArray
        if (arr.trim().startsWith("{")) {
            return getObjectArray(json, key);
        }

        // Sinon, parser comme des chaînes
        List<String> out = new ArrayList<>();
        int idx = 0;
        while (true) {
            int q1 = arr.indexOf('"', idx);
            if (q1 < 0) break;
            int q2 = arr.indexOf('"', q1 + 1);
            if (q2 < 0) break;
            out.add(arr.substring(q1 + 1, q2));
            idx = q2 + 1;
        }
        return out;
    }
    
    /**
     * Extrait un tableau d'objets JSON
     * Par exemple: "offers":[{...},{...}] retourne une liste de chaînes JSON
     */
    public static List<String> getObjectArray(String json, String key) {
        String pat = "\"" + key + "\":[";
        int i = json.indexOf(pat);
        if (i < 0) return Collections.emptyList();
        i += pat.length();

        List<String> objects = new ArrayList<>();
        int bracketDepth = 0;
        int objectStart = -1;

        for (int pos = i; pos < json.length(); pos++) {
            char c = json.charAt(pos);

            if (c == '{') {
                if (bracketDepth == 0) {
                    objectStart = pos;
                }
                bracketDepth++;
            } else if (c == '}') {
                bracketDepth--;
                if (bracketDepth == 0 && objectStart >= 0) {
                    objects.add(json.substring(objectStart, pos + 1));
                    objectStart = -1;
                }
            } else if (c == ']' && bracketDepth == 0) {
                break;
            }
        }

        return objects;
    }

    /**
     * Extrait un objet JSON d'un tableau par son index
     * Par exemple, si json contient "offers":[{...},{...}], 
     * cette méthode retourne l'objet à l'index donné
     */
    public static String getObjectFromArray(String json, String key, int index) {
        String pat = "\"" + key + "\":[";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        i += pat.length();
        
        // Parcourir les objets
        int objectCount = 0;
        int bracketDepth = 0;
        int objectStart = -1;
        
        for (int pos = i; pos < json.length(); pos++) {
            char c = json.charAt(pos);
            
            if (c == '{') {
                if (bracketDepth == 0) {
                    objectStart = pos;
                }
                bracketDepth++;
            } else if (c == '}') {
                bracketDepth--;
                if (bracketDepth == 0 && objectStart >= 0) {
                    if (objectCount == index) {
                        return json.substring(objectStart, pos + 1);
                    }
                    objectCount++;
                    objectStart = -1;
                }
            } else if (c == ']' && bracketDepth == 0) {
                break;
            }
        }
        
        return null;
    }
}

