package screenController;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import model.block.BlockModel;
import model.entity.AnimalModel;
import javafx.scene.layout.*;
import model.world.WorldModel;
import java.util.List;
import model.entity.EntityModel;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.canvas.GraphicsContext;
import model.animals.*;
import java.util.Random;
import model.entity.*;
import model.generation.*;
import controller.WorldController;
import javafx.scene.input.MouseEvent;
import model.entity.EntityCoordinate;
import javafx.scene.layout.AnchorPane;
import view.WorldView;
import model.block.*;
public class ScreenController {
    @FXML
    private AnchorPane worldPane;
    @FXML
    private Slider tickSpeedSlider;

    @FXML
    private Label tickSpeedLabel;
    private WorldView worldView;
    @FXML
    private Button startButton;
    @FXML
    private Label PositionLabel;

    private WorldModel worldModel;
    
    private boolean running = false;
	
    private final WorldController worldController =
            WorldController.getController();
    
    @FXML
    private ChoiceBox<String> animalChoiceBox;

    @FXML
    private ChoiceBox<String> blockChoiceBox;

    @FXML
    private Button spawnButton;
    
    @FXML
    private Button insertButton;
    @FXML
    private Label BlockPosition;
    
    private int selectedX = -1;
    private int selectedY = -1;
    private GraphicsContext gc;
    @FXML
    public void initialize() {

        tickSpeedSlider.setMin(1);
        tickSpeedSlider.setMax(20);
        tickSpeedSlider.setValue(5);

        tickSpeedLabel.setText("Tick speed: 5 TPS");

        tickSpeedSlider.valueProperty().addListener(
            (obs, oldVal, newVal) -> {

                int tps = newVal.intValue();

                tickSpeedLabel.setText(
                        "Tick speed: " + tps + " TPS"
                );

                if (running) {

                    worldController.stopUpdateWorldService();

                    worldController.startUpdateWorldService(tps);
                }
            }
        );
        animalChoiceBox.getItems().addAll(
                "Pig",
                "Wolf",
                "Cow"
        );

        animalChoiceBox.setValue("Pig");
        blockChoiceBox.getItems().addAll(
                "Grass",
                "Water",
                "Sand",
                "Wood",
                "Dirt",
                "Mud"
        );

        blockChoiceBox.setValue("Grass");
        worldPane.setOnMouseClicked(
                this::handleMapClick
        );
        worldModel = new WorldModel(50,50);
        worldModel.generateTerrain();
        worldView =
        	    new WorldView(
        	        worldModel,
        	        worldPane
        	    );

        worldController.setWorldModel(worldModel);
        worldController.setWorldView(worldView);
        worldController.registerBlockTextures();

        worldController.registerEntityTextures();
        worldController.registerSound();
        worldView.startRendering();
        
    }
    @FXML
    private void handleStart() {

        if (!running) {

            worldController.startUpdateWorldService(
                    (long) tickSpeedSlider.getValue()
            );

            running = true;

            startButton.setText("Pause");
        }
        else {

            worldController.stopUpdateWorldService();

            running = false;

            startButton.setText("Start");
        }
    }
    @FXML
    private void handleSpawn() {

        String selected = animalChoiceBox.getValue();

        if(selected == null) {
            return;
        }

        Random random = new Random();

        int x = random.nextInt(worldModel.getWidth());

        int y = random.nextInt(worldModel.getLength());

        EntityCoordinate position =
                new EntityCoordinate(x, y);

        EntityModel entity = null;

        switch(selected) {

            case "Pig":
                entity = new Pig(position);
                break;

            case "Wolf":
                entity = new Wolf(position);
                break;

            case "Cow":
                entity = new Cow(position);
                break;
        }

        if(entity != null) {

        	worldController.spawnEntity(entity);

            System.out.println(
                    "Spawned " +
                    entity.getEntityType()
            );
        }
    }

    @FXML
    private void handleInsert() {

        String blockType =
                blockChoiceBox.getValue();

        if(blockType == null) {
            return;
        }

        try {

            BlockModel block =
                    BlockFactory.create(
                            blockType.toLowerCase(),
                            selectedX,
                            selectedY,
                            0
                    );

            worldController.placeBlock(
                    block,
                    selectedX,
                    selectedY
            );

        }
        catch(Exception ex) {

            ex.printStackTrace();
        }
    }

    @FXML
    private void handleKillAll() {

        worldController.killAllEntities();

        System.out.println("All entities removed");
    }
    private void handleMapClick(MouseEvent e) {

        selectedX =
                (int)e.getX()
                / WorldController.WORLD_TILE_SIZE;

        selectedY =
                (int)e.getY()
                / WorldController.WORLD_TILE_SIZE;

        BlockPosition.setText(
                "Block Position: "
                + selectedX
                + ", "
                + selectedY
        );
    }
    
}