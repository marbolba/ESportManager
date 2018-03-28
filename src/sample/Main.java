/**
 * @author Marcin Bołba
 * @version v1.0
 * @title ESport Manager
 * */
package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.WebScrapper;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ListIterator;

import static java.awt.SystemColor.control;
import static java.awt.SystemColor.controlShadow;
import static java.lang.Thread.sleep;
import static javafx.application.Application.launch;
import static javafx.geometry.Pos.BASELINE_RIGHT;
import static javafx.geometry.Pos.BOTTOM_CENTER;
import static javafx.geometry.Pos.TOP_CENTER;

public class Main extends Application {
    Stage window;
    Scene mainScene;
    Button blogin;
    PostgreSQLJDBC postgres;
    Player me;
    AlertBox alert;
    Label konto, logout, rejestracja, budzet;

    ChoiceBox<Object> druzyna,zakupZawodnika,gra;
    TableView<Druzyna> table;
    TableView<Zawodnik> tableZ,tableZ2;
    TableView<Turniej4> tableT;
    TableView<Scoreboard> tableS;
    TableView<Mecz> tableM;
    String[] tab;
    BorderPane szkielet;
    HBox topHbox,money,playerData,nullHbox;
    VBox napisy, center;
    ImageView coinIcon;
    Image coinJpg;
    Region spacing;

    sample.JschExecutor2 sesjaSSH;

