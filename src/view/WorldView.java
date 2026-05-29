package view;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.block.BlockModel;
import model.world.WorldModel;
import view.block.BlockTextureMap;
import view.block.TerrainView;
import view.entity.EntityTextureMap;
import view.entity.AllEntitiesView;

/**
 * Render the world.
 * The world terrain is rendered as a grid. Each block occupy one cell of the grid.
 */
public class WorldView {
    BlockTextureMap blockTextureMap = BlockTextureMap.getInstance();
    EntityTextureMap entityTextureMap = new EntityTextureMap();
    AllEntitiesView allEntitiesView;
    TerrainView terrainView;
    WorldModel worldModel;
    private ImageView[][] imageViews;


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
     * @param worldGridPane The target element
     */
    public WorldView(WorldModel worldModel, GridPane worldGridPane, AnchorPane entityPane) {
        this.worldModel = worldModel;
        imageViews = new ImageView[worldModel.getWidth()][worldModel.getLength()];
        allEntitiesView = new AllEntitiesView(entityTextureMap, entityPane);
        terrainView = new TerrainView(worldModel.getWidth(), worldModel.getLength(), worldGridPane, blockTextureMap);
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

}
