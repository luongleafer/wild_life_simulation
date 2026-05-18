package test;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.block.BlockModel;
import model.generation.DirtBlock;
import model.world.WorldModel;
import view.BlockView;
import view.GuiBlockView;

import java.nio.file.Path;
import java.util.List;

public class TestFX extends Application {
    int worldWidth = 50;
    int worldHeight = 20;

    WorldModel world = new WorldModel(worldWidth, worldHeight);
    BlockView blockView = new BlockView();
    long tps = 20;

    public static void main(String[] args) {
        launch(args);
    }

    long ticks;

    ScheduledService<Integer> tickUpdateService = new ScheduledService<Integer>() {

        @Override
        protected Task createTask() {
            return new Task<Object>(){

                @Override
                protected Object call() throws Exception {
                    ticks++;
                    return null;
                }
            };
        }
    };

    ScheduledService<Integer> updateWorldService = new ScheduledService<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new  Task<Integer>(){
                @Override
                protected Integer call() throws Exception {
                    world.update();
                    return 0;
                }
            };
        }
    };

    private Label countLabel = new Label("Counting...");
    private Label textWorld = new Label();
    private GridPane gridPane = new GridPane();
    private GuiBlockView guiBlockView = GuiBlockView.getInstance();
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {

           countLabel.setText("Tick Count: " + ticks);
           textWorld.setText(worldView(worldWidth, worldHeight));
           renderWorld(worldWidth, worldHeight);
        }
    };

    private void registerBlockTextures(){
        guiBlockView.registerTextures("dirt", List.of(
                Path.of("assets/dirt.png")
        ));
        guiBlockView.registerTextures("grass", List.of(
                Path.of("assets/grass_block_top.png")
        ));
        guiBlockView.registerTextures("sand", List.of(
                Path.of("assets/sand.png")
        ));
        guiBlockView.registerTextures("water", List.of(
                Path.of("assets/water_still_oneblock.png")
        ));
        guiBlockView.registerTextures("wood", List.of(
                Path.of("assets/oak_log.png")
        ));
        guiBlockView.registerTextures("mud", List.of(
                Path.of("assets/mud.png")
        ));
    }

    private ImageView[][] imageViews = new ImageView[worldWidth][worldHeight];

    @Override
    public void start(Stage stage) throws Exception {
        registerBlockTextures();
        tickUpdateService.setPeriod(Duration.seconds(1.0/tps));
        tickUpdateService.start();
        updateWorldService.setPeriod(Duration.seconds(1.0/tps));
        updateWorldService.start();
        world.generateTerrain();
        String javaVersion = System.getProperty("java.version");
        String javafxVersion =  System.getProperty("javafx.version");
        textWorld.setFont(Font.font("Roboto Mono"));
        setGrid();
        renderWorld(worldWidth, worldHeight);
        Scene scene = new Scene(new VBox(countLabel, gridPane), 640, 480);
        stage.setTitle("Wild Life Simulation");
        stage.setScene(scene);
        stage.show();
        timer.start();
    }

    private String worldView(int width, int height) {
        StringBuilder sb = new StringBuilder();
        BlockModel[][] blocksData = world.getBlocksData();
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                sb.append(blockView.getBlockDisplay(blocksData[x][y]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void renderWorld(int width, int height) {
//        gridPane.getChildren().clear();
        BlockModel[][] blocksData = world.getBlocksData();
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if(blocksData[x][y] != null) {
                    imageViews[x][y].setImage(guiBlockView.getBlockTexture(blocksData[x][y]));
                }
                else{
                    imageViews[x][y].setImage(guiBlockView.getBlockTexture(new DirtBlock(0,0,0)));
                }
            }
        }

    }

    private void setGrid(){
        for(int y = 0; y < worldHeight; y++){
            for(int x = 0; x < worldWidth; x++){
                imageViews[x][y] = new ImageView();
                gridPane.add(imageViews[x][y], x, y);
            }
        }
    }
}
