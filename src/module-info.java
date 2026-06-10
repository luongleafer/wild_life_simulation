module wild.life.simulation{
    requires javafx.controls;
    requires javafx.graphics;
    requires java.logging;
    requires javafx.media;

    opens test to javafx.graphics, javafx.fxml, javafx.media;
    requires javafx.fxml;
    opens screencontroller to javafx.fxml;
    exports screencontroller;
}