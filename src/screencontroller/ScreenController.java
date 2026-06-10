package screencontroller;
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
public class ScreenController {


	    @FXML
	    private Slider tickSpeedSlider;

	    @FXML
	    private ChoiceBox<String> animalChoiceBox;

	    @FXML
	    private ChoiceBox<String> blockChoiceBox;

	    @FXML
	    private Button spawnButton;

	    @FXML
	    private Button insertButton;

	    @FXML
	    private Canvas canvas;

	    @FXML
	    private Label TypeLabel;

	    @FXML
	    private Label PositionLabel;

	    @FXML
	    private Label HungerLabel;

	    @FXML
	    private Label ThirstLabel;

	    @FXML
	    private Label EnergyLabel;

	    private WorldModel world;

	    private EntityModel selectedEntity;

	    private Timeline timeline;
	    
	    private Image grassTexture;
	    private Image waterTexture;
	    private Image dirtTexture;

	    private Image cowTexture;
	    private Image wolfTexture;
	    private Image pigTexture;

    
    @FXML
    public void initialize() {

    	grassTexture =
    		    new Image(getClass().getResourceAsStream("/assets/homemade/GrassBlock.png"));

    		waterTexture =
    		    new Image(getClass().getResourceAsStream("/assets/homemade/water block.png"));

    		dirtTexture =
    		    new Image(getClass().getResourceAsStream("/assets/homemade/DirtBlock.png"));

    		cowTexture =
    		    new Image(getClass().getResourceAsStream("/assets/minecraft_based/cow.png"));

    		wolfTexture =
    		    new Image(getClass().getResourceAsStream("/assets/minecraft_based/wolf.png"));

    		pigTexture =
    		    new Image(getClass().getResourceAsStream("/assets/minecraft_based/pig.png"));
    	    world = new WorldModel(100,100);

    	    world.generateTerrain();

    	    setupChoiceBoxes();

    	    redraw();
    }
    
    private void setupChoiceBoxes() {

        animalChoiceBox.getItems().addAll(
                "Cow",
                "Wolf",
                "Pig"
        );

        blockChoiceBox.getItems().addAll(
                "Grass",
                "Water",
                "Sand",
                "Cobble",
                "Dirt",
                "Mud",
                "Sapling",
                "Seed",
                "Tree",
                "Wood"
        );

        animalChoiceBox.getSelectionModel().selectFirst();
        blockChoiceBox.getSelectionModel().selectFirst();
    }
    
    @FXML
    private void handleSpawn() {

        String animal = animalChoiceBox.getValue();

        EntityModel entity = null;

        Random random = new Random();

        int x = random.nextInt(world.getWidth());
        int y = random.nextInt(world.getLength());

        switch (animal) {

            case "Cow":
                entity = new Cow(
                        new EntityCoordinate(x, y)
                );
                break;

            case "Wolf":
                entity = new Wolf(
                        new EntityCoordinate(x, y)
                );
                break;

            case "Pig":
                entity = new Pig(
                        new EntityCoordinate(x, y)
                );
                break;
        }

        if(entity != null) {
            world.getEntities().add(entity);
        }

        redraw();
    }
    @FXML
    private void handleInsert() {

        String selected = blockChoiceBox.getValue();

        Random random = new Random();

        int x = random.nextInt(world.getWidth());
        int y = random.nextInt(world.getLength());

        BlockModel block = null;

        switch(selected) {

            case "Grass":
                block = new GrassBlock(x,y,0);
                break;

            case "Water":
                block = new WaterBlock(x,y,0);
                break;

            case "Dirt":
                block = new DirtBlock(x,y,0);
                break;

            case "Mud":
                block = new MudBlock(x,y,0);
                break;

            case "Wood":
                block = new WoodBlock(x,y,0);
                break;
        }

        if(block != null) {
            world.placeBlock(block);
        }

        redraw();
    }
    
    @FXML
    private void handleStart() {

        if(timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(50),
                        e -> {

                            world.update();

                            redraw();
                        }
                )
        );

        timeline.setCycleCount(
                Timeline.INDEFINITE
        );

        timeline.play();
    }
    private void redraw() {

        GraphicsContext gc =
                canvas.getGraphicsContext2D();

        gc.clearRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
        );

        drawTerrain(gc);

        drawEntities(gc);
    }
    private void drawTerrain(GraphicsContext gc) {

        BlockModel[][] map =
                world.getBlocksData();

        double cellSize = 5;

        for(int x = 0 ; x < world.getWidth() ; x++) {

            for(int y = 0 ; y < world.getLength() ; y++) {

                BlockModel block = map[x][y];

                if(block == null)
                    continue;

                Image texture = null;

                switch(block.getBlockType()) {

                    case "grass":
                        texture = grassTexture;
                        break;

                    case "water":
                        texture = waterTexture;
                        break;

                    case "dirt":
                        texture = dirtTexture;
                        break;
                }

                if(texture != null) {

                    gc.drawImage(
                        texture,
                        x * cellSize,
                        y * cellSize,
                        cellSize,
                        cellSize
                    );
                }
            }
        }
    }
    private void drawEntities(GraphicsContext gc) {

        double cellSize = 5;

        for(EntityModel entity : world.getEntities()) {

            Image texture = null;

            if(entity instanceof Cow) {
                texture = cowTexture;
            }
            else if(entity instanceof Wolf) {
                texture = wolfTexture;
            }
            else if(entity instanceof Pig) {
                texture = pigTexture;
            }

            if(texture != null) {

                gc.drawImage(
                    texture,
                    entity.getPosition().getPosX() * cellSize,
                    entity.getPosition().getPosY() * cellSize,
                    cellSize,
                    cellSize
                );
            }
        }
    }
    
}