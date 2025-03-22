const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql2');
const app = express();
const port = 3000;

// Connexion à MariaDB
const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '',
  database: 'Commerce'  // Vous pouvez omettre cela si vous souhaitez choisir dynamiquement la base de données
});


db.connect(err => { 
    if(err) {
        console.error('Erreur de la connexion a la base : ' , err);
        return;
    }
    console.log('Connecte a MariaDB');
});

app.use(bodyParser.json());
// Middleware pour parser le corps de la requête en JSON
app.use(express.json());
// Fonction pour mettre à jour une ligne spécifique dans une table
function updateRow(databaseName, tableName, columnName, columnValue, updateData) {
    return new Promise((resolve, reject) => {
      // Sélectionner la base de données
      db.query(`USE ${databaseName}`, (err) => {
        if (err) {
          reject('Erreur lors de la sélection de la base de données: ' + err.message);
          return;
        }
  
        // Construire la partie SET de la requête pour la mise à jour
        const setClause = Object.keys(updateData)
          .map(key => `${key} = ?`)  // Utiliser ? pour les valeurs
          .join(', ');
  
        // Rassembler les valeurs dans le bon ordre pour la requête
        const values = [
          ...Object.values(updateData),  // Valeurs des colonnes à mettre à jour
          columnValue                    // La valeur de la colonne pour WHERE
        ];
  
        // Utilisation de paramètres préparés pour éviter les erreurs de syntaxe SQL
        const query = `UPDATE ?? SET ${setClause} WHERE ?? = ?`; // ?? pour les noms de table/colonne, ? pour les valeurs
  
        // Log pour débogage
        console.log(query, [tableName, ...values, columnName]);
  
        db.query(query, [tableName, ...values, columnName], (err, result) => {
          if (err) {
            reject('Erreur lors de la mise à jour de la ligne: ' + err.message);
          } else {
            resolve(result);
          }
        });
      });
    });
  }

// Définir la route PUT pour mettre à jour une ligne
app.put('/update-row/:database/:table/:column=:value', async (req, res) => {
  try {
    const { database, table, column, value } = req.params;  // Extraction des paramètres depuis l'URL
    const updateData = req.body;  // Les données à mettre à jour viennent du corps de la requête

    // Vérifier que des données sont fournies
    if (Object.keys(updateData).length === 0) {
      return res.status(400).send('Aucune donnée à mettre à jour.');
    }

    // Appel de la fonction pour mettre à jour la ligne
    await updateRow(database, table, column, value, updateData);
    res.status(200).send(`Ligne mise à jour avec succès dans la table ${table} de la base de données ${database} où ${column} = ${value}.`);
  } catch (err) {
    res.status(500).send('Erreur lors de la mise à jour: ' + err);
  }
});

// Démarrer le serveur
app.listen(port, () => {
  console.log(`API en cours d'exécution sur http://localhost:${port}`);
});
