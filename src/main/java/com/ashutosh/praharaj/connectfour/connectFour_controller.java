package com.ashutosh.praharaj.connectfour;

//1 Error in which multiple discs are inserted by same user is fixed
//2 blurry disc edges (c.setSmooth)
//3 undo last action Function (to be implemented)

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class connectFour_controller implements Initializable {

    private static final int COLUMNS = 7 ;
    private static final int ROWS = 6 ;
    private static final int CIRCLE_DIAMETER = 80 ;

    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";


    private static String PLAYER_ONE ="Player One" ;
    private static String PLAYER_TWO ="Player Two" ;

    private boolean isPLAYER_ONE = true ;

    //to fix error 1
    private boolean isALLOWED =  true ;

    //stores the status of the playground (where the discs are)
    private  Disc[][] insertedDiscs = new Disc[ROWS][COLUMNS] ;

    @FXML
    public GridPane parentGridPane ;
    @FXML
    public Pane menuBarPane , playGround;
    @FXML
    public VBox infoVBox ;
    @FXML
    public TextField player2TextField , player1TextField ;
    @FXML
    public Button setNameButton ;
    @FXML
    public Label playerNameLabel , turnLabel ;

    public void createPlayground() {
        //THIS function a rectangle(WHITE) is added on to grid pane 0,1 (BLUISH COLOR) and
        //circle object is used to create holes in it Shape.subtract()
        Shape stage = new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER) ;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Circle c = new Circle() ;
                c.setRadius(CIRCLE_DIAMETER/2); ;
                c.setCenterX(CIRCLE_DIAMETER/2);
                c.setCenterY(CIRCLE_DIAMETER/2);

                c.setTranslateX(j*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                c.setTranslateY(i*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

                //smooth edges for our "discs"
                c.setSmooth(true) ;
                stage = Shape.subtract(stage,c) ;
            }
        }
        stage.setFill(Color.WHITE);
        parentGridPane.add(stage,0,1) ;

        List<Rectangle> blinker = createClickableColumns();
        for ( Rectangle x:blinker) {
            parentGridPane.add(x,0, 1) ;
        }



    }
    private List<Rectangle> createClickableColumns() {
        //this function a rectangle for  a blinking effect on entire column
        // when mouse hover on them + calling the disc function
        List<Rectangle> rxList = new ArrayList<>() ;
        for(int i=0 ; i<COLUMNS;i++){
        Rectangle rx = new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER) ;
        rx.setFill(Color.TRANSPARENT);
        rx.setTranslateX(i*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        rx.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                rx.setFill(Color.valueOf("#eeeeee26"));
            }
        });
        rx.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                rx.setFill(Color.TRANSPARENT);
            }
        });
        final int ii = i ;
        rx.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(isALLOWED) {
                    isALLOWED = false ;
                    insertDisc(new Disc(isPLAYER_ONE), ii);
                }
            }
        });
        rxList.add(rx) ;
        }
        return rxList ;
    }

    private void insertDisc(Disc disc ,int column) {
    //this function creates the disc that translates to available slot in selected column on
        // mouse click also changes disc color based on  which player turn

        int row = ROWS -1 ;
        while (row>=0)
        {
            if(getDiscIfPresent(row,column) ==null)
                break ; //empty space found
            row-- ;
        }
        if (row < 0)
            return ; //ROW IS FULL
        insertedDiscs[row][column] = disc ;
        playGround.getChildren().add(disc) ;


        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

        TranslateTransition tr = new TranslateTransition(Duration.seconds(0.4),disc) ;
        tr.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        switchPlayer (tr) ;
        tr.play() ;
        if (gameEnded(row,column)){
            gameOver() ;
            return ;
        }


    }

    private boolean gameEnded(int row, int col) {
//IMP
        //VERTICAL
        List<Point2D> vertical = IntStream.rangeClosed(row-3,row+3).mapToObj(r -> new Point2D(r,col)).collect(Collectors.toList()) ;
        //HORIZONTAL
        List<Point2D> horizontal = IntStream.rangeClosed(col-3,col+3).mapToObj(c -> new Point2D(row,c)).collect(Collectors.toList()) ;

        //DIAGONAL 1

        Point2D startPoint1 = new Point2D(row - 3, col + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        //Diagonal 2
        Point2D startPoint2 = new Point2D(row - 3, col - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());

        boolean isEnded = checkAns(vertical) || checkAns(horizontal) || checkAns(diagonal1Points) || checkAns(diagonal2Points);
        return  isEnded ;
    }

    private boolean checkAns(List <Point2D> points) {
//IMP
        int total = 0 ;
        for (Point2D target : points){
            int rowIndexForArray = (int) target.getX() ;
            int colIndexForArray = (int) target.getY() ;

            Disc disc = getDiscIfPresent(rowIndexForArray,colIndexForArray);

            if(disc !=null && disc.isPlayerOneMove == isPLAYER_ONE) {
                total++ ;
                if(total == 4){
                    return true ;
                }
            } else {
                total = 0 ;
            }
        }
        return false ;
    }
    private Disc getDiscIfPresent (int r, int c){
        //to avoid array out of bound exception
        if (r>=ROWS || r < 0 || c >= COLUMNS || c < 0)
            return null ;
        return insertedDiscs[r][c] ;
    }
    private void gameOver() {
//CHECK AGAIN ---done
        String winner = isPLAYER_ONE ? PLAYER_ONE : PLAYER_TWO ;
        System.out.println("Winner is " + winner);

        Alert al = new Alert(Alert.AlertType.INFORMATION) ;
        al.setTitle("Connect Four");
        al.setHeaderText("Winner is " + winner);
        al.setContentText("Want to play again ?");

        ButtonType yes = new ButtonType("yes") ;
        ButtonType no = new ButtonType("No") ;
        al.getButtonTypes().setAll(yes,no) ;

        Optional <ButtonType> clicked = al.showAndWait() ;

        Platform.runLater(()->{
            //MAKES SURE THIS IS EXECUTED ONLY AFTER translate transformation
            if (clicked.isPresent() && clicked.get() == yes){
                //clear the insertedDisc[][] and the playground
                resetGame() ;
                System.out.println("reset the game");
            }
            else{
                System.out.println("exit the game from confirmation");
                Platform.exit();
                System.exit(0);
            }
        });


    }

    public void resetGame() {
        //CALLED IN THE WINNER ALERTBOX
        playGround.getChildren().clear() ;
        for (int i = 0; i < insertedDiscs.length; i++) {
            for (int j = 0; j < insertedDiscs[i].length; j++) {
                insertedDiscs[i][j] = null ;
            }
        }
        playerNameLabel.setText(isPLAYER_ONE?PLAYER_ONE:PLAYER_TWO) ;
        isPLAYER_ONE = true ;
        createPlayground();
    }

    private void switchPlayer(TranslateTransition tr) {
        //USE TO SWITCH PLAYERS , automatically ofCourse ; isALLOWED if for err1
        tr.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                isPLAYER_ONE = !isPLAYER_ONE ;
                if (isPLAYER_ONE){
                    playerNameLabel.setText("Player One");
                }
                else {
                    playerNameLabel.setText("Player Two");
                }
                isALLOWED = true ;
            }

        });
    }

    public class Disc extends Circle {
        //to create a disc object to use in mouseClicked event inside createClickableColumn
        private final boolean isPlayerOneMove ;
        public Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove ? Color.valueOf(discColor1): Color.valueOf(discColor2)) ;
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setNameButton.setOnAction(new EventHandler<ActionEvent>() {
            //Event Handler for set names button
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Set Name button clicked");
                String a , b ;
                a = player1TextField.getText() ;
                b = player2TextField.getText() ;
                PLAYER_ONE = a ;
                PLAYER_TWO = b ;
                //without next line change will not happen immediately after the click
                playerNameLabel.setText(isPLAYER_ONE?PLAYER_ONE:PLAYER_TWO) ;
            }
        });
    }
}