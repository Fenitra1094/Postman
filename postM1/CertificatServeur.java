import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.security.cert.Certificate;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;


public class CertificatServeur {
     public static void main(String[] args) throws Exception {
        // Adresse du serveur et le port
        String host = "example.com";
        int port = 443;

        // Création d'un SSLSocket pour se connecter au serveur
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

        // Démarre le processus de handshake SSL (négociation)
        socket.startHandshake();

        // Récupère les certificats du serveur
        Certificate[] serverCerts = socket.getSession().getPeerCertificates();

        // Afficher les certificats
        for (Certificate cert : serverCerts) {
            System.out.println(cert.toString());
        }

        // Fermer la connexion
        socket.close();
    }


    public void getCertificat( String host, int port ){
        try {
        
        // Création d'un SSLSocket pour se connecter au serveur
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

        // Démarre le processus de handshake SSL (négociation)
        socket.startHandshake();

        // Récupère les certificats du serveur
        Certificate[] serverCerts = socket.getSession().getPeerCertificates();

        // Afficher les certificats
        for (Certificate cert : serverCerts) {
            System.out.println(cert.toString());
        }

        // Fermer la connexion
        socket.close();

            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // public static void saveCertificate(Certificate cert) throws IOException {
    //     X509Certificate x509Cert = (X509Certificate) cert;

    //     // Enregistrer le certificat dans un fichier
    //     try (FileOutputStream out = new FileOutputStream("server-cert.pem")) {
    //         out.write("-----BEGIN CERTIFICATE-----\n".getBytes());
    //         out.write(java.util.Base64.getEncoder().encode(x509Cert.getEncoded()));
    //         out.write("\n-----END CERTIFICATE-----".getBytes());
    //     }
        
    // }

}
