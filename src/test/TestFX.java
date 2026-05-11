package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.block.BlockModel;
import model.world.WorldModel;
import view.BlockView;

public class TestFX extends Application {
    int worldWidth = 50;
    int worldHeight = 20;

    WorldModel world = new WorldModel(worldWidth, worldHeight);
    BlockView blockView = new BlockView();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        world.generateTerrain();
        String javaVersion = System.getProperty("java.version");
        String javafxVersion =  System.getProperty("javafx.version");
        Label l = new Label(worldView(worldWidth, worldHeight));
        l.setFont(Font.font("Roboto Mono"));
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setTitle("Wild Life Simulation");
        stage.setScene(scene);
        stage.show();
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
