package main;

import controller.ControlPanelController;
import controller.WorldController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.entity.EntityCoordinate;
import model.entity.EntityFactory;
import model.world.WorldModel;
import view.WorldView;
import view.audio.SoundEngine;

import java.net.URL;
import java.nio.file.Path;
import java.util.Random;

public class Main extends Application {
    private static final int SIMULATION_TPS = 20;
    private final WorldModel model = new WorldModel(80, 60);
    private final Random random = new Random();
    private ControlPanelController controlPanelController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        StackPane worldPane = new StackPane();
        worldPane.setPrefSize(model.getWidth() * WorldController.WORLD_TILE_SIZE,
                              model.getLength() * WorldController.WORLD_TILE_SIZE);
        WorldView renderer = new WorldView(model, worldPane);
        WorldController controller = WorldController.getController();
        controller.setWorldModel(model);
        controller.setWorldView(renderer);
        SoundEngine.initEngine();
        controller.registerBlocksAndEntities();
        model.generateTerrain();
        controller.setTexturePath("basic");
        controller.registerSound();
        seedInitialEntities(controller);
        controller.startUpdateWorldService(SIMULATION_TPS);
        renderer.startRendering();

        BorderPane root = new BorderPane();
        ScrollPane worldScroll = new ScrollPane(worldPane);
        worldScroll.setPannable(true);
        worldScroll.setFitToHeight(false);
        worldScroll.setFitToWidth(false);
        ScrollPane controlPanelScroll = new ScrollPane(loadControlPanel(controller));
        controlPanelScroll.setFitToWidth(true);
        controlPanelScroll.setFitToHeight(true);
        controlPanelScroll.setPannable(true);
        root.setCenter(worldScroll);
        root.setRight(controlPanelScroll);

        worldPane.setOnMouseClicked(mouseEvent ->
                                            controlPanelController.handleWorldClick(mouseEvent.getX(),
                                                                                    mouseEvent.getY()));

        Scene scene = new Scene(root, 1200, 760);

        stage.setOnCloseRequest(event -> {
            if(controlPanelController != null){
                controlPanelController.stop();
            }
            controller.stopUpdateWorldService();
        });
        stage.setTitle("Wild Life Simulation");
        stage.setScene(scene);
        stage.show();
    }

    private Parent loadControlPanel(WorldController controller) {
        try {
            URL fxmlUrl = getClass().getResource("/screen/ControlPanel.fxml");
            if(fxmlUrl == null){
                fxmlUrl = Path.of("src/screen/ControlPanel.fxml").toUri().toURL();
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent panel = loader.load();
            controlPanelController = loader.getController();
            controlPanelController.bind(model, controller, SIMULATION_TPS);
            return panel;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load screen/ControlPanel.fxml", exception);
        }
    }

    private void seedInitialEntities(WorldController controller) {
        for(int i = 0; i < 10; i++) {
            controller.spawnEntity(EntityFactory.create("wolf", new EntityCoordinate(5 * i, 5 * i)));
        }
        for(int i = 0; i < 20; i++) {
            controller.spawnEntity(EntityFactory.create("pig", new EntityCoordinate(random.nextDouble() * model.getWidth(), random.nextDouble() * model.getLength())));
        }
        for(int i = 0; i < 10; i++) {
            controller.spawnEntity(EntityFactory.create("cow", new EntityCoordinate(30, 30)));
        }
        for(int i = 0; i < 5; i++) {
            controller.spawnEntity(EntityFactory.create("elephant", new EntityCoordinate(random.nextDouble() * model.getWidth(), random.nextDouble() * model.getLength())));
        }
        for(int i = 0; i< 300;i++){
            int x = random.nextInt(model.getWidth());
            int y = random.nextInt(model.getLength());
            if(model.getBlocksData()[x][y].getBlockType().equals("grass")){
                controller.spawnEntity(EntityFactory.create("grass", new EntityCoordinate(x, y)));
            }
        }
    }

}
