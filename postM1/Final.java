package All;

import java.nio.charset.StandardCharsets;

import javax.swing.table.DefaultTableModel;
import javax.print.attribute.standard.RequestingUserName;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.nio.charset.StandardCharsets;


// cest quoi lauthentification a base token avec http client 


public class Final {
    public HttpURLConnection CreateConnection(URL lienURL) {
        try {
            // Créer une connexion à l'URL spécifiée
            //cad creer un pont grace a lurl specifique 
            URLConnection ConnexionUrl = lienURL.openConnection();
            // Convertir la connexion en HttpURLConnection (car URLConnection est une classe générale
            HttpURLConnection connection = (HttpURLConnection) ConnexionUrl;
            return connection;
        } catch (Exception e) {
        }
        return null;
    }

    // Fonction qui permet de prendre le resultat dun requete venant du serveur
    public String TakeResult(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        // System.out.println("Reponse du serveur : " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Affiche la reponse du serveur
                return response.toString();
            }
        } else {
            System.out.println("Échec de la requête. Code de réponse HTTP : " + responseCode);
        }
        return null;
    }

    /// Mamadika ho forme lisible an i serveur
    /// Objectif email=mama&nom=jean 
    public StringBuilder getForm(String[] key, String[] value) {
        StringBuilder postData = new StringBuilder();

        for (int i = 0; i < key.length; i++) {
            if (i > 0) {
                try {
                    postData.append("&");
                    postData.append(URLEncoder.encode(key[i], "UTF-8"));
                    postData.append("=");
                    postData.append(URLEncoder.encode(value[i], "UTF-8"));
                } catch (Exception e) {
                }
            } 
            /// 
            else {
                try {
                    postData.append(URLEncoder.encode(key[i], "UTF-8"));
                    postData.append("=");
                    postData.append(URLEncoder.encode(value[i], "UTF-8"));
                } catch (Exception e) {
    
                }
            }

        }
        return postData;
    }

    /// if 0 -> non secure || 1 -> secure
    public URL convertUrl(String urlString, String Securise) {
        String httpx = "";
        if (Securise.equalsIgnoreCase("https")) {
            httpx = "https://";
        }
        if (Securise.equalsIgnoreCase("http")) {
            httpx = "http://";
        }
        try {
            // Convertir la chaîne en URL
            String Value = httpx + urlString;
            URL url = new URL(Value);
            return url;
        } catch (MalformedURLException e) {
            // Gestion de l'exception en cas d'URL invalide
            System.err.println("URL invalide : " + e.getMessage());
        }
        return null;
    }

    public String convertUrlSimple(String urlString, String Securise) {
        String httpx = "";
        if (Securise.equalsIgnoreCase("https")) {
            httpx = "https://";
        }
        if (Securise.equalsIgnoreCase("http")) {
            httpx = "http://";
        }
        try {
            // Convertir la chaîne en URL
            String Value = httpx + urlString;
            return Value ;
        } catch (Exception e) {
            // Gestion de l'exception en cas d'URL invalide
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

 
    public String MethodPost(URL lienURL, String[] key, String[] value) {
        HttpURLConnection connection = null;
        try {
            connection = CreateConnection(lienURL);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder postData = getForm(key, value);

            // Écriture des données dans le corps de la requête POST
            try (OutputStream fluxExterne = connection.getOutputStream()) {
                String SendMessage = postData.toString();
                System.out.println(SendMessage);
                byte[] MessageInByte = SendMessage.getBytes("utf-8");
                fluxExterne.write(MessageInByte, 0, MessageInByte.length);
            }

            // Obtenir le code de réponse
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            response.append("Code de réponse : ").append(responseCode).append("\n");

            // Lire le contenu de la réponse en cas de succès (200 OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                in.close();
            } else {
                // Si ce n'est pas un code 200, obtenir le message d'erreur
                response.append("Erreur : ").append(connection.getResponseMessage());
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String sendGetRequest(URL urlString) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) urlString.openConnection();

        // Configurer la requête
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // Timeout pour la connexion
        connection.setReadTimeout(5000); // Timeout pour la lecture
        // Lire la réponse
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        response.append("Code de réponse : ").append(responseCode).append("\n");

        // Lire le contenu de la réponse si le code est 200 (OK)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
        } else {
            response.append("Erreur : ").append(connection.getResponseMessage());
        }

        connection.disconnect();
        return response.toString();
    }

    // methode pour put
    public String sendPutRequest(URL url, String jsonInput) throws Exception {
        // URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configurer la requête
        connection.setRequestMethod("PUT");
        connection.setConnectTimeout(5000); // Timeout pour la connexion
        connection.setReadTimeout(5000); // Timeout pour la lecture
        connection.setDoOutput(true); // Permet l'envoi de données
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // Écrire les données dans le corps de la requête
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Lire la réponse
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        response.append("Code de réponse : ").append(responseCode).append("\n");

        // Lire le contenu de la réponse si le code est 200 (OK) ou 204 (No Content)
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
        }

        connection.disconnect();
        return response.toString();
    }

    public String sendDeleteRequest(URL url) throws Exception {
        // URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configurer la requête DELETE
        connection.setRequestMethod("DELETE");
        connection.setConnectTimeout(5000); // Timeout pour la connexion
        connection.setReadTimeout(5000); // Timeout pour la lecture

        // Envoyer la requête et lire la réponse
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        response.append("Code de réponse : ").append(responseCode).append("\n");

        // Lire le contenu de la réponse si le code est 200 (OK)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
        } else {
            response.append("Erreur : ").append(connection.getResponseMessage());
        }

        connection.disconnect();
        return response.toString();
    }

    // fonction updateURL necessire a la fonction showParamsSection
    public void updateURL(JTextField urlField, JTable paramsTable) {
        String baseURL = urlField.getText().split("\\?")[0];
        StringBuilder queryParams = new StringBuilder();

        for (int i = 0; i < paramsTable.getRowCount(); i++) {
            String key = (String) paramsTable.getValueAt(i, 0);
            String value = (String) paramsTable.getValueAt(i, 1);

            if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
                if (queryParams.length() > 0)
                    queryParams.append("&");
                queryParams.append(key).append("=").append(value);
            }
        }

        urlField.setText(baseURL + (queryParams.length() > 0 ? "?" + queryParams.toString() : ""));
    }

    public void showParamsSection(JTextField urlField, JTable paramsTable, JPanel dynamicPanel) {
        dynamicPanel.removeAll();
        JScrollPane scrollPane = new JScrollPane(paramsTable);
        dynamicPanel.setLayout(new BorderLayout());
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("Params"));
        dynamicPanel.add(scrollPane, BorderLayout.CENTER);

        paramsTable.getModel().addTableModelListener(e -> updateURL(urlField, paramsTable));

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    public void saveCurrentRequest(JTextField urlField, JComboBox<String> methodComboBox, JTextArea requestBodyArea,
            JTable headersTable, JFrame Conteneur) {
        // Implement request saving logic
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(Conteneur);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("URL:" + urlField.getText());
                writer.println("Method:" + methodComboBox.getSelectedItem());
                writer.println("Body:" + requestBodyArea.getText());

                // Save headers
                DefaultTableModel headerModel = (DefaultTableModel) headersTable.getModel();
                writer.println("Headers-Count:" + headerModel.getRowCount());
                for (int i = 0; i < headerModel.getRowCount(); i++) {
                    writer.println("Header:" +
                            headerModel.getValueAt(i, 0) + "=" +
                            headerModel.getValueAt(i, 1));
                }

                JOptionPane.showMessageDialog(Conteneur, "Request saved successfully!");
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(Conteneur, "Error saving request: " + ex.getMessage());
            }
        }
    }

    public void loadSavedRequest(JTextField urlField, JComboBox<String> methodComboBox, JTextArea requestBodyArea,
            JTable headersTable, JFrame Conteneur) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(Conteneur);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                DefaultTableModel headerModel = (DefaultTableModel) headersTable.getModel();
                headerModel.setRowCount(0); // Clear existing headers

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("URL:")) {
                        urlField.setText(line.substring(4));
                    } else if (line.startsWith("Method:")) {
                        methodComboBox.setSelectedItem(line.substring(7));
                    } else if (line.startsWith("Body:")) {
                        requestBodyArea.setText(line.substring(5));
                    } else if (line.startsWith("Headers-Count:")) {
                        // Skip Conteneur line, we'll read headers next
                    } else if (line.startsWith("Header:")) {
                        String[] headerParts = line.substring(7).split("=");
                        if (headerParts.length == 2) {
                            headerModel.addRow(new Object[] { headerParts[0], headerParts[1], "" });
                        }
                    }
                }

                JOptionPane.showMessageDialog(Conteneur, "Request loaded successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(Conteneur, "Error loading request: " + ex.getMessage());
            }
        }
    }

    public JFileChooser gererateFileChoice() {
        // Créer la boîte de dialogue JFileChooser
        JFileChooser fileChooser = new JFileChooser();

        // Afficher la boîte de dialogue et récupérer la réponse
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            // Récupérer le fichier sélectionné
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("Aucun fichier sélectionné");
        }
        return fileChooser;
    }

    public void readFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            System.out.println("Contenu du fichier lu avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String uploadPost(File file, String keys, URL url) {
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            // Créer une connexion HTTP
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----Boundary");

            // Créer un flux de sortie
            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, "UTF-8");
            PrintWriter writer = new PrintWriter(outputWriter, true);
            
            // Ajouter les métadonnées du fichier
            writer.append("------Boundary\r\n");
            writer.append("Content-Disposition: form-data; name=\"" + keys + "\"; filename=\"" + file.getName() + "\"\r\n");
            writer.append("Content-Type: application/octet-stream\r\n\r\n");
            writer.flush();
    
            // Ajouter le contenu du fichier
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
    
            // Terminer la requête
            writer.append("\r\n").flush();
            writer.append("------Boundary--\r\n");
            writer.close();
    
            // Lire la réponse du serveur
            int responseCode = connection.getResponseCode();
            BufferedReader in;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Si le code de réponse est 200, lire le contenu de la réponse
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                in.close();
                System.out.println("Fichier envoyé avec succès !");
            } else {
                // En cas d'erreur, obtenir le message d'erreur
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                in.close();
                System.out.println("Erreur lors de l'envoi : " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur de connexion ou d'envoi de fichier.";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }
    


    public Map<String, String> getHeadersFromUrl(  URL url ) {
        Map<String, String> headersMap = new HashMap<>();
        try {
            // Ouvrir la connexion
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lire les en-têtes renvoyés par le serveur
            for (Map.Entry<String, java.util.List<String>> entry : connection.getHeaderFields().entrySet()) {
                String key = entry.getKey(); // Clé de l'en-tête (par ex. Date, Server, etc.)
                String value = String.join(", ", entry.getValue()); // Valeur de l'en-tête
                if (key != null) { // Certains en-têtes peuvent avoir une clé null (par ex. la ligne HTTP/1.1 200 OK)
                    headersMap.put(key, value);
                }
            }
            // Fermer la connexion
            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des en-têtes : " + e.getMessage());
        }

        return headersMap;
    }

      // Fonction qui permet de prendre le resultat dun requete venant du serveur
      public String TakeResult(URL url)  {
        String Val = "" ; 
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            Val =  responseCode+"" ; 
        } catch (Exception e) {
        }
        return Val ;
    }

    public String AfficheEntete( URL url) {
        Map<String, String>  headersMap =  getHeadersFromUrl( url  ) ; 
        // Initialiser un StringBuilder pour créer la chaîne de caractères
        StringBuilder result = new StringBuilder();
        String Rep  =  TakeResult( url ) ; 
        String Valuex = "En-tetes recuperes :"+"\n"+"Reponse du serveur : " + Rep +"\n";
        result.append(Valuex) ; 
        // Parcourir chaque entrée de la Map et formater les clés et valeurs
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            result.append(entry.getKey())
                  .append(": ")
                  .append(entry.getValue())
                  .append("\n"); // Ajouter un saut de ligne après chaque en-tête
        }
    
        // Retourner la chaîne formée
        return result.toString();
    }


    public  String getHeaders(String urlString) throws IOException {
        // Convertir l'URL en objet URL
        URL url = new URL(urlString);

        // Ouvrir une connexion HTTP
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Définir la méthode HTTP (GET par défaut)
        connection.setRequestMethod("GET");

        // Obtenir les en-têtes de la réponse
        Map<String, List<String>> headers = connection.getHeaderFields();

        // Fermer la connexion
        connection.disconnect();

        // Construire une chaîne pour les en-têtes
        StringBuilder headerString = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            // Ajouter chaque en-tête sous forme de "Clé: Valeur"
            headerString.append(entry.getKey()).append(": ").append(String.join(", ", entry.getValue())).append("\n");
        }

        // Retourner les en-têtes sous forme de chaîne
        return headerString.toString();
    }

}
