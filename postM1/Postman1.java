
package All;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import historique.Historique;

public class Postman1 extends JFrame {
    private JTextField urlField;
    private JComboBox<String> methodeUrl;
    private JComboBox<String> methodComboBox;
    private JPanel historyPanel;

    private JTabbedPane mainTabbedPane;
    private JTextArea requestBodyArea;
    private JLabel statusLabel;
    String rawText;
    JComboBox<String> bodyTypeComboBox;

    private JTable headersTable;
    private JTable paramsTable;
    private JTable authTable;
    private JTable formDataTable;
    private JTable urlEncodedTable;
    private JTable rawTable;

    // la reponse
    private JEditorPane previewPane;
    private JTextArea prettyArea;
    private JTextArea headerArea;
    private JTextArea cacheArea;


    // Response details
    private JLabel responseTimeLabel;
    private JLabel responseCodeLabel;

    // Déclaration de rawTextArea à la portée globale
    private JTextArea rawTextArea;

    // Déclaration liste des requêtes (historiques)
    private static Vector<Historique> historyList = new Vector<>();
    // Déclaration de la file où l'historique est sauvegardé
    private static String HISTORY_FILE = "history.dat";
    private static JTable historyTable;
    CacheItem CacheNavigateur;
    Final fx = new Final();
    Configx conf = new  Configx();
    HttpCacheManager cacheManager = new HttpCacheManager(conf.getDurreVie()); // Expiration en 1minute 



    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] methodx = { "https", "http" };
        methodeUrl = new JComboBox<>(methodx);

        urlField = new JTextField(50);
        urlField.setToolTipText("Enter URL here");

        String[] methods = { "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS" };
        methodComboBox = new JComboBox<>(methods);

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(Color.GREEN);
        sendButton.addActionListener(e -> executeRequest());

        JButton saveButton = new JButton("Save Request");
        saveButton.addActionListener(
                e -> this.fx.saveCurrentRequest(urlField, methodComboBox, requestBodyArea, headersTable, this));

        JButton loadButton = new JButton("Load Request");
        loadButton.addActionListener(
                e -> this.fx.loadSavedRequest(urlField, methodComboBox, requestBodyArea, headersTable, this));

                JButton clearBtn = new JButton("clear all");
                clearBtn.addActionListener(
                        e -> this.cacheManager.clear() );
        
        topPanel.add(methodeUrl);
        topPanel.add(new JLabel("URL:"));
        topPanel.add(urlField);
        topPanel.add(new JLabel("Method:"));
        topPanel.add(methodComboBox);
        topPanel.add(sendButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);
        topPanel.add(clearBtn);

        return topPanel;
    }

    private void initializeUI() {
        setTitle("PostMaki");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Panel: URL, Method, Send Button
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Main Content Area
        mainTabbedPane = new JTabbedPane();

        // Request Tabs
        JPanel requestPanel = createRequestPanel();
        mainTabbedPane.addTab("Request", requestPanel);

        // Response Area
        JPanel responsePanel = createResponsePanel();
        mainTabbedPane.addTab("Response", responsePanel);

      

        // Historique
        historyPanel = createHistoryPanel();
        mainTabbedPane.addTab("Historique", historyPanel);
 
        loadHistory();

        add(mainTabbedPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    // public void generateMenu( String[] tableValue , JPanel[] PanelConcerned ){
    // // Main Content Area
    // mainTabbedPane = new JTabbedPane();

    // // Request Tabs
    // for (int i = 0; i < tableValue.length; i++) {
    // JPanel requestPanel = createRequestPanel();
    // mainTabbedPane.addTab("Request", requestPanel);
    // }
    // }

    public Postman1() {
        initializeUI();
    }

    public void UpdateTable(JPanel tabPanel, JTable tableConcerned) {
        JPanel paramsButtonPanel = new JPanel(); // Conteneur pour les boutons de ce panneau
        JButton addParamsRowButton = new JButton("Add Row");
        JButton removeParamsRowButton = new JButton("Remove Row");

        // Actions des boutons
        addParamsRowButton.addActionListener(e -> addRow(tableConcerned));
        removeParamsRowButton.addActionListener(e -> removeRow(tableConcerned));

        // Ajouter les boutons au panel
        paramsButtonPanel.add(addParamsRowButton);
        paramsButtonPanel.add(removeParamsRowButton);

        // Ajouter le tableau et les boutons au panneau spécifique
        tabPanel.setLayout(new BorderLayout()); // Utilisation de BorderLayout
        tabPanel.add(new JScrollPane(tableConcerned), BorderLayout.CENTER); // Tableau au centre
        tabPanel.add(paramsButtonPanel, BorderLayout.SOUTH); // Boutons en bas
    }

    private JPanel createBodyPanel() {
        // Panneau principal pour l'onglet Body
        JPanel bodyPanel = new JPanel(new BorderLayout());

        // Menu déroulant pour choisir le type
        bodyTypeComboBox = new JComboBox<>(
                new String[] { "none", "form-data", "x-www-form-urlencoded", "raw" });

        // CardLayout pour basculer entre différents panneaux
        JPanel bodyCardPanel = new JPanel(new CardLayout());

        // Panneau vide pour "none"
        JPanel nonePanel = new JPanel();

        // Panneau pour "form-data" : Utilisation de UpdateTable
        JPanel formDataPanel = new JPanel();
        formDataTable = createEditableTableFor(new String[] { "Key", "Value" });
        UpdateTable(formDataPanel, formDataTable);

        // Panneau pour "x-www-form-urlencoded" : Utilisation de UpdateTable
        JPanel urlEncodedPanel = new JPanel();
        urlEncodedTable = createEditableTable(new String[] { "Key", "Value" });
        UpdateTable(urlEncodedPanel, urlEncodedTable);

        // Panneau pour "raw" : Une grande zone de texte
        rawTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(rawTextArea);
        /*
         * JScrollPane scrollPane = new JScrollPane(this.rawTextArea);
         * container.add(scrollPane);
         */
        rawTextArea.setLineWrap(true);
        JPanel rawPanel = new JPanel(new BorderLayout());
        rawPanel.add(new JScrollPane(rawTextArea), BorderLayout.CENTER);

        // Ajouter les panneaux au CardLayout
        bodyCardPanel.add(nonePanel, "none");
        bodyCardPanel.add(formDataPanel, "form-data");
        bodyCardPanel.add(urlEncodedPanel, "x-www-form-urlencoded");
        bodyCardPanel.add(rawPanel, "raw");

        // Action : Bascule entre les panneaux selon la sélection
        bodyTypeComboBox.addActionListener(e -> {
            String selectedType = (String) bodyTypeComboBox.getSelectedItem();
            CardLayout cl = (CardLayout) bodyCardPanel.getLayout();
            cl.show(bodyCardPanel, selectedType); // Afficher le bon panneau
        });

        // Ajouter le menu déroulant et les cartes au panneau principal
        bodyPanel.add(bodyTypeComboBox, BorderLayout.NORTH);
        bodyPanel.add(bodyCardPanel, BorderLayout.CENTER);

        return bodyPanel;
    }

    private JPanel createRequestPanel() {
        JPanel requestPage = new JPanel(new BorderLayout());
        JTabbedPane requestTable = new JTabbedPane();

        String[] tableVal = { "Key", "Value", "Description" };
        paramsTable = createEditableTable(tableVal);
        JPanel paramsTabPanel = new JPanel(); // Panneau spécifique pour l'onglet "Params"
        UpdateTable(paramsTabPanel, paramsTable); // Boutons et tableau propres à cet onglet
        paramsTable.getModel().addTableModelListener(e -> this.fx.updateURL(urlField, paramsTable));

        requestTable.addTab("Params", paramsTabPanel);

        // Onglet "Headers"
        headersTable = createEditableTable(new String[] { "Key", "Value", "Description" });
        JPanel headersTabPanel = new JPanel(); // Panneau spécifique pour l'onglet "Headers"
        UpdateTable(headersTabPanel, headersTable); // Boutons et tableau propres à cet onglet
        requestTable.addTab("Headers", headersTabPanel);

        // Auth Tab
        authTable = createEditableTable(new String[] { "Type", "Value", "Description" });
        requestTable.addTab("Authorization", new JScrollPane(authTable));

        // Onglet Body
        JPanel bodyPanel = createBodyPanel();
        requestTable.addTab("Body", bodyPanel);
        requestPage.add(requestTable, BorderLayout.CENTER);
        return requestPage;
    }

    private void addRow(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[] { "", "", "" });
    }

    private void removeRow(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(table, "Please select a row to remove.");
        }
    }

    private List<Map<String, String>> getTableData(JTable table) {
        List<Map<String, String>> tableData = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < model.getColumnCount(); j++) {
                String columnName = model.getColumnName(j);
                String cellValue = model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString() : "";
                rowData.put(columnName, cellValue);
            }
            tableData.add(rowData);
        }

        return tableData;
    }

    private void printTableData(List<Map<String, String>> tableData) {
        if (tableData == null || tableData.isEmpty()) {
            System.out.println("La liste est vide ou nulle.");
            return;
        }

        System.out.println("Contenu de la table :");
        for (int i = 0; i < tableData.size(); i++) {
            Map<String, String> row = tableData.get(i);
            System.out.println("Ligne " + (i + 1) + ":");
            for (Map.Entry<String, String> entry : row.entrySet()) {
                System.out.println("    " + entry.getKey() + " : " + entry.getValue());
            }
        }
    }

    public Vector<String[]> TakeTableData(List<Map<String, String>> tableData) {
        if (tableData == null || tableData.isEmpty()) {
            return null;
        }

        Vector<String[]> tableResult = new Vector<String[]>();
        String[] tableKey = new String[tableData.size()];
        String[] tableValue = new String[tableData.size()];
        for (int i = 0; i < tableData.size(); i++) {
            Map<String, String> row = tableData.get(i);
            // String[] Value = {"cle" , "val" };
            int indice = 0;
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("key")) {
                    tableKey[i] = entry.getValue();
                }
                if (entry.getKey().equalsIgnoreCase("value")) {
                    tableValue[i] = entry.getValue();
                }
                if (entry.getKey().equalsIgnoreCase("description")) {
                }

                // System.out.println("" + entry.getKey() + " : " + entry.getValue());
            }
        }
        tableResult.add(tableKey);
        tableResult.add(tableValue);
        return tableResult;
    }

    private JPanel createResponsePanel() {
        JPanel responsePanel = new JPanel(new BorderLayout());

        prettyArea = new JTextArea();
        prettyArea.setEditable(false);
        prettyArea.setLineWrap(true);

        headerArea = new JTextArea();
        headerArea.setEditable(false);
        headerArea.setLineWrap(true);

        cacheArea = new JTextArea();
        cacheArea.setEditable(false);
        cacheArea.setLineWrap(true);
        
        previewPane = new JEditorPane();
        previewPane.setContentType("text/html");
        previewPane.setEditable(false);

        JTabbedPane responseTabs = new JTabbedPane();
        responseTabs.addTab("Pretty", new JScrollPane(prettyArea));
        responseTabs.addTab("Preview", new JScrollPane(previewPane));
        responseTabs.addTab("Headers", new JScrollPane(headerArea));
        responseTabs.addTab("Cache", new JScrollPane(cacheArea));


        // Future: Add Headers, Cookies tabs
        responsePanel.add(responseTabs, BorderLayout.CENTER);

        return responsePanel;
    }

    private JPanel createHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());

        historyTable = new JTable(new DefaultTableModel(new Object[] { "Method", "URL" }, 0));
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return historyPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        statusLabel = new JLabel("Ready");
        responseTimeLabel = new JLabel("Time: - ms");
        responseCodeLabel = new JLabel("Status: -");

        statusPanel.add(statusLabel);
        statusPanel.add(responseTimeLabel);
        statusPanel.add(responseCodeLabel);

        return statusPanel;
    }

    private JTable createEditableTable(String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        // Add a row by default
        model.addRow(new Object[] { "", "", "" });
        // Allow adding new rows
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        return table;
    }

    private JTable createEditableTableFor(String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        // Créer la JTable
        JTable table = new JTable(model);
        model.addRow(new Object[] { "", "", "" });

        // Ajouter un éditeur personnalisé à la colonne "Fichier"
        table.getColumn("Value").setCellEditor(new FileChooserEditor());

        // Allow adding new rows
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        return table;
    }

    private void executeRequest() {
        String urlx = urlField.getText();
        String urlSelector = (String) methodeUrl.getSelectedItem();

        URL urlEntre = fx.convertUrl( urlx , urlSelector ) ; 
        String  urlFormed = fx.convertUrlSimple(urlx , urlSelector) ; 
        /// ilayy get/post ...
        
        String method = (String) methodComboBox.getSelectedItem();  
        
        
        List<Map<String, String>> headersTable1 =  getTableData(headersTable )     ; 
        List<Map<String, String>> paramsTable1 =  getTableData(paramsTable )     ; 
        List<Map<String, String>> authTable1 =  getTableData(authTable )     ; 
        List<Map<String, String>> formDataTable1 =  getTableData(formDataTable )     ; 
        List<Map<String, String>> urlEncodedTable1 =  getTableData(urlEncodedTable )     ;
        
        // printTableData(headersTable1  ) ;            
        //     printTableData( paramsTable1  ) ;
        //         printTableData( authTable1  ) ;
        String ReponseValue = "" ; 
        // new String[]{"none", "form-data", "x-www-form-urlencoded", "raw"}
        // this.fx.updateURL(urlField, paramsTable) ; 
        try {
        if((cacheManager.get(urlFormed,  "url" ) != null ) && (cacheManager.get(urlFormed,  "methodes" ) != null )){
        //   System.out.println("Teetoooooooo");
            if((cacheManager.get(urlFormed,  "url" ).equalsIgnoreCase(urlFormed) ) && (cacheManager.get(urlFormed,  "methodes" ).equalsIgnoreCase(method))){                
        //   System.out.println("Teetoooooooo 111");
                System.out.println("Value recupered in cache , so do not send request in the server!");
                String urlxx =  cacheManager.get( urlFormed , "url") ; 
                 urlEntre = new URL(urlxx) ; 
                 method  =  cacheManager.get( urlFormed , "methodes") ; 
                 ReponseValue =  cacheManager.get( urlFormed , "response") ; 
             }
         }else{
            String selectedBodyType = (String) bodyTypeComboBox.getSelectedItem();
           if((method.equalsIgnoreCase("POST")) ){
                if( ( selectedBodyType.equalsIgnoreCase("x-www-form-urlencoded"))){
                    Vector<String[]> table =  TakeTableData(urlEncodedTable1) ; 
                    ReponseValue  =  this.fx.MethodPost( urlEntre,  table.elementAt(0) ,  table.elementAt(1))  ;
                }
                if( ( selectedBodyType.equalsIgnoreCase("form-data"))){
                    Vector<String[]> table =  TakeTableData(formDataTable1) ; 
                    ReponseValue  =  this.fx.MethodPost( urlEntre,  table.elementAt(0) ,  table.elementAt(1))  ;
                    File Files  = new File( table.elementAt(1)[0]);
                    String keyx  =   table.elementAt(0)[0];
                    ReponseValue  = this.fx.uploadPost(Files, keyx , urlEntre )  ; 
                    // ReponseValue  = String.valueOf(val);
                }
            }
            if((method.equalsIgnoreCase("get")) ){
                ReponseValue  =  this.fx.sendGetRequest(urlEntre)  ;
            }
            if((method.equalsIgnoreCase("put")) ){
                String jsonInput = rawTextArea.getText().trim();
                ReponseValue  =  this.fx.sendPutRequest(urlEntre , jsonInput)  ;
            }
            if((method.equalsIgnoreCase("delete")) ){
                String jsonInput = rawTextArea.getText().trim();
                ReponseValue  =  this.fx.sendDeleteRequest(urlEntre)  ;
            }
            if(cacheManager.get(urlFormed,  "url" ) == null ){
                System.out.println("Value Not found in cache , add in cache with succes!");
                cacheManager.put(urlFormed ,ReponseValue , method ) ; 
                // System.out.println();
            }
        }
            prettyArea.setText(ReponseValue);
            previewPane.setText(ReponseValue);
            headerArea.setText( this.fx.AfficheEntete( urlEntre ));
            cacheArea.setText( cacheManager.listeValueInCache());
            historyList.add(new Historique(method, urlx, "", "", ReponseValue));
            saveHistory();
            updateHistoryTable(); // Actualiser la JTable après l'ajout
        }
        catch (Exception e) {
            prettyArea.setText("Error: " + e.getMessage());
            previewPane.setText("Error: " + e.getMessage());
            statusLabel.setText("Request Failed");
        } 
         historyTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int selectedRow = historyTable.getSelectedRow();
                        Historique selectedRequest = historyList.get(selectedRow);

                        methodComboBox.setSelectedItem(selectedRequest.getMethod());
                        urlField.setText(selectedRequest.getUrl());
                        rawTextArea.setText(selectedRequest.getBody());

                        JOptionPane.showMessageDialog(historyPanel, "Requête chargée depuis l'historique !");
                    }
                }
            });

    //frame.setVisible(true);
    
    
    }

    private HttpURLConnection setupConnection(String urlString, String method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        return connection;
    }

    private void addHeadersToConnection(HttpURLConnection connection) {
        DefaultTableModel model = (DefaultTableModel) headersTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String key = (String) model.getValueAt(i, 0);
            String value = (String) model.getValueAt(i, 1);
            if (!key.isEmpty() && !value.isEmpty()) {
                connection.setRequestProperty(key, value);
            }
        }
    }

    private boolean shouldSendBody(String method) {
        return method.equals("POST") || method.equals("PUT") ||
                method.equals("PATCH") || method.equals("DELETE");
    }

    private void sendRequestBody(HttpURLConnection connection) throws IOException {
        String bodyText = requestBodyArea.getText();
        if (!bodyText.isEmpty()) {
            connection.setDoOutput(true);
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(bodyText);
                writer.flush();
            }
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ex) {
            inputStream = connection.getErrorStream();
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Postman1 client = new Postman1();
            client.setVisible(true);
        });

    }

    private static void saveHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(historyList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la sauvegarde de l'historique : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void loadHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof Vector<?>) {
                    historyList.clear();
                    for (Object o : (Vector<?>) obj) {
                        if (o instanceof Historique) {
                            historyList.add((Historique) o);
                        }
                    }
                }
                updateHistoryTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void updateHistoryTable() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);

        for (Historique historique : historyList) {
            model.addRow(new Object[] { historique.getMethod(), historique.getUrl() });
        }
    }
}