    public static void main(String args[]) {
        launch(args);
    }
    /**
     * Metoda start służy uruchomieniu graficznego hud gry
     * @param primaryStage scena na jakies wyswietlana bedzie aplikacja
     * */
    public void start(Stage primaryStage) throws Exception
    {
        window = primaryStage;
        window.setTitle("Manager");

        initConnection();
        initMain();

        loginView();
        if(me==null)
        {
            System.out.println("nikogo nie zalogowano");
            wait(1000);
            window.close();
        }
        topButtonBar();

        window.setOnCloseRequest(e -> {
            if (ConfirmBox.alert("Zamykanie", "Czy na pewno chcesz zamknac?")) {
                postgres.dc();
                sesjaSSH.dc();
                window.close();
            } else
                e.consume();
        });
    }
    /**
     * Metoda słuzaca inicjalizacji podstawowych zmiennych aplikacji
     * */
    public void initMain()
    {
        szkielet = new BorderPane();
//górny pasek wyboru
        druzyna = new ChoiceBox<>();
        zakupZawodnika = new ChoiceBox<>();
        gra = new ChoiceBox<>();
        nullHbox=new HBox(10);

        //druzyna
        druzyna.getItems().add("Drużyna");
        druzyna.getItems().add("Zarzadzaj druzyna");
        druzyna.getItems().add("Lawka rezerwowych");
        druzyna.setValue("Drużyna");
        druzyna.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if(newValue!="Drużyna")
            {

            if(newValue=="Zarzadzaj druzyna")
                openTeamManagement();
            else if(newValue=="Lawka rezerwowych")
                openBench();
            else
                openTeamView(newValue.toString());

                zakupZawodnika.setValue("Zawodnicy");
                gra.setValue("Graj");
            }
        });
        // bank i rynek
        zakupZawodnika.getItems().add("Zawodnicy");
        zakupZawodnika.getItems().add("Bank");
        zakupZawodnika.getItems().add("Rynek transferowy");
        zakupZawodnika.setValue("Zawodnicy");
        zakupZawodnika.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if(newValue!="Zawodnicy")
            {
            if(newValue=="Bank")
                openBank();
            else if(newValue=="Rynek transferowy")
                openMarket();

                druzyna.setValue("Drużyna");
                gra.setValue("Graj");
            }

        });
        //turniej i mecz
        gra.getItems().add("Graj");
        gra.getItems().add("Turniej");
        gra.getItems().add("Twoje mecze");
        gra.setValue("Graj");
        gra.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if(newValue!="Graj")
            {
                if (newValue == "Turniej")
                    openTournamentView();
                else if (newValue == "Twoje mecze")
                    openMyMatches();

                zakupZawodnika.setValue("Zawodnicy");
                druzyna.setValue("Drużyna");
            }
        });


        konto = new Label("");
        logout = new Label("");
        budzet = new Label("");

        coinIcon = new ImageView();
        money = new HBox(3);

        playerData = new HBox(5);
        napisy = new VBox(5);
        spacing = new Region();
        spacing.setMinWidth(200);
        spacing.setMaxWidth(300);



        try {
            coinJpg = new Image("file:img/coin.jpg");
        }catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
        }

        coinIcon.setImage(coinJpg);
        money.getChildren().addAll(coinIcon,budzet);
        napisy.getChildren().addAll(konto,logout);
        playerData.getChildren().addAll(napisy,money);
        logout.setOnMouseClicked(e -> {
            updateNoLogon();
        });

        //playerData.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: black;");
    }
    /**
     *  Metoda sluzaca inicjalizacji górnego paska narzedzi
     * */
    public void topButtonBar() {
        topHbox = new HBox(20);
        topHbox.setPadding(new Insets(10, 10, 10, 10));
        topHbox.getChildren().add(druzyna);
        topHbox.getChildren().add(zakupZawodnika);
        topHbox.getChildren().add(gra);
        topHbox.getChildren().add(spacing);
        topHbox.getChildren().add(playerData);

        szkielet.setTop(topHbox);
        szkielet.getTop().setStyle("-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #ff9900;");

        mainScene = new Scene(szkielet, 800, 600);
        window.setScene(mainScene);
        window.show();
    }
    /**
     *  Metoda sluzaca odswieżeniu górnego paska narzedzi dla zalogowanego uzytkownika
     * */
    public void updateLogin()
    {
        if(druzyna.getItems().size()>3)
            druzyna.getItems().remove(3,druzyna.getItems().size());
        String all = postgres.getTeams(me.id);
        if(all!="")
        {
            tab = all.split(",");
            for (int i = 0; i < tab.length; i++) {
                if (i % 3 == 0)
                    druzyna.getItems().add(tab[i]);
            }
        }
        konto.setText(me.name);
        budzet.setText(""+postgres.getMyMoney(me.id));
        logout.setText("Wyloguj");

        napisy.getChildren().removeAll(konto, logout);
        napisy.getChildren().addAll(konto, logout);
    }
    /**
     *  Metoda sluzaca odswieżeniu górnego paska narzedzi dla niezalogowanego uzytkownika
     * */
    public void updateNoLogon()
    {
        me = null;
        druzyna.setValue("Drużyna");
        zakupZawodnika.setValue("Zawodnicy");
        gra.setValue("Graj");
        window.close();
        loginView();
    }
    /**
     *  Metoda sluzaca nawiazaniu polaczenia ssh oraz podlaczeniu do bazy PostereSQL
     * */
    public void initConnection() {
        //init conn
        postgres = new PostgreSQLJDBC();
        sesjaSSH = new sample.JschExecutor2();
        try {
            sesjaSSH.go4pascal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        postgres.getConnection();
        //--init conn
    }
    /**
     * Metoda wyswietlajaca pozadana tabele zawodnikow
     * */
    public void displayPlayerTable(String tableType)
    {
        final ProgressBar pb = new ProgressBar();
        pb.setProgress(0.5f);
        TableColumn<Zawodnik, String> nameColumn = new TableColumn<>("Nick");
        nameColumn.setMinWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        TableColumn<Zawodnik, String> teamColumn = new TableColumn<>("Druzna");
        teamColumn.setMinWidth(100);
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("org_druzyna"));
        TableColumn<Zawodnik, Integer> ratingColumn = new TableColumn<>("Ocena");
        ratingColumn.setMinWidth(100);
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("ocena"));
        TableColumn<Zawodnik, Double> progressCol = new TableColumn("Forma");
        progressCol.setCellFactory(ProgressBarTableCell.<Zawodnik> forTableColumn());
        progressCol.setCellValueFactory(new PropertyValueFactory("formaPasek"));
        TableColumn<Zawodnik, Integer> priceColumn = new TableColumn<>("Cena");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("cena"));

        tableZ = new TableView<>();
        tableZ.getColumns().addAll(nameColumn, teamColumn, ratingColumn,progressCol, priceColumn);
        if(tableType.equals("bank"))
            tableZ.setItems(getBankPlayers());
        else if(tableType.equals("druzyna"))
            tableZ.setItems(getTeammates());
        else if(tableType.equals("lawka"))
            tableZ.setItems(getBenchedPlayers());
        //else if(tableType.equals(""))
    }
    /**
     *  Metoda pozwalajaca na dokonwyanie operacji wewnatrz druzyny
     *  @param teamName nazwa druzyny do edycji
     * */
    public void openTeamView(String teamName)
    {
        me.currTeam = postgres.getTeamInfo(teamName,me.id);

        Label labelTeam = new Label(teamName);
        labelTeam.setFont(new Font("Arial",30.0));

        Label rating =new Label("Rating: "+Integer.toString(me.currTeam.ocena));
        rating.setFont(new Font("Arial",26.0));

        Button remove = new Button("Wyslij na lawkę rezerwowych");
        remove.setOnAction(e->removePlayerFromTeam());

        displayPlayerTable("druzyna");

        HBox pres=new HBox(450);
        pres.getChildren().addAll(labelTeam,rating);
        center = new VBox(10);
        center.getChildren().addAll(pres,remove, tableZ);

        szkielet.setCenter(center);//tak to sie powinno konczyc
        szkielet.getCenter().setStyle("-fx-padding: 10;"+"-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #f2f2f2;");
    }
    /**
     *  Metoda sluzaca usuwaniu zawodnika z druzyny
     * */
    public void removePlayerFromTeam()
    {
        Zawodnik selectedRecord =(Zawodnik)tableZ.getSelectionModel().getSelectedItems().get(0);
        postgres.removePlayerFromTeam(selectedRecord.id_karta);

        try {
            ObservableList<Zawodnik> allProduct, selectedProduct;
            allProduct = tableZ.getItems();
            selectedProduct = tableZ.getSelectionModel().getSelectedItems();
            selectedProduct.forEach(allProduct::remove);
        }catch (java.util.NoSuchElementException a)
        {
            System.out.println("ostatni element");
        }
    }
    /**
     *  Metoda pozwalajaca Wstawia zawodników do widoku druzyny
     *  @return  lista obiektów do wstawienia do tabelki
     * */
    public ObservableList<Zawodnik> getTeammates() //potrzebne do setItems()
    {
        ObservableList<Zawodnik> kolekcja = FXCollections.observableArrayList();
        Zawodnik[] tabPlayers= null;
        tabPlayers=postgres.getTeamPlayers(me.currTeam.id_druzyna);
        for(int i=0;i<tabPlayers.length;i++)
        {
            kolekcja.add(tabPlayers[i]);
        }
        return kolekcja;
    }
    /**
     *  Metoda pozwalajaca wyswietlic zawodnikow danej druzyny
     * */
    public ObservableList<Zawodnik> getTeammatesOfTeam(Druzyna d) //potrzebne do setItems()
    {
        ObservableList<Zawodnik> team = FXCollections.observableArrayList();
        team.add(d.zawodnicy[0]);
        team.add(d.zawodnicy[1]);
        team.add(d.zawodnicy[2]);
        team.add(d.zawodnicy[3]);
        team.add(d.zawodnicy[4]);
        return team;
    }
    /**
     *  Metoda sluzaca otwarciu widoku lawki rezerwowych
     * */
    public void openBench()
    {
        Label z = new Label("Ławka rezerwowych");
        z.setFont(new Font("Arial",30.0));
        Button przypisanie=new Button("Przypisz zawodnika");
        Button qsell=new Button("Szybka sprzedaż");
        Button makeOffer=new Button("Wystaw na rynku");
        HBox buttons=new HBox(10);
        buttons.getChildren().addAll(przypisanie,qsell,makeOffer);

        displayPlayerTable("lawka");
        tableZ.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableZ.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                przypisanie.setOnAction(e ->openAssignmentView());
                qsell.setOnAction(f-> {
                    ConfirmBox conf=new ConfirmBox();
                    if(conf.alert("Potwierdzenie","Czy chcesz sprzedac zawodnika za polowe ceny?"))
                        quickSellPlayer();
                });
                makeOffer.setOnAction(g->{
                    addOffer();
                    openBench();
                });
            }
        });


        center = new VBox(10);
        center.getChildren().addAll(z,buttons, tableZ);

        szkielet.setCenter(center);//tak to sie powinno konczyc
        szkielet.getCenter().setStyle("-fx-padding: 10;"+"-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #f2f2f2;");

    }
    /**
     *  Metoda sluzaca otwarciu widoku przypisania zawodnika do danej druzyny
     * */
    public void openAssignmentView()
    {
        Stage assign =new Stage();

        assign.initModality(Modality.APPLICATION_MODAL);//okienko pierwszego priorytetu
        assign.setTitle("Przypisanie");
        assign.setMinWidth(250);
        assign.setMinHeight(100);

        Button b=new Button("Przypisz");

        Label mes=new Label("Wybierz druzyne do której przypisany bedzie zawodnik");
        //tabelka
        TableColumn<Druzyna, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        TableColumn<Druzyna, Integer> teamColumn = new TableColumn<>("Ocena");
        teamColumn.setMinWidth(100);
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("ocena"));

        table = new TableView<>();
        table.setItems(getTeams());
        table.getColumns().addAll(nameColumn, teamColumn);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                b.setOnAction(e -> {
                    assingPlayer();
                    assign.close();
                });
            }
        });


        VBox label1=new VBox(10);
        label1.getChildren().addAll(mes,table,b);
        label1.setAlignment(Pos.CENTER);

        Scene s=new Scene(label1);

        assign.setScene(s);
        assign.showAndWait();
    }
    /**
     *  Metoda wykonujaca operacje dodawania zawodnika do danej druzyny
     * */
    public void assingPlayer()
    {
        Druzyna selectedRecord =(Druzyna)table.getSelectionModel().getSelectedItems().get(0);

        //obsluga wielokrotnego zaznaczenia
        ObservableList<Zawodnik> selectedItems = tableZ.getSelectionModel().getSelectedItems();
        System.out.println(selectedItems);

        // TEST
        ArrayList<Zawodnik> selectedIDs = new ArrayList<Zawodnik>();
        for (Zawodnik row : selectedItems) {
            //System.out.println(row.nazwa);
            selectedIDs.add(row);
        }

        ListIterator<Zawodnik> iterator = selectedIDs.listIterator();
        while (iterator.hasNext()) {                                            //for each selected player do:
            Zawodnik z=iterator.next();
            System.out.println(z.id_zawodnik);
            if(postgres.checkPlayers(selectedRecord.id_druzyna,z.id_zawodnik))
            {
                if(postgres.getTeamSize(selectedRecord.id_druzyna)<5)
                {
                    //usuniecie z listy
                    try{
                        ObservableList<Zawodnik> allProduct,selectedProduct;
                        allProduct=tableZ.getItems();
                        selectedProduct=tableZ.getSelectionModel().getSelectedItems();
                        selectedProduct.forEach(allProduct::remove);
                    }catch (java.util.NoSuchElementException a)
                    {
                        System.out.println("ostatni element");
                    }
                }
                else
                {
                    AlertBox a=new AlertBox();
                    a.alert("Nie mozesz dodac zawodnika","Maksymalna ilosc zawodników w drużynie to 5");

                }
                //czesc postgresowa
                postgres.addPlayerToTeam(z.id_karta,selectedRecord.id_druzyna);
            }
            else
            {
                AlertBox a=new AlertBox();
                a.alert("Nie mozesz dodac zawodnika","Ten zawodnik juz jest w druzynie");
            }
        }
        openBench();
        table.getSelectionModel().select(null);

    //\new
    }
    /**
     *  Metoda otwierajaca widok druzyn i pozwalajaca na dokonwyanie operacji na druzynach takich jak dodawanie i usuwanie druzyny
     * */
    public void openTeamManagement()
    {
        //tabelka

        Label z = new Label("Zarzadzanie druzynami");
        z.setFont(new Font("Arial",30.0));
        //dodawanie i usuwanie - labelki i przyciski
        Button newTeamButton = new Button("Zaloz nowa druzyne");
        newTeamButton.setOnAction(e -> {
            createTeamView();
        });
        Button deleteThis =new Button("Delete");

        TableColumn<Druzyna, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        TableColumn<Druzyna, Integer> teamColumn = new TableColumn<>("Ocena");
        teamColumn.setMinWidth(100);
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("ocena"));

        table = new TableView<>();
        table.setItems(getTeams());
        table.getColumns().addAll(nameColumn, teamColumn);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                deleteThis.setOnAction(e -> deleteValue());
            }
        });
        table.setOnMousePressed(new EventHandler<MouseEvent>() {    //double click on selected team
            @Override
            public void handle(MouseEvent event)
            {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2)
                {
                    Druzyna selectedRecord =(Druzyna)table.getSelectionModel().getSelectedItems().get(0);
                    openTeamView(selectedRecord.nazwa);
                    druzyna.setValue(selectedRecord.nazwa);

                }
            }
        });

        //ogranizacja guzikow
        HBox guziki =new HBox(10);
        guziki.getChildren().addAll(newTeamButton,deleteThis);

        center = new VBox(10);
        center.getChildren().addAll(z,guziki, table);

        szkielet.setCenter(center);
        szkielet.getCenter().setStyle("-fx-padding: 10;"+"-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #f2f2f2;");
    }
    /**
     *  Metoda pozwalajaca Wstawia druzyn do widoku wszystkich druzyn
     *  @return  lista obiektów do wstawienia do tabelki
     * */
    public ObservableList<Druzyna> getTeams() //potrzebne do setItems()
    {
        ObservableList<Druzyna> team = FXCollections.observableArrayList();

        String all = postgres.getTeams(me.id);
        System.out.println(all);
        String name="";
        int ocena=0;
        int id_d=0;
        tab = all.split(",");
        for (int i = 0; i < tab.length; i++)
        {
            if (i % 3 == 0)
                name=tab[i];
            else if(i % 3 == 1)
                ocena=Integer.parseInt(tab[i]);
            else if(i % 3 == 2)
            {
                id_d=Integer.parseInt(tab[i]);
                System.out.println(name+" "+ocena+" "+id_d);
                Druzyna a = new Druzyna(name,ocena,id_d);
                team.add(a);
            }
        }
        return team;
    }
    /**
     *  Metoda wykonujaca operacje usuwania druzyny z listy druzyn
     * */
    public void deleteValue()
    {
        Druzyna selectedRecord =(Druzyna)table.getSelectionModel().getSelectedItems().get(0);
        //System.out.println("Proba usuniecia: "+selectedRecord.getId_druzyna());
        if(postgres.getTeamSize(selectedRecord.id_druzyna)==0)
            postgres.removeTeam(selectedRecord.getId_druzyna());
        else
        {
            postgres.replacePlayers(selectedRecord);
            postgres.removeTeam(selectedRecord.getId_druzyna());
        }

        //usuniecie z listy
        try {
            ObservableList<Druzyna> allProduct, selectedProduct;
            allProduct = table.getItems();
            selectedProduct = table.getSelectionModel().getSelectedItems();
            selectedProduct.forEach(allProduct::remove);
        }catch (java.util.NoSuchElementException a)
        {
            System.out.println("ostatni element");
        }

        //odswiezenie tabelki
        updateLogin();
    }
    /**
     *  Metoda otwierajaca okno tworzenia druzyny
     * */
    public void createTeamView()
    {
        Stage slog = new Stage();
        slog.setTitle("Zakladanie nowej druzyny");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label llogin = new Label("Nazwa nowej druzyny:");
        grid.add(llogin, 0, 0);
        TextField loginInput = new TextField("");
        grid.add(loginInput, 0, 1);
        //button
        Button buttonLogin = new Button("Dodaj");
        grid.add(buttonLogin, 1, 2);

        buttonLogin.setOnAction(f -> {
            if(loginInput.getText()!="")
            {
                if(postgres.insertTeam(me.id, loginInput.getText()))
                {
                    updateLogin();
                    openTeamManagement();
                    slog.close();
                }
                else
                {
                    AlertBox alert=new AlertBox();
                    alert.alert("Nie utworzono druzyn","Druzyna o podanej nazwie juz istnieje");
                    slog.close();
                }
            }
            else
            {
                AlertBox alert=new AlertBox();
                alert.alert("Nie utworzono druzyn","Podaj jakas nazwe");
        }
        });
        Scene log = new Scene(grid);
        slog.setScene(log);
        slog.showAndWait();
    }
    /**
     *  Metoda otwierajaca okno logowania do konta
     * */
    public void loginView()
    {
        Stage slog = new Stage();
        slog.setOnCloseRequest(p -> {
            System.out.println("zamykam bez logu");
        });
        slog.setTitle("Logowanie");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        //Login
        Label llogin = new Label("login");
        grid.add(llogin, 0, 0);
        TextField loginInput = new TextField("");
        grid.add(loginInput, 1, 0);
        Label lpassword = new Label("password");
        grid.add(lpassword, 0, 1);
        TextField passwordInput = new TextField("");
        grid.add(passwordInput, 1, 1);
        //button
        Button buttonLogin = new Button("Loguj");
        grid.add(buttonLogin, 1, 2);
        buttonLogin.setOnAction(f -> {
            me = postgres.loginPlayer(loginInput.getText(), passwordInput.getText());
            if (me != null) {
                updateLogin();

                druzyna.setValue("Drużyna");
                zakupZawodnika.setValue("Zawodnicy");
                gra.setValue("Graj");
                szkielet.setCenter(nullHbox);
                window.show();
                slog.close();
            } else {
                loginInput.setStyle("-fx-control-inner-background: #ff6666");
                passwordInput.setStyle("-fx-control-inner-background: #ff6666");
                loginInput.clear();
                passwordInput.clear();
            }
        });
        //register
        rejestracja = new Label("Nie masz jeszcze konta? Załóż je!");
        rejestracja.setOnMouseClicked(e -> {
            registerView();
            slog.close();
        });
        grid.add(rejestracja,1,3);

        Scene log = new Scene(grid);
        slog.setScene(log);
        slog.showAndWait();

    }
    /**
     *  Metoda otwierajaca okno tworzenia konta
     * */
    public void registerView()
    {
        Stage slog = new Stage();
        slog.setTitle("Rejestracja");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        //Rejestracja
        Label llogin = new Label("login");
        grid.add(llogin, 0, 0);
        TextField loginInput = new TextField("");
        grid.add(loginInput, 1, 0);
        Label lpassword = new Label("password");
        grid.add(lpassword, 0, 1);
        TextField passwordInput = new TextField("");
        grid.add(passwordInput, 1, 1);
        //button
        Button buttonLogin = new Button("Zarejestruj");
        grid.add(buttonLogin, 1, 2);
        buttonLogin.setOnAction(f -> {
            AlertBox a = new AlertBox();
            if(postgres.insertPlayer(loginInput.getText(), passwordInput.getText()))
            {
                me = postgres.loginPlayer(loginInput.getText(), passwordInput.getText());


                slog.close();
                if (me != null) {
                    a.alert("Rejestracja pomyslna!", "Utworzono konto pomyślnie");
                    updateLogin();

                    druzyna.setValue("Drużyna");
                    zakupZawodnika.setValue("Zawodnicy");
                    gra.setValue("Graj");
                    szkielet.setCenter(nullHbox);
                    window.show();
                    slog.close();
                } else
                    a.alert("Rejestracja niepomyslna!", "Nie udało sie utworzyc konta");
            }
            else
                a.alert("Rejestracja niepomyslna!", "Istnieje juz konto o takiej nazwie");
        });
        Scene log = new Scene(grid);
        slog.setScene(log);
        slog.showAndWait();
    }
    /**
     *  Metoda otwierajaca widok banku
     * */
    public void openBank()
    {
        Label labelTeam = new Label("Bank");
        labelTeam.setFont(new Font("Arial",30.0));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(7);
        grid.setHgap(3);

        //queries
        TextField nick = new TextField("");
        nick.setPromptText("Nick");
        grid.add(nick, 0, 0);
        TextField team = new TextField("");
        team.setPromptText("Team");
        grid.add(team, 1, 0);
        TextField cenaL = new TextField("");
        cenaL.setPromptText("Cena od");
        grid.add(cenaL, 0, 1);
        TextField cenaH = new TextField("");
        cenaH.setPromptText("Cena do");
        grid.add(cenaH, 1, 1);
        TextField ocenaL = new TextField("");
        ocenaL.setPromptText("Ocena od");
        grid.add(ocenaL, 0, 2);
        TextField ocenaH = new TextField("");
        ocenaH.setPromptText("Ocena do");
        grid.add(ocenaH, 1, 2);
        Button specQuery =new Button("Precyzuj wyszukiwanie");
        grid.add(specQuery, 2, 2);
        Button buyThis =new Button("Kup ");
        grid.add(buyThis, 8, 2);

        HBox pasek=new HBox(10);
        pasek.getChildren().addAll(labelTeam,grid);

        displayPlayerTable("bank");

        tableZ.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                buyThis.setOnAction(f -> buyThisPlayer());
            }
        });

        specQuery.setOnAction(g -> {
            //if (!cenaL.getText().isEmpty()||!cenaH.getText().isEmpty()||!ocenaL.getText().isEmpty()||!ocenaH.getText().isEmpty())
            {
                int sumaD=0;
                int sumaG=0;
                if (!cenaL.getText().isEmpty())
                {
                    sumaD++;        //suma wszystkich wpisow
                    if(cenaL.getText().matches("[0-9]*"))
                        sumaG++;    //suma poprawnych wpisow
                }
                if (!cenaH.getText().isEmpty())
                {
                    sumaD++;
                    if(cenaH.getText().matches("[0-9]*"))
                        sumaG++;
                }
                if (!ocenaL.getText().isEmpty())
                {
                    sumaD++;
                    if(ocenaL.getText().matches("[0-9]*"))
                        sumaG++;
                }
                if (!ocenaH.getText().isEmpty())
                {
                    sumaD++;
                    if(ocenaH.getText().matches("[0-9]*"))
                        sumaG++;
                }
                if(sumaD==sumaG)
                    tableZ.setItems(updateBankPlayers(cenaL.getText(),cenaH.getText(),ocenaL.getText(),ocenaH.getText(),nick.getText(),team.getText()));
                else
                {
                    AlertBox a=new AlertBox();
                    a.alert("Prosze podac jedynie liczby","Prosze podac jedynie liczby");
                }
            }
        });

        center = new VBox(10);
        center.getChildren().addAll(pasek, tableZ);

        szkielet.setCenter(center);
        szkielet.getCenter().setStyle("-fx-padding: 10;"+"-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #f2f2f2;");
    }
    /**
     *  Metoda pozwalajaca wyswietlenie zawodników z banku
     * */
    public ObservableList<Zawodnik> updateBankPlayers(String cL,String cH,String oL,String oH,String nick,String team)
    {
        ObservableList<Zawodnik> kolekcja = FXCollections.observableArrayList();
        Zawodnik[] tabPlayers= null;
        tabPlayers=postgres.getQueriedPlayers(cL,cH,oL,oH,nick,team);
        for(int i=0;i<tabPlayers.length;i++)
        {
            kolekcja.add(tabPlayers[i]);
        }
        return kolekcja;
    }
    /**
     *  Metoda pozwalajaca wyswietlenie zawodników z banku
     * */
    public ObservableList<Zawodnik> getBankPlayers()
    {
        ObservableList<Zawodnik> kolekcja = FXCollections.observableArrayList();
        Zawodnik[] tabPlayers= null;
        tabPlayers=postgres.getAllPlayers();
        for(int i=0;i<tabPlayers.length;i++)
        {
            kolekcja.add(tabPlayers[i]);
        }
        return kolekcja;
    }
    /**
     *  Metoda pozwalajaca wyswietlenie zawodnikow na lawce rezerwowych
     * */
    public ObservableList<Zawodnik> getBenchedPlayers()
    {
        ObservableList<Zawodnik> kolekcja = FXCollections.observableArrayList();
        Zawodnik[] tabPlayers= null;
        tabPlayers=postgres.getBenchedPlayers(me.id);
        for(int i=0;i<tabPlayers.length;i++)
        {
            kolekcja.add(tabPlayers[i]);
        }
        return kolekcja;
    }
    /**
     *  Metoda pozwalajaca wyswietlenie zawodnikow na liscie transferowej
     * */
    public ObservableList<Zawodnik> getMarketPlayers()
    {
        ObservableList<Zawodnik> kolekcja = FXCollections.observableArrayList();
        Zawodnik[] tabPlayers= null;
        tabPlayers=postgres.getMarketOffers(me.id);
        for(int i=0;i<tabPlayers.length;i++)
        {
            kolekcja.add(tabPlayers[i]);
        }
        return kolekcja;
    }
    /**
     *  Metoda pozwalajaca wyswietlenie wlasnych ofert transferowych
     * */
    public ObservableList<Zawodnik> getMyMarket()
    {
        ObservableList<Zawodnik> kolekcja = FXCollections.observableArrayList();
        Zawodnik[] tabPlayers= null;
        tabPlayers=postgres.getMyMarketOffers(me.id);
        for(int i=0;i<tabPlayers.length;i++)
        {
            kolekcja.add(tabPlayers[i]);
        }
        return kolekcja;
    }
    /**
     *  Metoda pozwalajaca wyswietlenie wszystkich dostepnych turniejów
     * */
    public ObservableList<Turniej4> getAllTournaments()
    {
        ObservableList<Turniej4> kolekcja = FXCollections.observableArrayList();
        Turniej4[] tabTruniej4= null;
        tabTruniej4=postgres.getAllTournaments();
        for(int i=0;i<tabTruniej4.length;i++)
        {
            kolekcja.add(tabTruniej4[i]);
        }
        return kolekcja;
    }
    /**
     *  Metoda pozwalajaca dokonanie zakupu wybranego zawodnika z banku
     * */
    public void buyThisPlayer()
    {
        Zawodnik selectedRecord =(Zawodnik)tableZ.getSelectionModel().getSelectedItems().get(0);

        AlertBox a=new AlertBox();
        if(me.budzet>=selectedRecord.cena)
        {
            me.budzet-=selectedRecord.cena;
            postgres.addPlayer(selectedRecord.id_zawodnik,me.id,me.budzet);
            updateLogin();
            a.alert("Zakup pomyslny","Zakupiono zawodnika pomyslnie");
        }
        else
            a.alert("Zakup niepomyslny","Nie stać ciebie na zakup tego zawodnika");
    }
    /**
     *  Metoda pozwalajaca dokonanie zakupu wybranego zawodnika z rynku transferowego
     * */
    public void buyThisPlayerFromMarket()
    {
        Zawodnik selectedRecord =(Zawodnik)tableZ.getSelectionModel().getSelectedItems().get(0);

        AlertBox a=new AlertBox();
        if(me.budzet>=selectedRecord.cena)
        {
            me.budzet-=selectedRecord.cena;
            postgres.acceptBuyOffer(selectedRecord.id_karta,me.id,me.budzet,selectedRecord.cena);
            updateLogin();
            a.alert("Zakup pomyslny","Zakupiono zawodnika pomyslnie");
        }
        else
            a.alert("Zakup niepomyslny","Nie stać ciebie na zakup tego zawodnika");

        try {
            ObservableList<Zawodnik> allProduct, selectedProduct;
            allProduct = tableZ.getItems();
            selectedProduct = tableZ.getSelectionModel().getSelectedItems();
            selectedProduct.forEach(allProduct::remove);
        }catch (java.util.NoSuchElementException b)
        {
            System.out.println("ostatni element");
        }
    }
    /**
     *  Metoda pozwalajaca otwarcie widoku rynku transferowego
     * */
    public void openMarket()
    {
        //oferty innych
        Label labelTeam = new Label("Rynek transferowy");
        labelTeam.setFont(new Font("Arial",30.0));

        Button buyThis =new Button("Kup");
        HBox rynbuy=new HBox(10);
        rynbuy.getChildren().addAll(labelTeam,buyThis);

        TableColumn<Zawodnik, String> nameColumn = new TableColumn<>("Nick");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        TableColumn<Zawodnik, Integer> ratingColumn = new TableColumn<>("Ocena");
        ratingColumn.setMinWidth(100);
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("ocena"));
        TableColumn<Zawodnik, Integer> ageColumn = new TableColumn<>("Wiek");
        ageColumn.setMinWidth(100);
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("wiek"));
        TableColumn<Zawodnik, String> teamColumn = new TableColumn<>("Oferta gracza");
        teamColumn.setMinWidth(100);
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("org_druzyna"));
        TableColumn<Zawodnik, Integer> priceColumn = new TableColumn<>("Cena");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("cena"));


        tableZ = new TableView<>();
        tableZ.setItems(getMarketPlayers());
        tableZ.getColumns().addAll(nameColumn, ratingColumn, ageColumn, teamColumn, priceColumn);
        tableZ.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                buyThis.setOnAction(f -> buyThisPlayerFromMarket());
            }
        });

        Label myOffers = new Label("Moje oferty transferowe");
        myOffers.setFont(new Font("Arial",30.0));
        Button delOff = new Button("Usun oferte");
        HBox myoffman=new HBox(10);
        myoffman.getChildren().addAll(myOffers,delOff);

        //moje oferty
        TableColumn<Zawodnik, String> nameColumn2 = new TableColumn<>("Nick");
        nameColumn2.setMinWidth(200);
        nameColumn2.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        TableColumn<Zawodnik, Integer> ratingColumn2 = new TableColumn<>("Ocena");
        ratingColumn2.setMinWidth(100);
        ratingColumn2.setCellValueFactory(new PropertyValueFactory<>("ocena"));
        TableColumn<Zawodnik, Integer> ageColumn2 = new TableColumn<>("Wiek");
        ageColumn2.setMinWidth(100);
        ageColumn2.setCellValueFactory(new PropertyValueFactory<>("wiek"));
        TableColumn<Zawodnik, String> teamColumn2 = new TableColumn<>("Oferta gracza");
        teamColumn2.setMinWidth(100);
        teamColumn2.setCellValueFactory(new PropertyValueFactory<>("org_druzyna"));
        TableColumn<Zawodnik, Integer> priceColumn2 = new TableColumn<>("Cena");
        priceColumn2.setMinWidth(100);
        priceColumn2.setCellValueFactory(new PropertyValueFactory<>("cena"));


        tableZ2 = new TableView<>();
        tableZ2.setItems(getMyMarket());
        tableZ2.getColumns().addAll(nameColumn2, ratingColumn2, ageColumn2, teamColumn2, priceColumn2);
        tableZ2.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                delOff.setOnAction(g -> deleteOffer());
            }
        });

        //laczenie
        center = new VBox(10);
        center.getChildren().addAll(rynbuy, tableZ,myoffman,tableZ2);

        szkielet.setCenter(center);
        szkielet.getCenter().setStyle("-fx-padding: 10;"+"-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #f2f2f2;");
    }
    /**
     *  Metoda umożliwiająca usunięcie wybranej oferty transferowej
     * */
    public void deleteOffer()
    {
        Zawodnik selectedRecord =(Zawodnik)tableZ2.getSelectionModel().getSelectedItems().get(0);

        postgres.removeOffer(selectedRecord.id_karta);

        try {
            ObservableList<Zawodnik> allProduct, selectedProduct;
            allProduct = tableZ2.getItems();
            selectedProduct = tableZ2.getSelectionModel().getSelectedItems();
            selectedProduct.forEach(allProduct::remove);
        }catch (java.util.NoSuchElementException b)
        {
            System.out.println("ostatni element");
        }
    }
    /**
     *  Metoda umożliwiająca szybką sprzedaż wybranego zawodnika
     * */
    public void quickSellPlayer()
    {
        Zawodnik selectedRecord =(Zawodnik)tableZ.getSelectionModel().getSelectedItems().get(0);
        me.budzet+=(selectedRecord.cena/2);
        postgres.quickSellPlayer(me.id,selectedRecord.id_karta,me.budzet);
        updateLogin();
        openBench();

    }
    /**
     *  Metoda umożliwiająca dodanie oferty na rynku transferowym
     * */
    public void addOffer()
    {
        Stage oferta =new Stage();

        oferta.initModality(Modality.APPLICATION_MODAL);//okienko pierwszego priorytetu
        oferta.setTitle("Tworzenie oferty");
        oferta.setMinWidth(250);
        oferta.setMinHeight(100);

        Label mes=new Label("Określ cenę za jaką chcesz wystawić zawodnika na sprzedaż");

        Label cenalabel=new Label("Cena");
        TextField cena=new TextField();
        HBox cenaHBox=new HBox(5);
        cenaHBox.getChildren().addAll(cenalabel,cena);

        Button confirm=new Button("Wystaw");
        confirm.setOnAction(e->{
            Zawodnik selectedRecord =(Zawodnik)tableZ.getSelectionModel().getSelectedItems().get(0);
            postgres.addOffer(selectedRecord.id_karta,Integer.parseInt(cena.getText()));
            oferta.close();
        });

        VBox vert=new VBox(10);
        vert.getChildren().addAll(mes,cenaHBox,confirm);
        vert.setAlignment(Pos.CENTER);

        Scene s=new Scene(vert);

        oferta.setScene(s);
        oferta.showAndWait();
    }
    /**
     *  Metoda pozwalajaca na otwarcie widoku turniej
     * */
    public void openTournamentView()
    {
        Label tytul = new Label("Turnieje do wyboru");
        tytul.setFont(new Font("Arial",30.0));
        Button wybor=new Button("Wybierz turniej");
        Button tabela=new Button("Zobacz tabele");
        //admin things

                Button add=new Button("Dodaj turniej");
                add.setStyle("-fx-background-color:#90EE90");       //green for admin
                Button update=new Button("Update zawodnikow");
                update.setStyle("-fx-background-color:#90EE90");    //green for admin
                TextField link = new TextField("");
                link.setPromptText("link do aktualnego rankingu");
                link.setStyle("-fx-background-color:#90EE90");
                Button simulate=new Button("Symuluj turniej");
                simulate.setStyle("-fx-background-color:#90EE90");    //green for admin
                if(me.id!=8)        //marcin marcin
                {
                    add.setVisible(false);
                    update.setVisible(false);
                    link.setVisible(false);
                    simulate.setVisible(false);
                }
                update.setOnAction(h->{
                    if(!link.getText().isEmpty())
                        commitUpdate(link.getText());
                });

                add.setOnAction(f->{
                    Stage tabela2 =new Stage();
                    tabela2.initModality(Modality.APPLICATION_MODAL);//okienko pierwszego priorytetu
                    tabela2.setTitle("Dodawanie turnieju");

                    GridPane grid = new GridPane();
                    grid.setPadding(new Insets(10, 10, 10, 10));
                    grid.setVgap(10);
                    grid.setHgap(10);

                    Label nameLabel = new Label("Nazwa");
                    grid.add(nameLabel, 0, 0);
                    TextField name = new TextField("");
                    grid.add(name, 1, 0);
                    Label priceLabel = new Label("Nagroda");
                    grid.add(priceLabel, 0, 1);
                    TextField price = new TextField("");
                    grid.add(price, 1, 1);
                    //button
                    Button buttonLogin = new Button("Dodaj");
                    grid.add(buttonLogin, 1, 2);
                    buttonLogin.setOnAction(g->{
                        postgres.addTournament(name.getText(),Integer.parseInt(price.getText()));
                        tableT.setItems(getAllTournaments());
                        tabela2.close();
                    });


                    VBox all = new VBox(10);
                    all.getChildren().addAll(grid);

                    Scene s=new Scene(all,250,130);
                    tabela2.setScene(s);
                    tabela2.showAndWait();
                });

        HBox loc = new HBox(10);
        loc.getChildren().addAll(wybor,tabela,add,simulate,update,link);

        TableColumn<Turniej4, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        TableColumn<Turniej4, Integer> winningsColumn = new TableColumn<>("Nagroda");
        winningsColumn.setMinWidth(100);
        winningsColumn.setCellValueFactory(new PropertyValueFactory<>("nagroda"));
        TableColumn<Turniej4, String> team1Column = new TableColumn<>("Druzyna#1");
        team1Column.setMinWidth(100);
        team1Column.setCellValueFactory(new PropertyValueFactory<>("druzyna1"));
        TableColumn<Turniej4, String> team2Column = new TableColumn<>("Druzyna#2");
        team2Column.setMinWidth(100);
        team2Column.setCellValueFactory(new PropertyValueFactory<>("druzyna2"));
        TableColumn<Turniej4, String> team3Column = new TableColumn<>("Druzyna#3");
        team3Column.setMinWidth(100);
        team3Column.setCellValueFactory(new PropertyValueFactory<>("druzyna3"));
        TableColumn<Turniej4, String> team4Column = new TableColumn<>("Druzyna#4");
        team4Column.setMinWidth(100);
        team4Column.setCellValueFactory(new PropertyValueFactory<>("druzyna4"));

        tableT = new TableView<>();
        tableT.setItems(getAllTournaments());
        tableT.getColumns().addAll(nameColumn, winningsColumn, team1Column, team2Column, team3Column,team4Column);
        tableT.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                wybor.setOnAction(e-> openTeamsView());
                tabela.setOnAction(f-> tryOpenScoreBoard());
                simulate.setOnAction(p->simulateTournament(newSelection.id_turniej));
            }

        });

        center = new VBox(10);
        center.getChildren().addAll(tytul,loc,tableT);

        szkielet.setCenter(center);
        szkielet.getCenter().setStyle("-fx-padding: 10;"+"-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #f2f2f2;");
    }
    public void simulateTournament(int id_t)
    {
        int[] mecze=new int[6];
        mecze=postgres.getTournamentMatches(id_t);

        for(int m:mecze)
        {
            System.out.println("id_m:"+m);
            Mecz thisMatch =postgres.getThisMatchData(m);
            if(thisMatch.endScore.equals("nie rozegrano"))      //rozegraj tylko nie rozegrane mecze
            {
                thisMatch.setIdGraczy(postgres.getTeamManager(thisMatch.id_druzyna1),postgres.getTeamManager(thisMatch.id_druzyna2));
                thisMatch.A=postgres.getTeamInfo(thisMatch.druzyna1,thisMatch.id_gracz_1);
                thisMatch.B=postgres.getTeamInfo(thisMatch.druzyna2,thisMatch.id_gracz_2);
                System.out.println(thisMatch.A.nazwa+"vs"+thisMatch.B.nazwa);
                openMatch(thisMatch);
            }

        }
        openScoreBoard();

    }
    /**
     *  Metoda wykonujaca update na wszyskich zawodnikach
     * */
    public void commitUpdate(String link)
    {
        String str="";
        String[] tab;
        String[] topTeams=new String[20];       //20 teams are counted to form calculations
        int i=0;
        //copy to old version
        try
        {
            File f = new File("zawodnikRating.txt");
            File fold = new File("zawodnikRatingOLD.txt");
            if (!f.exists())        //jesli nie bylo zawodnikRating.txt
            {
                f.createNewFile();
            }
            else
            {
                if (!fold.exists())
                    fold.createNewFile();

                Files.copy(f.toPath(), fold.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            WebScrapper ws=new WebScrapper(link);
            topTeams=ws.topTeams;       //needed for bonus
            for(String n:topTeams)
                System.out.println("x:"+n);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        //update data in database
        double bonus=1.0;
        try {
            BufferedReader in = new BufferedReader(new FileReader("zawodnikRating.txt"));       //reader
            while ((str = in.readLine()) != null)
            {
                tab=str.split(",");
                for(int k=0;k<20;k++)
                {
                    if(topTeams[k].equals(tab[1]))
                    {
                        bonus=1.1-k*0.01;
                        break;
                    }
                    else
                        bonus=1.0;
                }


                postgres.updatePlayer(tab[0],tab[1],Double.parseDouble(tab[2])*bonus,bonus);
            }
            in.close();
            AlertBox a=new AlertBox();
            a.alert("Done","Zakonczono update pomyslnie");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *  Metoda sprawdzajaca czy wybrany turniej rozpoczął sie
     * */
    public void tryOpenScoreBoard()
    {
        Turniej4 selectedTournament =(Turniej4)tableT.getSelectionModel().getSelectedItems().get(0);
        if(!selectedTournament.checkIfRegisterOpened())
            openScoreBoard();
        else
        {
            AlertBox a=new AlertBox();
            a.alert("Turniej sie nie zaczal","Wybrany turniej jeszcze sie nie rozpoczął");
        }
    }
    /**
     *  Metoda umożliwiająca na podejrzenie statystyk dotyczacych wybranego turnieju
     * */
    public void openScoreBoard()
    {
        Stage tabela =new Stage();

        tabela.initModality(Modality.APPLICATION_MODAL);//okienko pierwszego priorytetu
        tabela.setTitle("Tabela");
        Turniej4 selectedTournament =(Turniej4)tableT.getSelectionModel().getSelectedItems().get(0);
        int id_t=selectedTournament.id_turniej;

        TableColumn<Scoreboard, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setMinWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Scoreboard, Integer> matchesColumn = new TableColumn<>("Mecze");
        matchesColumn.setMinWidth(50);
        matchesColumn.setCellValueFactory(new PropertyValueFactory<>("m"));
        TableColumn<Scoreboard, Integer> winsColumn = new TableColumn<>("Wygrane");
        winsColumn.setMinWidth(50);
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("w"));
        TableColumn<Scoreboard, Integer> loseColumn = new TableColumn<>("Przegrane");
        loseColumn.setMinWidth(50);
        loseColumn.setCellValueFactory(new PropertyValueFactory<>("l"));
        TableColumn<Scoreboard, Integer> pktColumn = new TableColumn<>("Punkty");
        pktColumn.setMinWidth(50);
        pktColumn.setCellValueFactory(new PropertyValueFactory<>("pkt"));
        pktColumn.setSortType(TableColumn.SortType.DESCENDING);

        tableS = new TableView<>();
        tableS.setItems(getScoreboard(id_t));
        tableS.getColumns().addAll(nameColumn,matchesColumn, winsColumn, loseColumn, pktColumn);
        tableS.getSortOrder().add(pktColumn);
        VBox all = new VBox(10);
        all.getChildren().add(tableS);

        Scene s=new Scene(all,450,200);
        tabela.setScene(s);
        tabela.showAndWait();
    }
    /**
     *  Metoda umożliwiająca wypelnienie tablicy wyników danego turnieju
     *  @param id_t id turnieju do wyszukania
     * */
    public ObservableList<Scoreboard> getScoreboard(int id_t)
    {
        ObservableList<Scoreboard> kolekcja = FXCollections.observableArrayList();

        Scoreboard[] s=postgres.calculateScoreboard(id_t);
        for(int i=0;i<s.length;i++)
        {
            kolekcja.add(s[i]);
        }

        updateLogin();
        return kolekcja;
    }
    /**
     *  Metoda umożliwiająca wybór drużyny grającej w turnieju
     * */
    public void openTeamsView()
    {
        Stage assign =new Stage();

        assign.initModality(Modality.APPLICATION_MODAL);//okienko pierwszego priorytetu
        assign.setTitle("Wybor duzyny");
        assign.setMinWidth(250);
        assign.setMinHeight(100);

        Button b=new Button("Wybierz");

        Label mes=new Label("Wybierz druzyne która zagra w turnieju");
        //tabelka
        TableColumn<Druzyna, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        TableColumn<Druzyna, Integer> teamColumn = new TableColumn<>("Ocena");
        teamColumn.setMinWidth(100);
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("ocena"));

        table = new TableView<>();
        table.setItems(getTeams());
        table.getColumns().addAll(nameColumn, teamColumn);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                b.setOnAction(e -> {
                    Turniej4 selectedTournament =(Turniej4)tableT.getSelectionModel().getSelectedItems().get(0);
                    Druzyna selectedTeam =(Druzyna)table.getSelectionModel().getSelectedItems().get(0);
                    if(selectedTournament.zapiszTeam(selectedTeam.id_druzyna,postgres.chceckIsPlaysInTournament(selectedTournament.id_turniej,me.id)))
                    {
                        postgres.addTeamToTournament(selectedTeam.id_druzyna,selectedTournament.id_turniej,selectedTournament.druzynaCount+1);
                        openTournamentView();
                    }
                    else
                    {
                        AlertBox a=new AlertBox();
                        a.alert("Juz grasz","Nie ma miejsca w turnieju");
                    }

                    assign.close();
                });
            }
        });


        VBox label1=new VBox(10);
        label1.getChildren().addAll(mes,table,b);
        label1.setAlignment(Pos.CENTER);

        Scene s=new Scene(label1);

        assign.setScene(s);
        assign.showAndWait();
    }
    /**
     *  Metoda umożliwiająca wyszukanie meczy zawierajacych druzyny gracza
     * */
    public void openMyMatches()
    {
        Label tytul = new Label("Mecze do zagrania");
        tytul.setFont(new Font("Arial",30.0));
        Button wybor=new Button("Zagraj");

        TableColumn<Mecz, String> name1Column = new TableColumn<>("Nazwa#1");
        name1Column.setMinWidth(200);
        name1Column.setCellValueFactory(new PropertyValueFactory<>("druzyna1"));
        TableColumn<Mecz, String> name2Column = new TableColumn<>("Nazwa#2");
        name2Column.setMinWidth(200);
        name2Column.setCellValueFactory(new PropertyValueFactory<>("druzyna2"));
        TableColumn<Mecz, String> scoreColumn = new TableColumn<>("Wynik");
        scoreColumn.setMinWidth(100);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("endScore"));

        tableM = new TableView<>();
        tableM.setItems(getMyMatches());
        tableM.getColumns().removeAll(name1Column, name2Column,scoreColumn);
        tableM.getColumns().addAll(name1Column, name2Column,scoreColumn);
        tableM.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null&& newSelection.endScore=="nie rozegrano")
            {
                int sizeA=postgres.getTeamSize(newSelection.id_druzyna1);
                int sizeB=postgres.getTeamSize(newSelection.id_druzyna2);
                AlertBox a=new AlertBox();

                    wybor.setOnAction(e-> {
                        System.out.println(sizeA +":a b: "+sizeB);
                        if(sizeA==5&&sizeB==5)
                            initMatch();
                        else if(sizeA!=5)
                            a.alert("Nie mozna rozegrac meczu","Twoja druzyna nie jest pełna");
                        else if(sizeB!=5)
                            a.alert("Nie mozna rozegrac meczu","Druzyna twojego przeciwnika nie jest pełna");
                    });
            }
        });

        center = new VBox(10);
        center.getChildren().addAll(tytul,wybor,tableM);

        szkielet.setCenter(center);
        szkielet.getCenter().setStyle("-fx-padding: 10;"+"-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #f2f2f2;");
    }
    /**
     *  Metoda inicjalizująca mecz
     * */
    public void initMatch()
    {
        Mecz thisMatch =(Mecz)tableM.getSelectionModel().getSelectedItems().get(0);
        thisMatch.setIdGraczy(postgres.getTeamManager(thisMatch.id_druzyna1),postgres.getTeamManager(thisMatch.id_druzyna2));
        thisMatch.A=postgres.getTeamInfo(thisMatch.druzyna1,thisMatch.id_gracz_1);
        thisMatch.B=postgres.getTeamInfo(thisMatch.druzyna2,thisMatch.id_gracz_2);
        openMatch(thisMatch);
    }
    /**
     *  Metoda umożliwiająca rozegranie meczu
     *  @param thisMatch adres obiektu meczu do rozegrania
     * */
    public void openMatch(Mecz thisMatch)
    {
        Stage meczyk =new Stage();

        meczyk.initModality(Modality.APPLICATION_MODAL);//okienko pierwszego priorytetu
        meczyk.setTitle("Mecz");

        HBox pasekWyniku = new HBox(10);
            pasekWyniku.setAlignment(TOP_CENTER);
            pasekWyniku.setPadding(new Insets(15, 12, 15, 12));
        Label d1name=new Label(thisMatch.druzyna1);
            d1name.setFont(new Font("Arial",30.0));
        Label d2name=new Label(thisMatch.druzyna2);
            d2name.setFont(new Font("Arial",30.0));
        Label d1score=new Label(Integer.toString(thisMatch.score1));
            d1score.setFont(new Font("Arial",30.0));
        Label d2score=new Label(Integer.toString(thisMatch.score2));
            d2score.setFont(new Font("Arial",30.0));
        VBox r=new VBox(3);
        Label round=new Label("Runda");
            round.setFont(new Font("Arial",14.0));
        Label runda=new Label(Integer.toString(thisMatch.runda)+"/30");
            runda.setFont(new Font("Arial",14.0));
        r.getChildren().addAll(round,runda);
        pasekWyniku.getChildren().addAll(d1name,d1score,r,d2score,d2name);
        pasekWyniku.setStyle("-fx-border-style: solid;" + "-fx-border-width: 8;" + "-fx-border-color: black;"+"-fx-background-color: #ff9900;");

                    //team A
                    TableColumn<Zawodnik, String> nameColumn = new TableColumn<>("Nick");
                    nameColumn.setMinWidth(100);
                    nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
                    TableColumn<Zawodnik, String> teamColumn = new TableColumn<>("Druzna");
                    teamColumn.setMinWidth(100);
                    teamColumn.setCellValueFactory(new PropertyValueFactory<>("org_druzyna"));
                    TableColumn<Zawodnik, Integer> ratingColumn = new TableColumn<>("Ocena");
                    ratingColumn.setMinWidth(50);
                    ratingColumn.setCellValueFactory(new PropertyValueFactory<>("ocena"));
                    TableColumn<Zawodnik, Integer> playerKill = new TableColumn<>("Zabicia");
                    playerKill.setMinWidth(10);
                    playerKill.setCellValueFactory(new PropertyValueFactory<>("k"));
                    TableColumn<Zawodnik, Integer> playerDeath = new TableColumn<>("Zgony");
                    playerDeath.setMinWidth(10);
                    playerDeath.setCellValueFactory(new PropertyValueFactory<>("d"));

                    tableZ = new TableView<>();
                    tableZ.setItems(getTeammatesOfTeam(thisMatch.A));
                    tableZ.getColumns().addAll(nameColumn, teamColumn, ratingColumn,playerKill,playerDeath);

                    //team B
                    TableColumn<Zawodnik, String> nameColumn2 = new TableColumn<>("Nick");
                    nameColumn2.setMinWidth(100);
                    nameColumn2.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
                    TableColumn<Zawodnik, String> teamColumn2 = new TableColumn<>("Druzna");
                    teamColumn2.setMinWidth(100);
                    teamColumn2.setCellValueFactory(new PropertyValueFactory<>("org_druzyna"));
                    TableColumn<Zawodnik, Integer> ratingColumn2 = new TableColumn<>("Ocena");
                    ratingColumn2.setMinWidth(50);
                    ratingColumn2.setCellValueFactory(new PropertyValueFactory<>("ocena"));
                    TableColumn<Zawodnik, Integer> playerKill2 = new TableColumn<>("Zabicia");
                    playerKill2.setMinWidth(10);
                    playerKill2.setCellValueFactory(new PropertyValueFactory<>("k"));
                    TableColumn<Zawodnik, Integer> playerDeath2 = new TableColumn<>("Zgony");
                    playerDeath2.setMinWidth(10);
                    playerDeath2.setCellValueFactory(new PropertyValueFactory<>("d"));

                    tableZ2 = new TableView<>();
                    tableZ2.setItems(getTeammatesOfTeam(thisMatch.B));
                    tableZ2.getColumns().addAll(nameColumn2, teamColumn2, ratingColumn2,playerKill2,playerDeath2);

        VBox all=new VBox(10);
        HBox tables=new HBox(10);
        Scene s=new Scene(all,800,600);

        Button play=new Button("Rozegraj runde");
        Button endGame=new Button("Zakończ mecz");
        Button simulate=new Button("Symuluj mecz");
        endGame.setVisible(false);
        endGame.setOnAction(f->{
            AlertBox a=new AlertBox();
            int winnings=Math.abs(thisMatch.score1 - thisMatch.score2);
            a.alert("wygrana","Wygrana druzyna zarobila "+(500+ winnings*20)+" monet");
            thisMatch.endScore=thisMatch.score1+" : "+thisMatch.score2;
            meczyk.close();
            tableM.refresh();
            updateLogin();
            openMyMatches();
        });
        //symuluje mecz
        simulate.setOnAction(f->{
            {
                for (int rundy = 0; rundy < 30; rundy++)
                {
                    thisMatch.rozegrajRunde();
                    d1score.setText(Integer.toString(thisMatch.score1));
                    d2score.setText(Integer.toString(thisMatch.score2));
                    runda.setText(Integer.toString(thisMatch.runda)+"/30");

                    if(thisMatch.score1>15)
                    {
                        System.out.println(thisMatch.score1+" --- "+thisMatch.score2);
                        System.out.println("Mecz wygral 1");
                        play.setVisible(false);
                        endGame.setVisible(true);
                        simulate.setVisible(false);
                        //add money to winner np. aka return
                        postgres.addScore(thisMatch.id_druzyna1,thisMatch.id_mecz,thisMatch.score1+" : "+thisMatch.score2,500+(thisMatch.score1-thisMatch.score2)*20);
                        break;
                    }
                    else
                    {
                        //System.out.println("...setting...1");
                        tableZ.getItems().removeAll();
                        tableZ.setItems(getTeammatesOfTeam(thisMatch.A));
                        tableZ.refresh();

                    }
                    if(thisMatch.score2>15)
                    {
                        System.out.println(thisMatch.score1+" --- "+thisMatch.score2);
                        System.out.println("Mecz wygral 2");
                        play.setVisible(false);
                        endGame.setVisible(true);
                        simulate.setVisible(false);
                        //add money to winner np. aka return
                        postgres.addScore(thisMatch.id_druzyna2,thisMatch.id_mecz,thisMatch.score1+" : "+thisMatch.score2,500+ (thisMatch.score2 - thisMatch.score1)*20);
                        break;
                    }
                    else
                    {
                        //System.out.println("...setting...2");
                        tableZ2.getItems().removeAll();
                        tableZ2.setItems(getTeammatesOfTeam(thisMatch.B));
                        tableZ2.refresh();
                    }
                }
                System.out.println("KONIEC rundy");
            }
        });
        //rozgrywa runde
        play.setOnAction(e->{
            //odcinam mozliwosc symulacji wyniku
            simulate.setVisible(false);
            //play.setVisible(false);
            {
                //for (int rundy = 0; rundy < 30; rundy++)
                {
                    thisMatch.rozegrajRunde();
                    d1score.setText(Integer.toString(thisMatch.score1));
                    d2score.setText(Integer.toString(thisMatch.score2));
                    runda.setText(Integer.toString(thisMatch.runda)+"/30");

                    if(thisMatch.score1>15)
                    {
                        System.out.println(thisMatch.score1+" --- "+thisMatch.score2);
                        System.out.println("Mecz wygral 1");
                        play.setVisible(false);
                        endGame.setVisible(true);
                        //break;
                        //add money to winner np. aka return
                        postgres.addScore(thisMatch.id_druzyna1,thisMatch.id_mecz,thisMatch.score1+" : "+thisMatch.score2,500+(thisMatch.score1-thisMatch.score2)*20);

                    }
                    else
                    {
                        //System.out.println("...setting...1");
                        tableZ.getItems().removeAll();
                        tableZ.setItems(getTeammatesOfTeam(thisMatch.A));
                        tableZ.refresh();

                    }
                    if(thisMatch.score2>15)
                    {
                        System.out.println(thisMatch.score1+" --- "+thisMatch.score2);
                        System.out.println("Mecz wygral 2");
                        play.setVisible(false);
                        endGame.setVisible(true);
                        //break;
                        //add money to winner np. aka return
                        postgres.addScore(thisMatch.id_druzyna2,thisMatch.id_mecz,thisMatch.score1+" : "+thisMatch.score2,500+ (thisMatch.score2 - thisMatch.score1)*20);
                    }
                    else
                    {
                        //System.out.println("...setting...2");
                        tableZ2.getItems().removeAll();
                        tableZ2.setItems(getTeammatesOfTeam(thisMatch.B));
                        tableZ2.refresh();
                    }
                }
                System.out.println("KONIEC rundy");
            }
            meczyk.setOnCloseRequest(p -> {
                if (thisMatch.score1>15||thisMatch.score2>15)
                {
                    thisMatch.endScore=thisMatch.score1+" : "+thisMatch.score2;
                    //System.out.println(thisMatch.score1+" : "+thisMatch.score2);
                    meczyk.close();
                    tableM.refresh();
                    updateLogin();
                    openMyMatches();
                } else
                {
                    AlertBox a=new AlertBox();
                    a.alert("nie","Nie dokonczyles meczu");
                    p.consume();
                }

            });
        });
        HBox HPlay=new HBox(10);
            HPlay.setAlignment(BOTTOM_CENTER);
            HPlay.getChildren().addAll(play,endGame,simulate);


        tables.getChildren().addAll(tableZ,tableZ2);
        all.getChildren().addAll(pasekWyniku,tables,HPlay);


        meczyk.setScene(s);
        meczyk.showAndWait();
    }
    /**
     *  Metoda umożliwiająca wypelnienie tabeli meczy
     * */
    public ObservableList<Mecz> getMyMatches()
    {
        ObservableList<Mecz> kolekcja = FXCollections.observableArrayList();
        Mecz[] tabMatches= null;
        tabMatches=postgres.getMyMatches(me.id);
        for(int i=0;i<tabMatches.length;i++)
        {
            kolekcja.add(tabMatches[i]);
        }
        return kolekcja;
    }
}
