module test{
    requires javafx.controls;
    requires javafx.graphics;
    requires java.logging;

    opens test to javafx.graphics, javafx.fxml;

}