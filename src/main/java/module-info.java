module com.ashutosh.praharaj.connectfour {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ashutosh.praharaj.connectfour to javafx.fxml;
    exports com.ashutosh.praharaj.connectfour;
}