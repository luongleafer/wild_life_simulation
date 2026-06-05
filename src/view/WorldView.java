package view;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.world.WorldModel;
import view.block.BlockTextureMap;
import view.block.TerrainView;
import view.entity.EntityTextureMap;
import view.entity.AllEntitiesView;

import java.nio.file.Paths;

/**
 * Render the world.
 * The world terrain is rendered as a grid. Each block occupy one cell of the grid.
 */
public class WorldView {
    AllEntitiesView allEntitiesView;
    TerrainView terrainView;
    WorldModel worldModel;
    Pane rootPane;

    AudioClip test = new AudioClip(Paths.get("assets/audio/grass1.mp3").toUri().toString());


    // AnimationTimer is an abstract class that represent animation in JavaFX application
    // The `handle()` method is called each frame.
    // Reference for AnimationTimer: https://openjfx.io/javadoc/26/javafx.graphics/javafx/animation/AnimationTimer.html
    private final AnimationTimer rerenderingTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            terrainView.refresh(worldModel.getBlocksData());
            allEntitiesView.refresh();
        }
    };

    /**
     * Manage the rendering of a worldModel in a GridPane element.
     * @param worldModel The model to render
     * @param root The Pane to render to
     */
    public WorldView(WorldModel worldModel, Pane root) {
        root.getChildren().clear();
        this.rootPane = root;
        this.worldModel = worldModel;
        AnchorPane entityPane = new AnchorPane();
        GridPane worldGridPane = new GridPane();
        root.getChildren().add(worldGridPane);
        root.getChildren().add(entityPane);
        allEntitiesView = new AllEntitiesView(null, entityPane);
        terrainView = new TerrainView(worldModel.getWidth(), worldModel.getLength(), worldGridPane, null);
    }

    /**
     * Start the rendering loop
     */
    public void startRendering(){
        rerenderingTimer.start();
    }

    public AllEntitiesView getAllEntitiesView() {
        return allEntitiesView;
    }

    public TerrainView getTerrainView() {
        return terrainView;
    }

}
