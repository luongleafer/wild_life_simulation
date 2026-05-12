package test;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.block.BlockModel;
import model.world.WorldModel;
import view.BlockView;

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

    ScheduledService<Integer> tickUpdateService = new ScheduledService() {

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

    ScheduledService<Integer> updateWorldService = new ScheduledService() {
        @Override
        protected Task createTask() {
            return new  Task<Object>(){
                @Override
                protected Object call() throws Exception {
                    world.update();
                    return null;
                }
            };
        }
    };

    private Label countLabel = new Label("Counting...");
    private Label textWorld = new Label();
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
           countLabel.setText("Tick Count: " + ticks);
           textWorld.setText(worldView(worldWidth, worldHeight));
        }
    };

    @Override
    public void start(Stage stage) throws Exception {
        tickUpdateService.setPeriod(Duration.seconds(1.0/tps));
        tickUpdateService.start();
        updateWorldService.setPeriod(Duration.seconds(1.0/tps));
        updateWorldService.start();
        world.generateTerrain();
        String javaVersion = System.getProperty("java.version");
        String javafxVersion =  System.getProperty("javafx.version");
        textWorld.setFont(Font.font("Roboto Mono"));
        Scene scene = new Scene(new VBox(textWorld, countLabel), 640, 480);

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
}
