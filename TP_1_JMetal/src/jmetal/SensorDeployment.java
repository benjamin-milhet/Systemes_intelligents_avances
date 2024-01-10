package jmetal;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SensorDeployment extends Problem {

    private int nombreCapteurs;
    private int rayonCapteurs;
    private int dimmension;
    private int[][] coordonneesCibles;

    public SensorDeployment(String solutionType, int nombreCapteurs, int rayonCapteurs, int dimmension) {
        this.nombreCapteurs = nombreCapteurs * 2; // 2 variables par capteur (x, y)
        super.problemName_ = "SensorDeployment"; // Nom du problème
        super.numberOfObjectives_ = 2; // Nombre de fonctions objectifs
        super.numberOfConstraints_ = 0; // Nombre de contraintes
        super.numberOfVariables_ = this.nombreCapteurs; // Nombre de variables de décision
        this.dimmension = dimmension; // Dimension de l'espace de recherche

        // Type de solution
        if (solutionType.compareTo("Real") == 0) {
            this.solutionType_ = new RealSolutionType(this);
        } else {
            System.out.println("Error");
            System.exit(-1);
        }

        super.lowerLimit_ = new double[numberOfVariables_]; // Limite inférieure des variables de décision
        super.upperLimit_ = new double[numberOfVariables_]; // Limite supérieure des variables de décision

        for (int i = 0 ; i < numberOfVariables_ ; i++) { // Initialiser les limites des variables de décision
            super.lowerLimit_[i] = 0.0; // Limite inférieure à 0
            super.upperLimit_[i] = this.dimmension; // Limite supérieure à la dimension de l'espace de recherche
        }

        this.rayonCapteurs = rayonCapteurs; // Initialiser le rayon des capteurs

        this.lireFichierEtInitialiser("src/jmetal/cible.txt"); // Lire le fichier contenant les coordonnées des cibles

        for (int i = 0; i < this.coordonneesCibles.length; i++) { // Parcourir les cibles
            System.out.println(this.coordonneesCibles[i][0] + " " + this.coordonneesCibles[i][1]); // Afficher les coordonnées de la cible
        }
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        Variable[] decisionVariables = solution.getDecisionVariables(); // Récupérer les variables de décision

        int[] listNombreCapteurParCible = new int[this.coordonneesCibles.length]; // Tableau contenant le nombre de capteurs autour de chaque cible
        Arrays.fill(listNombreCapteurParCible, 0); // Initialiser les valeurs du tableau à 0
        int nombreCapteurSansCible = 0; // Nombre de capteurs qui ne couvrent aucune cible

        for (int i = 0; i < this.nombreCapteurs; i += 2) { // Parcourir les capteurs 2 par 2 (x, y)
            double x = decisionVariables[i].getValue(); // Récupérer la valeur de la variable de décision
            double y = decisionVariables[i + 1].getValue(); // Récupérer la valeur de la variable de décision
            boolean couvreCible = false; // Indique si le capteur couvre une cible

            for (int j = 0; j < this.coordonneesCibles.length; j++) { // Parcourir les cibles pour vérifier si le capteur couvre une cible
                double distance = Math.sqrt(Math.pow(x - this.coordonneesCibles[j][0], 2) + Math.pow(y - this.coordonneesCibles[j][1], 2)); // Calculer la distance entre le capteur et la cible
                if (distance <= this.rayonCapteurs) { // Si la distance est inférieure ou égale au rayon du capteur, la cible est couverte
                    listNombreCapteurParCible[j]++; // Incrémenter le nombre de capteurs autour de la cible
                    couvreCible = true; // Indiquer que le capteur couvre une cible
                }
            }

            if (!couvreCible) { // Si le capteur ne couvre aucune cible, incrémenter le nombre de capteurs sans cible
                nombreCapteurSansCible++; // Incrémenter le nombre de capteurs sans cible
            }
        }

        int nombreCiblesCouvertes = 0; // Nombre de cibles couvertes
        int minCapteursAutourCible = Integer.MAX_VALUE; // Nombre minimum de capteurs autour d'une cible
        for (int couverture : listNombreCapteurParCible) { // Parcourir le tableau contenant le nombre de capteurs autour de chaque cible
            if (couverture > 0) { // Si la cible est couverte
                nombreCiblesCouvertes++; // Incrémenter le nombre de cibles couvertes
                if (couverture < minCapteursAutourCible) { // Si le nombre de capteurs autour de la cible est inférieur au minimum
                    minCapteursAutourCible = couverture; // Mettre à jour le minimum
                }
            }
        }

        if (nombreCiblesCouvertes == 0) { // Si aucune cible n'est couverte
            minCapteursAutourCible = 0; // Définir le minimum à 0
        }

        // Mise à jour des objectifs avec une pénalité pour les capteurs sans cible
        solution.setObjective(0, -(nombreCiblesCouvertes - nombreCapteurSansCible)); // Maximiser le nombre de cibles couvertes en tenant compte de la pénalité
        solution.setObjective(1, -minCapteursAutourCible); // Maximiser le nombre minimum de capteurs autour des cibles
    }


    public void lireFichierEtInitialiser(String cheminFichier) {
        ArrayList<int[]> listeCible = new ArrayList<>(); // Liste contenant les coordonnées des cibles

        try {
            File fichier = new File(cheminFichier); // Créer un fichier à partir du chemin
            Scanner scanner = new Scanner(fichier); // Créer un scanner à partir du fichier
            while (scanner.hasNextLine()) { // Parcourir les lignes du fichier
                String[] parts = scanner.nextLine().split(","); // Récupérer les coordonnées de la cible (x, y) séparées par une virgule
                int x = Integer.parseInt(parts[0]); // Récupérer la coordonnée x
                int y = Integer.parseInt(parts[1]); // Récupérer la coordonnée y
                listeCible.add(new int[]{x, y}); // Ajouter les coordonnées de la cible à la liste
            }
            scanner.close(); // Fermer le scanner

            this.coordonneesCibles = new int[listeCible.size()][2]; // Initialiser le tableau contenant les coordonnées des cibles
            for (int i = 0; i < listeCible.size(); i++) { // Parcourir la liste contenant les coordonnées des cibles
                this.coordonneesCibles[i] = listeCible.get(i); // Ajouter les coordonnées de la cible au tableau
            }
        } catch (FileNotFoundException e) {
            System.err.println("Fichier non trouvé: " + e.getMessage());
        }
    }
}
