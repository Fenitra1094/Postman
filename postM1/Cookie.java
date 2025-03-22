// import java.util.HashMap;
// import java.util.Map;
// import java.util.Date;

// // Classe représentant un cookie
// public class Cookie {
//     private String name;
//     private String value;
//     private Date expirationDate;
//     private boolean isSecure;
//     private boolean isHttpOnly;
//     private String sameSite;

//     // Constructeur de Cookie
//     public Cookie(String name, String value, Date expirationDate, boolean isSecure, boolean isHttpOnly, String sameSite) {
//         this.name = name;
//         this.value = value;
//         this.expirationDate = expirationDate;
//         this.isSecure = isSecure;
//         this.isHttpOnly = isHttpOnly;
//         this.sameSite = sameSite;
//     }

//     // Getter et Setter
//     public String getName() {
//         return name;
//     }

//     public String getValue() {
//         return value;
//     }

//     public Date getExpirationDate() {
//         return expirationDate;
//     }

//     public boolean isSecure() {
//         return isSecure;
//     }

//     public boolean isHttpOnly() {
//         return isHttpOnly;
//     }

//     public String getSameSite() {
//         return sameSite;
//     }
// }

// // Classe principale pour la gestion des cookies
// public class Cookies {
//     // Stockage des cookies sous forme de HashMap
//     private Map<String, Cookie> cookiesMap;

//     // Constructeur
//     public Cookies() {
//         cookiesMap = new HashMap<>();
//     }

//     // Ajouter un cookie avec différentes options
//     public void addCookie(String name, String value, Date expirationDate, boolean isSecure, boolean isHttpOnly, String sameSite) {
//         cookiesMap.put(name, new Cookie(name, value, expirationDate, isSecure, isHttpOnly, sameSite));
//     }

//     // Ajouter un cookie avec des valeurs par défaut
//     public void addCookie(String name, String value) {
//         addCookie(name, value, null, false, false, "Lax");
//     }

//     // Récupérer la valeur d'un cookie par son nom
//     public String getCookie(String name) {
//         Cookie cookie = cookiesMap.get(name);
//         if (cookie != null && (cookie.getExpirationDate() == null || cookie.getExpirationDate().after(new Date()))) {
//             return cookie.getValue();
//         }
//         return null;  // Si cookie expiré ou inexistant
//     }

//     // Supprimer un cookie par son nom
//     public void removeCookie(String name) {
//         cookiesMap.remove(name);
//     }

//     // Vérifier si un cookie existe par son nom
//     public boolean hasCookie(String name) {
//         return cookiesMap.containsKey(name);
//     }

//     // Obtenir tous les cookies sous forme de chaîne (pour l'envoi dans les headers HTTP, par exemple)
//     public String getAllCookies() {
//         StringBuilder cookiesString = new StringBuilder();
//         for (Map.Entry<String, Cookie> entry : cookiesMap.entrySet()) {
//             Cookie cookie = entry.getValue();
//             cookiesString.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
//             if (cookie.getExpirationDate() != null) {
//                 cookiesString.append("Expires=").append(cookie.getExpirationDate()).append("; ");
//             }
//             if (cookie.isSecure()) {
//                 cookiesString.append("Secure; ");
//             }
//             if (cookie.isHttpOnly()) {
//                 cookiesString.append("HttpOnly; ");
//             }
//             if (cookie.getSameSite() != null) {
//                 cookiesString.append("SameSite=").append(cookie.getSameSite()).append("; ");
//             }
//         }
//         return cookiesString.toString();
//     }

//     // Afficher tous les cookies (utile pour le débogage)
//     public void displayCookies() {
//         if (cookiesMap.isEmpty()) {
//             System.out.println("Aucun cookie enregistré.");
//         } else {
//             System.out.println("Cookies stockés :");
//             for (Map.Entry<String, Cookie> entry : cookiesMap.entrySet()) {
//                 System.out.println(entry.getKey() + " = " + entry.getValue().getValue());
//             }
//         }
//     }
// }
