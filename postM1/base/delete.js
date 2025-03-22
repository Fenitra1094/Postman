const express = require('express');
const mysql = require('mysql');
const app = express();
const port = 3000;

// Connexion à MariaDB
const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '',
  database: 'Commerce'  // Vous pouvez omettre cela si vous souhaitez choisir dynamiquement la base de données
});

// Fonction pour supprimer une ligne spécifique dans une table
function deleteRow(databaseName, tableName, columnName, columnValue) {
  return new Promise((resolve, reject) => {
    // Sélectionner la base de données
    db.query(`USE ${databaseName}`, (err) => {
      if (err) {
        reject('Erreur lors de la sélection de la base de données: ' + err.message);
        return;
      }

      // Utilisation de paramètres préparés pour éviter les erreurs de syntaxe SQL
      const query = `DELETE FROM ?? WHERE ?? = ?`; // ?? pour les noms de table/colonne, ? pour les valeurs
      db.query(query, [tableName, columnName, columnValue], (err, result) => {
        if (err) {
          reject('Erreur lors de la suppression de la ligne: ' + err.message);
        } else {
          resolve(result);
        }
      });
    });
  });
}

// Définir la route DELETE
app.delete('/delete-row/:database/:table/:column=:value', async (req, res) => {
  try {
    const { database, table, column, value } = req.params;  // Extraction des paramètres depuis l'URL

    // Appel de la fonction pour supprimer la ligne
    await deleteRow(database, table, column, value);
    res.status(200).send(`Ligne supprimée avec succès dans la table ${table} de la base de données ${database} où ${column} = ${value}.`);
  } catch (err) {
    res.status(500).send('Erreur lors de la suppression: ' + err);
  }
});

// Démarrer le serveur
app.listen(port, () => {
  console.log(`API en cours d'exécution sur http://localhost:${port}`);
});
