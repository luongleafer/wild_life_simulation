package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.world.WorldModel;
import view.WorldRenderer;

public class TestWorldRenderer extends Application {
    WorldModel model = new WorldModel(50, 50);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GridPane grid = new GridPane();
        WorldRenderer renderer = new WorldRenderer(model,grid);
        model.generateTerrain();

        for(int x = 0; x < model.getWidth(); x++){
            for(int y = 0; y < model.getLength();y++){
                if(model.getBlocksData()[x][y] == null){
                    IO.println("Null at " + x + " ; " + y);
                }
            }
        }

        Scene scene = new Scene(new VBox(grid), 640, 480);
        renderer.registerBlockTextures();
        //renderer.renderWorld();
        stage.setTitle("Wild Life Simulation");
        stage.setScene(scene);
        stage.show();
    }
}
