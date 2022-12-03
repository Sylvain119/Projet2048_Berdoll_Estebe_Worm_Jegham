package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import static java.lang.Math.abs;
import static java.lang.Thread.State.TERMINATED;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import modele.Case;
import modele.ConnexionBDD;
import modele.Jeu;
import modele.Personne;
import static modele.Personne.recupPersonne;
import variables.Parametres;
import static variables.Parametres.BAS;
import static variables.Parametres.DESCG;
import static variables.Parametres.DROITE;
import static variables.Parametres.GAUCHE;
import static variables.Parametres.GRILLEB;
import static variables.Parametres.GRILLEH;
import static variables.Parametres.GRILLEM;
import static variables.Parametres.HAUT;
import static variables.Parametres.MONTERG;
import static variables.Parametres.TAILLE;

/**
 * FXML Controller class
 *
 * @author sylvainestebe
 */
public class FXMLController implements Initializable, Parametres {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button mouvOrdi;
    @FXML
    private MenuItem chargePartie;
    @FXML
    private MenuItem sauvegardePartie;
    @FXML
    private Button help;
    @FXML
    private Button stopIA;
    @FXML
    private GridPane grilleH;
    @FXML
    private GridPane grilleM;
    @FXML
    private Pane instructionJeu;
    @FXML
    private Pane tuile, case8, case2, case32;
    @FXML
    private Label valTuile;
    @FXML
    private GridPane grilleB;
    @FXML
    private Label meilleurScore;
    @FXML
    private MenuItem daltonien;
    @FXML
    private MenuItem dyslexique;
    @FXML
    private MenuItem classique;

    @FXML
    private MenuItem ia1;
    @FXML
    private MenuItem i2;
    @FXML
    private MenuItem i3;
    @FXML
    private MenuItem stat;

    @FXML
    private MenuItem newPartie;
    @FXML
    private MenuItem quitter;
    private ArrayList<Pane> eltsGrilles = null;

    private Jeu jeuAppli = null;
    private int nbRetour = 0;
    private boolean retourUtilise = false;
    private ArrayList<GridPane> tabGrillesApp;
    private LinkedList<Jeu> etatsPrecedents = null;

    @FXML
    private Label score;
    @FXML
    private Button boutonBDD;
    @FXML
    private Button retour;

