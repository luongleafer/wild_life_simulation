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
import view.audio.SoundEngine;

public class TestWorldRenderer extends Application {
    WorldModel model = new WorldModel(80, 80);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane worldPane = new StackPane();
        WorldView renderer = new WorldView(model, worldPane);
        WorldController controller = new WorldController(model, renderer);
        SoundEngine.initEngine();
        model.generateTerrain();
        Scene scene = new Scene(worldPane, 640, 480);
        controller.registerBlockTextures();
        controller.registerEntityTextures();
        controller.registerSound();
        controller.startUpdateWorldService(20);
        renderer.startRendering();
        for(int i = 0; i<10;i++) {
            controller.spawnEntity(new Wolf(new EntityCoordinate(5 * i, 5 * i)));
        }
        for(int i = 0; i<20;i++){
            // let's just spawn 100 pigs cuz why not
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
