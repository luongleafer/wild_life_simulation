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
    
   @FXML
    private Label TypeLabel;
   @FXML
    private Label HungerLabel;
   @FXML
    private Label ThirstLabel;
   @FXML
    private Label EnergyLabel;

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
    @FXML
    private ListView<String> entityStatsListView;
    private EntityModel selectedEntity;
    
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

        	        if(running) {
        	            worldController.changeTPS(tps);
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
        Timeline infoUpdater =
                new Timeline(
                        new KeyFrame(
                                Duration.millis(200),
                                e -> updateSelectedEntityInfo()
                        )
                );

        infoUpdater.setCycleCount(
                Timeline.INDEFINITE
        );

        infoUpdater.play();
        if(selectedEntity == null) {
            System.out.println("No entity selected");
        }
        
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

            updateEntityStats();

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
        updateEntityStats();

        System.out.println("All entities removed");
    }
    private void handleMapClick(MouseEvent e) {

        int tileX =
                (int)e.getX()
                / WorldController.WORLD_TILE_SIZE;

        int tileY =
                (int)e.getY()
                / WorldController.WORLD_TILE_SIZE;

        selectedX = tileX;
        selectedY = tileY;

        BlockPosition.setText(
                "Block Position: "
                + tileX + ", " + tileY
        );

        // tìm entity tại vị trí click
        selectedEntity = null;

        for(EntityModel entity : worldModel.getEntities()) {

            if(entity.getPosition().getPosX() == tileX &&
               entity.getPosition().getPosY() == tileY) {

                selectedEntity = entity;

                System.out.println(
                    "Selected: " +
                    entity.getEntityType()
                );

                break;
            }
        }

        updateSelectedEntityInfo();
    }
    private void updateEntityStats() {

        long wolfCount =
                worldModel.getEntities().stream()
                        .filter(e -> e instanceof Wolf)
                        .count();

        long pigCount =
                worldModel.getEntities().stream()
                        .filter(e -> e instanceof Pig)
                        .count();

        long cowCount =
                worldModel.getEntities().stream()
                        .filter(e -> e instanceof Cow)
                        .count();

        entityStatsListView.getItems().setAll(
                "Wolf: " + wolfCount,
                "Pig: " + pigCount,
                "Cow: " + cowCount
        );
    }
    private void updateSelectedEntityInfo() {
        System.out.println("Update panel");

        if(selectedEntity == null) {

            TypeLabel.setText("Type:");
            PositionLabel.setText("Position:");
            EnergyLabel.setText("Health:");
            HungerLabel.setText("Hunger:");
            ThirstLabel.setText("Thirst:");

            return;
        }

        TypeLabel.setText(
                "Type: " +
                selectedEntity.getEntityType()
        );

        PositionLabel.setText(
                "Position: "
                + selectedEntity.getPosition().getPosX()
                + ", "
                + selectedEntity.getPosition().getPosY()
        );

        EnergyLabel.setText(
                "Health: "
                + selectedEntity.getHealth()
        );

        if(selectedEntity instanceof AnimalModel animal) {

            HungerLabel.setText(
                    "Hunger: "
                    + animal.getHunger()
            );

            ThirstLabel.setText(
                    "Thirst: "
                    + animal.getThirst()
            );
        }
    }
    
}