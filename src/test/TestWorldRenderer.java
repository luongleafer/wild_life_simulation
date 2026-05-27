package test;

import controller.WorldController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.animals.Cow;
import model.animals.Pig;
import model.animals.Wolf;
import model.entity.EntityCoordinate;
import model.world.WorldModel;
import view.WorldView;

public class TestWorldRenderer extends Application {
    WorldModel model = new WorldModel(80, 80);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        GridPane terrainGrid = new GridPane();
        AnchorPane entityPane = new AnchorPane();
        WorldView renderer = new WorldView(model, terrainGrid, entityPane);
        WorldController controller = new WorldController(model);
        model.generateTerrain();
        Scene scene = new Scene(new StackPane(terrainGrid, entityPane), 640, 480);
        controller.registerBlockTextures();
        renderer.registerEntityTextures();
        renderer.renderWorld();
        controller.startUpdateWorldService(20);
        renderer.startRendering();
        for(int i = 0; i<3;i++) {
            controller.spawnEntity(new Wolf(new EntityCoordinate(10 * i, 10 * i)));
        }
        for(int i = 0; i<10;i++){
            controller.spawnEntity(new Pig(new EntityCoordinate(25,25)));
        }
        for(int i = 0; i<10;i++){
            controller.spawnEntity(new Cow(new EntityCoordinate(30, 30)));
        }
        stage.setTitle("Wild Life Simulation");
        stage.setScene(scene);
        stage.show();
    }
}
