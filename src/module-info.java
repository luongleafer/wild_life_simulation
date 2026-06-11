module wild.life.simulation{
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires javafx.media;
    
    opens test to javafx.graphics, javafx.fxml, javafx.media;
    opens controller to javafx.fxml;
    // Thêm nếu có lỗi reflection khi chạy JavaFX
}
