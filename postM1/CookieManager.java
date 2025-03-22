import java.util.HashMap;
import java.util.Map;

public class CookieManager {
    private Map<String, String> cookies = new HashMap<>();

    // Ajouter un cookie
    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    // Supprimer un cookie
    public void removeCookie(String name) {
        cookies.remove(name);
    }

    // Générer l'en-tête Cookie
    public String generateCookieHeader() {
        StringBuilder cookieHeader = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            if (cookieHeader.length() > 0) {
                cookieHeader.append("; ");
            }
            cookieHeader.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return cookieHeader.toString();
    }

    // Mettre à jour les cookies à partir de l'en-tête Set-Cookie
    public void updateFromSetCookieHeader(String setCookieHeader) {
        if (setCookieHeader != null && !setCookieHeader.isEmpty()) {
            String[] parts = setCookieHeader.split(";");
            if (parts.length > 0) {
                String[] keyValue = parts[0].split("=", 2);
                if (keyValue.length == 2) {
                    cookies.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }
    }

    // Afficher les cookies actuels
    public Map<String, String> getCookies() {
        return new HashMap<>(cookies);
    }
}
