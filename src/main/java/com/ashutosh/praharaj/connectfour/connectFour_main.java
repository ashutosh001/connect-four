package com.ashutosh.praharaj.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class connectFour_main extends Application {
    private connectFour_controller qwer;

    @Override
    public void start(Stage x) throws IOException {
        System.out.println("App Starting");
        FXMLLoader y = new FXMLLoader(getClass().getResource("connectFour_layout.fxml"));
        GridPane rootNode = y.load();

        qwer = y.getController();
        qwer.createPlayground();

        MenuBar qw = createMenu();
        qw.prefWidthProperty().bind(x.widthProperty());

        Pane menuPane = (Pane) rootNode.getChildren().get(0);
        menuPane.getChildren().setAll(qw);

        Scene z = new Scene(rootNode );
        x.setTitle("Connect Four");
        x.setScene(z);
        x.setResizable(false);
        x.show();
    }


    @Override
    public void init() throws Exception {
        System.out.println("Initializing app");
        super.init();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping App");
        super.stop();
    }

    private MenuBar createMenu() {
        Menu a = new Menu("File");
        Menu b = new Menu("Help");

        MenuItem m = new MenuItem("Reset");
        m.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("File --> Reset");
                qwer.resetGame();
            }
        });
        SeparatorMenuItem x = new SeparatorMenuItem();
        MenuItem n = new MenuItem("Exit");
        n.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("File --> Exit");
                exitGame() ;
            }
        });
        MenuItem o = new MenuItem("How to Play");
        o.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Help --> How to play");
                aboutConnectFour();
            }
        });
        SeparatorMenuItem y = new SeparatorMenuItem();
        MenuItem p = new MenuItem("About the developer");
        p.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("About --> About the developer");
                aboutTheDeveloper();
            }
        });

        a.getItems().addAll(m, x, n);
        b.getItems().addAll(o, y, p);

        MenuBar z = new MenuBar();
        z.getMenus().addAll(a, b);

        return z;
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void aboutConnectFour() {
        //DISPLAYS ONLY PART OF MESSAGE NEEDS TO BE FIXED
        //Used setWidth and setHeight
        Alert y = new Alert(Alert.AlertType.INFORMATION);
        y.setWidth(300);
        y.setHeight(400);
        y.setTitle("About Connect Four");
        y.setHeaderText("How to Play");
        y.setContentText("Connect Four is a two-player connection game in which the players first choose a color " +
                "and then take turns dropping colored discs from the top into a seven-column, " +
                "six-row vertically suspended grid. The pieces fall straight down, occupying the next available" +
                " space within the column. The objective of the game is to be the first to form a horizontal, " +
                "vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.");
        y.show();
    }

    private void aboutTheDeveloper() {
        Alert x = new Alert(Alert.AlertType.INFORMATION);
        x.setTitle("About the Developer");
        x.setHeaderText("Ashutosh Praharaj");
        x.setContentText("This is my Connect Four GUI project using JAVA , JavaFX and IntellijIDEA");
        x.show();
    }

    public static void main(String[] args) {
        System.out.println("Main");
        launch();

    }
}