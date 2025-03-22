package All;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import All.CacheItem;

public class HttpCacheManager {

    // Classe interne pour encapsuler les éléments du cache

    public Map<String, CacheItem> cacheMap; // Cache principal pour stocker les réponses
    public long defaultExpirationTime; // Temps d'expiration en millisecondes

    // Constructeur avec un temps d'expiration par défaut
    public HttpCacheManager(long defaultExpirationTime) {
        this.cacheMap = new ConcurrentHashMap<>();
        this.defaultExpirationTime = defaultExpirationTime;
    }

    // Ajouter une réponse au cache
    public void put(String url, String response, String methodes) {
        cacheMap.put(url, new CacheItem(url, response, methodes));
        System.out.println("Add with success !");
        System.out.println(cacheMap.size());
    }

    // Récupérer un élément du cache selon le type de donnée souhaité (response, methodes, url)
    public String get(String url, String dataType) {
        CacheItem cacheItem = cacheMap.get(url);
        if (cacheItem != null) {
            if (isExpired(cacheItem)) {
                cacheMap.remove(url); // Supprimer si expiré
                return null;
            }
            switch (dataType.toLowerCase()) {
                case "response":
                    return cacheItem.getResponse();
                case "methodes":
                    return cacheItem.getMethodes();
                case "url":
                    return cacheItem.getUrl();
                default:
                    return null; // Type de donnée invalide
            }
        }
        return null; // Pas trouvé dans le cache
    }

    // Vérifier si un élément a expiré
    private boolean isExpired(CacheItem cacheItem) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - cacheItem.getTimestamp()) > defaultExpirationTime;
    }

    private long LastTimeExpired(CacheItem cacheItem) {
        long currentTime = System.currentTimeMillis();
        long tempRestant = cacheItem.getTimestamp() - currentTime;
        return Math.abs(tempRestant / 1000);
    }

    // Supprimer une réponse spécifique
    public void remove(String url) {
        cacheMap.remove(url);
    }

    // Vider le cache
    public void clear() {
        cacheMap.clear();
    }

    // Obtenir la taille du cache
    public int size() {
        return cacheMap.size();
    }

    // Méthode pour convertir un timestamp en date et heure sous forme de chaîne
    public String convertTimestmp(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public String listeValueInCache() {
        StringBuilder cacheState = new StringBuilder();
        cacheState.append("Etat actuel du cache:\n");
        cacheState.append("---------------------------------\n");
        int count = 1;
        for (CacheItem TableValue : cacheMap.values()) {
            if (isExpired(TableValue)) {
                cacheMap.remove(TableValue.getUrl()); // Supprimer si expiré
                return null;
            }
            cacheState.append(" Cache num: " + count + "\n");
            cacheState.append("URL: ").append(TableValue.getUrl()).append("\n");
            cacheState.append("Method Used:").append(TableValue.getMethodes()).append("\n");
            cacheState.append("Response:").append(TableValue.getResponse()).append("\n");
            cacheState.append("Creation Date:").append(convertTimestmp((TableValue.getTimestamp()))).append("\n");
            cacheState.append("Time lasted before expire:").append((LastTimeExpired(TableValue)))
                    .append(" sur " + defaultExpirationTime / 1000).append(" seconde \n");
            cacheState.append("---------------------------------\n");
            count++;
        }
        return cacheState.toString();
    }

    // Exemple d'utilisation
    public static void main(String[] args) {
        HttpCacheManager cacheManager = new HttpCacheManager(5000); // Expiration en 5 secondes

        // Ajouter des réponses au cache
        cacheManager.put("http://example.com/api/data1", "Réponse 1", "GET");
        cacheManager.put("http://example.com/api/data2", "Réponse 2", "POST");

        // Récupérer et afficher les réponses
        System.out.println("Response Data 1: " + cacheManager.get("http://example.com/api/data1", "response"));
        System.out.println("Methodes Data 1: " + cacheManager.get("http://example.com/api/data1", "methodes"));
        System.out.println("URL Data 1: " + cacheManager.get("http://example.com/api/data1", "url"));

        // Attendre 6 secondes pour que le cache expire
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Essayer de récupérer une réponse expirée
        System.out.println(
                "Response Data 1 (après expiration): " + cacheManager.get("http://example.com/api/data1", "response"));

        // Supprimer une réponse spécifique
        cacheManager.remove("http://example.com/api/data2");

        // Essayer de récupérer une réponse supprimée
        System.out.println(
                "Response Data 2 (après suppression): " + cacheManager.get("http://example.com/api/data2", "response"));

        // Vider le cache
        cacheManager.clear();
        System.out.println("Taille du cache après vidage: " + cacheManager.size());
    }
}
