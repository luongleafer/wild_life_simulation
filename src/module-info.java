module test{
    requires javafx.controls;
    requires javafx.graphics;

    opens test to javafx.graphics, javafx.fxml;

}