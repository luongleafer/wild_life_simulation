module wild.life.simulation{
    requires javafx.controls;
    requires javafx.graphics;
    requires java.logging;
    requires javafx.media;

<<<<<<< HEAD:src/module-info.java.bak
    opens test to javafx.graphics, javafx.fxml;
    // Thêm nếu có lỗi reflection khi chạy JavaFX:
=======
    opens test to javafx.graphics, javafx.fxml, javafx.media;
>>>>>>> origin/main:src/module-info.java
}