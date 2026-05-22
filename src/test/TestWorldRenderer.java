package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entity.EntityCoordinate;
import model.entity.Wolf;
import model.world.WorldModel;
import view.WorldRenderer;

public class TestWorldRenderer extends Application {
    WorldModel model = new WorldModel(50, 20);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        GridPane terrainGrid = new GridPane();
        AnchorPane entityPane = new AnchorPane();
        WorldRenderer renderer = new WorldRenderer(model,terrainGrid, entityPane);
        model.generateTerrain();
        Scene scene = new Scene(new StackPane(terrainGrid, entityPane), 640, 480);
        renderer.registerBlockTextures();
        renderer.registerEntityTextures();
        renderer.renderWorld();
        renderer.startUpdateWorldService(20);
        renderer.startRendering();
        model.spawnEntity(new Wolf(new EntityCoordinate(0,0)));
        model.spawnEntity(new Wolf(new EntityCoordinate(12,12)));
        stage.setTitle("Wild Life Simulation");
        stage.setScene(scene);
        stage.show();
    }
}
