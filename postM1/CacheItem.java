package All;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheItem {
    private String response; // Réponse HTTP mise en cache
    private long timestamp;  // Timestamp d'ajout au cache
    private String methodes; // En-têtes HTTP associés à la réponse
    private String url;      // URL de la réponse

    public CacheItem(String url, String response, String methodes) {
        this.url = url;
        this.response = response;
        this.timestamp = System.currentTimeMillis();
        this.methodes = methodes;
    }

    public String getResponse() {
        return response;
    }

    public String getMethodes() {
        return methodes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }
}
