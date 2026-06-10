package test;

import controller.WorldController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import model.animals.Wolf;
import model.entity.EntityCoordinate;
import model.world.WorldModel;
import view.WorldView;

public class TestPredatorPrey extends Application {


    @Override
    public void start(Stage stage) {

        // Tạo world
        WorldModel world = new WorldModel(100, 100);
        world.generateTerrain();

        // Tạo view
        Pane root = new Pane();
        WorldView worldView = new WorldView(world, root);

        // Tạo controller
        WorldController controller = new WorldController(world, worldView);

        controller.registerBlockTextures();
        controller.registerEntityTextures();

        // ===== Spawn Rabbits =====

        controller.spawnEntity(
                new Rabbit(new EntityCoordinate(20, 20))
        );

        controller.spawnEntity(
                new Rabbit(new EntityCoordinate(25, 25))
        );

        controller.spawnEntity(
                new Rabbit(new EntityCoordinate(30, 20))
        );

        // ===== Spawn Wolves =====

        controller.spawnEntity(
                new Wolf(new EntityCoordinate(60, 60))
        );

        controller.spawnEntity(
                new Wolf(new EntityCoordinate(65, 65))
        );

        // JavaFX
        Scene scene = new Scene(root, 1200, 800);

        stage.setTitle("Predator-Prey Test");
        stage.setScene(scene);
        stage.show();

        // Start simulation
        worldView.startRendering();
        controller.startUpdateWorldService(30);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
