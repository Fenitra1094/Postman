package All;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        String[] key = { "name" , "email"};
        String[] value = { "Luckas" , "Rbm"};
        try {
             Final fx = new Final();
            String Result  = fx.MethodPost(new URL("http://localhost/luckas.php"), key, value);
            System.out.println(Result);
        } catch (Exception e) {
            // Afficher l'exception dans le cas où une erreur se produit
            e.printStackTrace(); 
        }
        
    }
}