    int compteMouv;
    @FXML
    private Menu fichier;
    @FXML
    private Label txtBDD;
    @FXML
    private ImageView haut;
    @FXML
    private ImageView bas;
    @FXML
    private ImageView gauhe;
    @FXML
    private ImageView droite;
    @FXML
    private ImageView descendre;
    @FXML
    private ImageView monter;
    int deplacementBDD;
    @FXML
    private Label chrono;
    long chronos;
    long temps;
    @FXML
    private Pane fond;
    private final int minYCase = 15, minXCaseGH = 14, longCase = 100, longGrille = 300, minXCaseGM = 353, minXCaseGB = 692;
    private int xCase = 0, yCase = 0, compteurTask = 0, nbTask = 0;
    private boolean threadDead = false;
    @FXML
    private Pane fondGrille;
    private ArrayList<Thread> threadDepl = null;
    private ArrayList<Task> deplacementCases = new ArrayList<Task>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // verification de l'existance d'une partie précedente 
        boolean existe = false;
        ObjectInputStream ois = null;
        try {
            FileInputStream fichierIn = new FileInputStream("jeu.ser");
            existe = (fichierIn != null);
        } catch (FileNotFoundException ex) {
        }
        if (existe == false) { //il n'y a pas une partie enregistrée -->  on lance une nouvelle 
            chargePartie.setDisable(true);
            sauvegardePartie.setDisable(true);
        } else {
            try {
                chargePartie.setDisable(false);
                final FileInputStream fichierIn = new FileInputStream("jeu.ser");
                ois = new ObjectInputStream(fichierIn);
                Jeu jeu = (Jeu) ois.readObject();
                jeuAppli = jeu;
            } catch (final java.io.IOException e) {
            } catch (final ClassNotFoundException e) {

            }
        }

    }

    @FXML
    public void nouvellePartie(ActionEvent event) {
        deplacementBDD = 0;
        chronos = java.lang.System.currentTimeMillis();

        jeuAppli = new Jeu();
        jeuAppli.lancementJeuAppli();
        sauvegardePartie.setDisable(false);
        this.majGrillesApp();
        //System.out.println(jeuAppli.toString());
        mouvOrdi.setDisable(false);
        nbRetour = 0;
        retourUtilise = false;
        tuile.setVisible(false);
        case8.setVisible(false);
        case2.setVisible(false);
        case32.setVisible(false);

    }

    @FXML
    private void chargerPartie(ActionEvent event) {
        chargePartie.setDisable(true);
        jeuAppli.deserialiser();
        this.majGrillesApp();
        this.sauvegardePartie.setDisable(false);
        mouvOrdi.setDisable(false);
        mouvOrdi.setDisable(false);
        nbRetour = 0;
        retourUtilise = false;
        tuile.setVisible(false);
        case8.setVisible(false);
        case2.setVisible(false);
        case32.setVisible(false);
    }

    @FXML
    private void sauvegarderPartie(ActionEvent event) {
        //Lorsqu'on sauvegarde, le bouton de chargement devient actif
        jeuAppli.serialiser();
        chargePartie.setDisable(false);
    }

    @FXML
    private void retour(MouseEvent event) {
        if (!etatsPrecedents.isEmpty()) {
            retourUtilise = true;
            etatsPrecedents.removeFirst();
            jeuAppli = etatsPrecedents.getFirst();
            this.majGrillesApp();
            this.majScoreApp();
            etatsPrecedents.removeFirst();
            nbRetour++;
            if (nbRetour == 5) {
                retour.setDisable(true);
            }
        } else {
            retour.setDisable(true);
        }
    }

    /**
     * Méthode pour afficher les 3 grilles du jeu IDEE : faire un affichage au
     * début où on initialise le tab, puis ensuite on change juste les couleurs
     * des panes et les valeurs des labels dans majGrillesApp
     */
    private void majGrillesApp() {

        tabGrillesApp = new ArrayList<GridPane>();
        tabGrillesApp.add(grilleH);
        tabGrillesApp.add(grilleM);
        tabGrillesApp.add(grilleB);
        eltsGrilles = null;
        eltsGrilles = new ArrayList<Pane>();
        fondGrille.getChildren().clear();
        //Boucle pour chaque grille
        for (int k = 0; k < TAILLE; k++) {
            for (int i = 0; i < TAILLE; i++) {
                for (int j = 0; j < TAILLE; j++) {
                    Case caseModele = jeuAppli.getGrilles().get(k).getGrille().get(i).get(j);
                    if (caseModele.getValeur() != 0) {
                        Label caseJeu = new Label("" + caseModele.getValeur());
                        caseJeu.getStyleClass().add("caseJeu");
                        Pane caseJeuCouleur = new Pane();
                        caseJeuCouleur.getChildren().add(caseJeu);
                        caseJeuCouleur.getStyleClass().add("couleurCase");
                        eltsGrilles.add(caseJeuCouleur);
                        this.couleursCases(caseJeuCouleur, caseModele.getValeur());
                        this.positionPane(caseJeuCouleur, caseModele, caseJeu, k);
                        fondGrille.getChildren().add(caseJeuCouleur);
                    }
                }
            }
        }
    }

    public void positionPane(Pane caseJeuCouleur, Case caseModele, Label caseJeu, int k) {
        if (caseModele.getGrille().getType() == GRILLEH) {
            xCase = minXCaseGH + caseModele.getY() * longCase;
        } else if (caseModele.getGrille().getType() == GRILLEM) {
            xCase = minXCaseGM + caseModele.getY() * longCase;
        } else {
            xCase = minXCaseGB + caseModele.getY() * longCase;
        }
        yCase = minYCase + caseModele.getX() * longCase;
        caseJeuCouleur.setPrefSize(100, 100);
        caseJeuCouleur.setLayoutX(xCase);
        caseJeuCouleur.setLayoutY(yCase);
        caseJeu.setVisible(true);
        caseJeuCouleur.setVisible(true);

        caseJeuCouleur.setOpacity(1);
        caseJeu.layoutXProperty().bind(caseJeuCouleur.widthProperty().subtract(caseJeu.widthProperty()).divide(2));
        tabGrillesApp.get(k).setHalignment(caseJeu, HPos.CENTER);
    }

    public void couleursCases(Pane caseJeuCouleur, int valeur) {
        if (classique.isDisable()) {
            //Gestion des couleurs des cases
            switch (valeur) {
                case 2 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFFADF;");
                case 4 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #F3E9BE;");
                case 8 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #F3C076;");
                case 16 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FD9C4C;");
                case 32 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FF7440;");
                case 64 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FF513F;");
                case 128 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFE76C;");
                case 256 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFE76C;");
                case 512 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFE76C;");
                case 1024 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFE76C;");
                case 2048 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFE76C;");
            }
        } else if (daltonien.isDisable()) {
            switch (valeur) {
                case 2 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #E0431C;");
                case 4 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #00926a;");
                case 8 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #c67bd5;");
                case 16 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFD53A;");
                case 32 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #bae1f5;");
                case 64 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FF513F;");
                case 128 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #00926a;");
                case 256 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #c67bd5;");
                case 512 ->
                    caseJeuCouleur.setStyle("-fx-background-color :#bae1f5;");
                case 1024 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #bae1f5;");
                case 2048 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #bae1f5;");
            }

        } else if (dyslexique.isDisable()) {
            switch (valeur) {
                case 2 ->
                    caseJeuCouleur.setStyle("-fx-background-color :  #00FA9A;");
                case 4 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #7FFFD4;");
                case 8 ->
                    caseJeuCouleur.setStyle("-fx-background-color :  #f2b179 ;");
                case 16 ->
                    caseJeuCouleur.setStyle("-fx-background-color :#DDA0DD;");
                case 32 ->
                    caseJeuCouleur.setStyle("-fx-background-color :  #5F9EA0;");
                case 64 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #90EE90;");
                case 128 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #5F9EA0;");
                case 256 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFE76C;");
                case 512 ->
                    caseJeuCouleur.setStyle("-fx-background-color : #FFE76C;");
                case 1024 ->
                    caseJeuCouleur.setStyle("-fx-background-color :  #90EE90;");
                case 2048 ->
                    caseJeuCouleur.setStyle("-fx-background-color :  #90EE90;");
            }
        }

    }

    @FXML
    private void affichageDaltonien(ActionEvent event) {
        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add("css/daltonien.css");
        this.dyslexique.setDisable(false);
        this.daltonien.setDisable(true);
        this.classique.setDisable(false);
        help.setVisible(true);
        help.setDisable(false);
        instructionJeu.setVisible(false);
        majGrillesApp();
    }

    @FXML
    private void affichageClassique(ActionEvent event) {
        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add("css/classique.css");
        this.dyslexique.setDisable(false);
        this.daltonien.setDisable(false);
        this.classique.setDisable(true);
        help.setVisible(true);
        help.setDisable(false);
        instructionJeu.setVisible(false);
        majGrillesApp();

    }

    @FXML
    private void affichageDyslexique(ActionEvent event) {

        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add("css/dyslexique.css");
        this.help.setDisable(true);
        this.dyslexique.setDisable(true);
        this.daltonien.setDisable(false);
        this.classique.setDisable(false);
        instructionJeu.setVisible(true);
        help.setVisible(false);
        majGrillesApp();

    }

    @FXML
    /**
     * Méthode qui affiches des aides concernant les commandes du jeu sous forme
     * d'alert *
     *
     */
    private void aidePopUp(MouseEvent event) {
        Stage fenetre_aide = new Stage();
        fenetre_aide.setTitle("Comment jouer?");
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane");

        //Titre
        Label titre_aide = new Label("Règles du jeu");
        titre_aide.getStyleClass().add("text_horsjeu");
        root.setTop(titre_aide);

        //Texte explicatif
        Pane boxText_expli = new Pane();
        boxText_expli.getStyleClass().add("boxStyle1");
        boxText_expli.setPrefWidth(400);

        Label text_expli = new Label(" Presser D pour aller à droite"
                + "\n Presser Q pour aller à gauche." + "\n Presser Z pour aller en haut."
                + "\n Presser S pour aller en bas.\n Presser R pour aller au niveau supérieur."
                + "\n Presser F pour aller au niveau inférieur.");
        text_expli.getStyleClass().add("text_horsjeu2");
        boxText_expli.getChildren().add(text_expli);
        root.setLeft(boxText_expli);
        root.setMargin(boxText_expli, new Insets(10, 10, 10, 10));
        final Scene scene = new Scene(root, 500, 200);

        if (classique.isDisable()) {
            scene.getStylesheets().add("css/classique.css");
        } else if (daltonien.isDisable()) {
            scene.getStylesheets().add("css/daltonien.css");
        }
        fenetre_aide.setScene(scene);
        fenetre_aide.show();

    }

    /**
     * Méthode qui lorsqu'on sélectionne une des ia dans le menu permet le
     * déclenchement de l'algo assoscié Également rend visible le bouton stop IA
     *
     */
    @FXML
    private void ia(ActionEvent event) {
        stopIA.setDisable(false);
    }

    @FXML
    /**
     * Méthode affiche une fenêtre du classement des joueurs
     *
     *
     */
    private void afficheStat(ActionEvent event) {
        ObservableList<Personne> listePerso = FXCollections.observableArrayList();
        listePerso = recupPersonne();

        // Création du tableau
        Stage stat = new Stage();
        TableView<Personne> table = new TableView<Personne>();
        final ObservableList<Personne> data = listePerso;
        Scene scene = new Scene(new Group());
        stat.setTitle("Statistique de ");

        final Label classement = new Label("Classement");
        classement.setFont(new Font("Arial", 20));
        table.setEditable(true);
        scene.getStylesheets().add("css/classique.css");
        TableColumn pseudoCol = new TableColumn("Pseudo");
        pseudoCol.setCellValueFactory(
                new PropertyValueFactory<Personne, String>("Pseudo"));

        TableColumn scoreCol = new TableColumn("Score");
        scoreCol.setCellValueFactory(
                new PropertyValueFactory<Personne, String>("Score"));

        TableColumn tempsCol = new TableColumn("Temps");
        tempsCol.setCellValueFactory(
                new PropertyValueFactory<Personne, String>("Temps"));

        TableColumn deplacementCol = new TableColumn("Déplacement");
        deplacementCol.setCellValueFactory(
                new PropertyValueFactory<Personne, String>("Déplacement"));

        table.getColumns().addAll(pseudoCol, scoreCol, tempsCol, deplacementCol);
        table.setItems(data);

        final VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 0, 0, 0));
        vbox.getChildren().addAll(classement, table);
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stat.setWidth(table.getWidth());
        stat.setHeight(table.getHeight());
        stat.setScene(scene);
        stat.show();
    }

    @FXML
    private void quitter(ActionEvent event) {

        Stage abandonner = new Stage();
        abandonner.setTitle("Voulez-vous vraiment quitter le jeu ? ");
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane");
        Label message = new Label("\t\t\t\t la partie n'est pas finie!"
                + "\n  Est-ce-que vous êtes sûr de vouloir quitter le jeu  ?");
        root.getStyleClass().add("message");
        root.setTop(message);
        Pane decision = new Pane();
        decision.setPrefWidth(200);
        Button oui = new Button();
        Button non = new Button();
        oui.setText("Quitter");
        non.setText("Retour");
        oui.setLayoutX(120);
        oui.setLayoutY(20);
        non.setLayoutX(220);
        non.setLayoutY(20);
        decision.getChildren().addAll(oui, non);
        root.setCenter(decision);
        root.setMargin(decision, new Insets(10, 10, 10, 10));
        final Scene scene = new Scene(root, 420, 120);
        if (classique.isDisable()) {
            scene.getStylesheets().add("css/classique.css");
        } else if (daltonien.isDisable()) {
            scene.getStylesheets().add("css/daltonien.css");
        } else if (dyslexique.isDisable()) {
            scene.getStylesheets().add("css/dyslexique.css");
        }

        abandonner.setScene(scene);
        abandonner.show();
        non.setOnAction(actionEvent -> abandonner.close());
        oui.setOnAction(actionEvent -> System.exit(0));
    }

    public void majScoreApp() {
        score.setText("" + jeuAppli.getScoreFinal());
        score.setVisible(true);
    }

    @FXML
    private void mouvJoueur(KeyEvent event) {
        deplacementBDD = deplacementBDD + 1;
        if (dyslexique.isDisable()) {
            instructionJeu.setVisible(true);
            help.setVisible(false);
        } else {
            instructionJeu.setVisible(false);
            help.setVisible(true);
        }
        String direction = event.getText();
        int dirThread = 0;
        boolean b = false;

        //Déplacement des cases selon la touche clavier
        if (direction.equals("q")) {
            b = jeuAppli.deplacerCases3G(GAUCHE);
            dirThread = GAUCHE;
        } else if (direction.equals("d")) {
            b = jeuAppli.deplacerCases3G(DROITE);
            dirThread = DROITE;
        } else if (direction.equals("z")) {
            b = jeuAppli.deplacerCases3G(HAUT);
            dirThread = HAUT;
        } else if (direction.equals("s")) {
            b = jeuAppli.deplacerCases3G(BAS);
            dirThread = BAS;
        } else if (direction.equals("f")) {
            b = jeuAppli.deplacerCases3G(DESCG);
            dirThread = DESCG;
        } else if (direction.equals("r")) {
            b = jeuAppli.deplacerCases3G(MONTERG);
            dirThread = MONTERG;
        } else {
            System.out.println("Attention, vous n'avez pas appuyé sur une touche valide!");

        }
        deplacementThread(dirThread, b);

        this.majScoreApp();
        if (jeuAppli.finJeu()) {
            if (jeuAppli.getValeurMaxJeu() >= OBJECTIF) {
                this.victoireAppli();
            } else {
                this.jeuPerduAppli();
            }
        }
        if (b) {
            etatsPrecedents = new LinkedList<Jeu>();
            etatsPrecedents = jeuAppli.enregistrement();
        }
        if (etatsPrecedents.size() >= 1 && !retourUtilise) {
            retour.setDisable(false);
        } else {
            retour.setDisable(true);
        }

    }

    public void victoireAppli() {
        Stage fenetre_aide = new Stage();
        fenetre_aide.setTitle("VICTOIRE");
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane");
        // enregistrementBDD();
        Label victoire = new Label("Félicitations, vous avez gagné!");
        Label scoreAff = new Label("Votre score : " + jeuAppli.getScoreFinal());
        victoire.getStyleClass().add("text_horsjeu");
        scoreAff.getStyleClass().add("text_horsjeu");
        root.setCenter(victoire);
        root.setTop(scoreAff);
        final Scene scene = new Scene(root, 100, 100);
        if (classique.isDisable()) {
            scene.getStylesheets().add("css/classique.css");
        } else if (daltonien.isDisable()) {
            scene.getStylesheets().add("css/daltonien.css");
        } else if (dyslexique.isDisable()) {
            scene.getStylesheets().add("css/dyslexique.css");
        }

        fenetre_aide.setScene(scene);
        fenetre_aide.show();
    }

    public void jeuPerduAppli() {
        Stage fenetre_aide = new Stage();
        fenetre_aide.setTitle("DEFAITE");
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pane");

        //Titre
        Label defaite = new Label("Mince, vous avez perdu!");
        Label scoreAff = new Label("Votre score : " + jeuAppli.getScoreFinal());
        defaite.getStyleClass().add("text_horsjeu");
        scoreAff.getStyleClass().add("text_horsjeu");
        root.setCenter(defaite);
        root.setTop(scoreAff);
        final Scene scene = new Scene(root, 100, 100);
        if (classique.isDisable()) {
            scene.getStylesheets().add("css/classique.css");
        } else if (daltonien.isDisable()) {
            scene.getStylesheets().add("css/daltonien.css");
        } else if (dyslexique.isDisable()) {
            scene.getStylesheets().add("css/dyslexique.css");
        }

        fenetre_aide.setScene(scene);
        fenetre_aide.show();
    }

    /**
     * Méthhode qui permet l'enregistrement du pseudonyme, du temps de partie,
     * du score, du nombre de déplacement du Joueur.
     *
     */
    @FXML
    private void enregistrementBDD(MouseEvent event) {
        // Récupération du temps de jeux et conversion en seconde 
        long chrono2 = java.lang.System.currentTimeMillis();
        long timeSpentPlaying = (chrono2 - chronos) / 1000;

        // Récupératon du pseudonyme avec l'obligation de rentrer au moins une lettre ou un chiffre
        TextInputDialog td = new TextInputDialog();
        td.setTitle("Pseudonyme");
        td.setHeaderText("Veuillez rentrer un pseudonyme");

        Optional<String> resultat = td.showAndWait();
        if (resultat.isPresent()) {
            while (td.getEditor().getText().isEmpty() && resultat.isPresent()) {

                Alert alert = new Alert(AlertType.WARNING);
                alert.setHeaderText("Veuillez mettre au minimum une lettre/chiffre pour le pseudonyme");
                alert.showAndWait();
                td.setTitle("Pseudonyme");
                td.setHeaderText("Veuillez rentrer un pseudonyme");
                resultat = td.showAndWait();
            }
            if (resultat.isPresent()) {
                String pseudo = td.getEditor().getText();
                // Récupération du score de la partie actuelle du joueur
                int scoreJoueurFin = Integer.valueOf(score.getText());
                // Le compteur de déplacement est situé dans la méthode mouvJoueur
                // Confirmation de l'enregistrement
                Personne.ajoutPersonne(0, pseudo, scoreJoueurFin, timeSpentPlaying, deplacementBDD);
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("Vous êtes bien enregistrer, votre score est de " + scoreJoueurFin);
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void mouvOrdiApp(MouseEvent event) {
        if (jeuAppli != null) {
            boolean b2 = jeuAppli.mouvementAlea();
            int direction = jeuAppli.getDirectionMouvAleo();
            this.deplacementThread(direction, b2);
            this.majScoreApp();
            // this.majGrillesApp();
            if (jeuAppli.finJeu()) {
                if (jeuAppli.getValeurMaxJeu() >= OBJECTIF) {
                    this.victoireAppli();
                } else {
                    this.jeuPerduAppli();
                }
            }
        }

    }

    public void deplacementThread(int direction, boolean b) {
        deplacementCases = new ArrayList<Task>();
        threadDepl = new ArrayList<Thread>();
        deplacementCases.clear();
        threadDepl.clear();
        int compteur = 0;
        if (b) {
            for (int k = 0; k < TAILLE; k++) {
                for (int i = 0; i < TAILLE; i++) {
                    for (int j = 0; j < TAILLE; j++) {
                        Case caseBouge = jeuAppli.getGrilles().get(k).getGrille().get(i).get(j);
                        if (caseBouge.getValAv() != 0) {
                            int depl = caseBouge.getNbDeplac();
                            //System.out.println("Grille"+ caseBouge.getGrilleApDepl()+"Valeur " +caseBouge.getValAv()+" xCase "+ caseBouge.getX()+"yCase"+caseBouge.getY()+"depl "+depl + "grille "+caseBouge.getGrille().getType());
                            int deplObj = 0;
                            switch (direction) {
                                case HAUT ->
                                    deplObj = minYCase + caseBouge.getX() * longCase - (abs(depl)) * longCase;
                                case BAS ->
                                    deplObj = minYCase + caseBouge.getX() * longCase + depl * longCase;
                                case GAUCHE -> {
                                    switch (caseBouge.getGrille().getType()) {
                                        case GRILLEH ->
                                            deplObj = minXCaseGH + caseBouge.getY() * longCase - (abs(depl) * longCase);

                                        case GRILLEM ->
                                            deplObj = minXCaseGM + caseBouge.getY() * longCase - (abs(depl) * longCase);
                                        case GRILLEB ->
                                            deplObj = minXCaseGB + caseBouge.getY() * longCase - (abs(depl) * longCase);
                                    }
                                }
                                case DROITE -> {
                                    switch (caseBouge.getGrille().getType()) {
                                        case GRILLEH ->
                                            deplObj = minXCaseGH + caseBouge.getY() * longCase + depl * longCase;
                                        case GRILLEM ->
                                            deplObj = minXCaseGM + caseBouge.getY() * longCase + depl * longCase;
                                        case GRILLEB ->
                                            deplObj = minXCaseGB + caseBouge.getY() * longCase + depl * longCase;
                                    }
                                }
                                case DESCG -> {
                                    if (caseBouge.getGrille().getType() == GRILLEH) {
                                        switch (caseBouge.getGrilleApDepl()) {
                                            case GRILLEH ->
                                                deplObj = minXCaseGH + caseBouge.getY() * longCase;
                                            case GRILLEM ->
                                                deplObj = minXCaseGM + caseBouge.getY() * longCase;
                                            case GRILLEB ->
                                                deplObj = minXCaseGB + caseBouge.getY() * longCase;
                                        }
                                    } else if (caseBouge.getGrille().getType() == GRILLEM) {
                                        switch (caseBouge.getGrilleApDepl()) {
                                            case GRILLEM ->
                                                deplObj = minXCaseGM + caseBouge.getY() * longCase;
                                            case GRILLEB ->
                                                deplObj = minXCaseGB + caseBouge.getY() * longCase;
                                        }
                                    } else {
                                        deplObj = minXCaseGB + caseBouge.getY() * longCase;
                                    }
                                }
                                case MONTERG -> {
                                    if (caseBouge.getGrille().getType() == GRILLEH) {
                                        deplObj = minXCaseGH + caseBouge.getY() * longCase;
                                    } else if (caseBouge.getGrille().getType() == GRILLEM) {
                                        switch (caseBouge.getGrilleApDepl()) {
                                            case GRILLEH ->
                                                deplObj = minXCaseGH + caseBouge.getY() * longCase;
                                            case GRILLEM ->
                                                deplObj = minXCaseGM + caseBouge.getY() * longCase;
                                            case GRILLEB ->
                                                deplObj = minXCaseGB + caseBouge.getY() * longCase;

                                        }
                                    } else {
                                        switch (caseBouge.getGrilleApDepl()) {
                                            case GRILLEH ->
                                                deplObj = minXCaseGH + caseBouge.getY() * longCase;
                                            case GRILLEM ->
                                                deplObj = minXCaseGM + caseBouge.getY() * longCase;
                                            case GRILLEB ->
                                                deplObj = minXCaseGB + caseBouge.getY() * longCase;

                                        }
                                    }
                                }
                                default -> {
                                }
                            }
                            xCase = (int) eltsGrilles.get(compteur).getLayoutX();
                            yCase = (int) eltsGrilles.get(compteur).getLayoutY();
                            //System.out.println(deplObj+" xCase"+caseBouge.getX()+"yCase"+caseBouge.getY());
                            Pane caseABouge = eltsGrilles.get(compteur);
                            DeplacementTask d = new DeplacementTask(xCase, yCase, deplObj, caseABouge, direction, this);
                            deplacementCases.add(d);
                            compteur++;
                        }
                    }
                }
            }
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch doneSignal = new CountDownLatch(deplacementCases.size());
            for (int i = 0; i < deplacementCases.size(); i++) {
                DeplacementTask d = (DeplacementTask) deplacementCases.get(i);
                d.setDebut(startSignal);
                d.setFin(doneSignal);
                Thread th = new Thread(deplacementCases.get(i)); // on crée un contrôleur de Thread
                threadDepl.add(th);
                th.setDaemon(true); // le Thread s'exécutera en arrière-plan (démon informatique)
                th.start();
            }
            jeuAppli.choixNbCasesAjout(b);
            startSignal.countDown();

        }
    }

    public Jeu getJeuAppli() {
        return this.jeuAppli;
    }

    public Pane getFondGrille() {
        return this.fondGrille;
    }

    public ArrayList<Pane> getEltsGrilles() {
        return this.eltsGrilles;
    }

    public void setEltsGrilles(ArrayList<Pane> l) {
        this.eltsGrilles = l;
    }

    public void setFonfGrille(Pane p) {
        fondGrille = p;
    }

    public void resetFondGrille() {
        fondGrille.getChildren().clear();
    }

    public void resetEltsGrilles() {
        eltsGrilles.clear();
    }

    public ArrayList<Thread> getDeplThread() {
        return threadDepl;
    }

}
