package view;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.block.BlockModel;
import model.world.WorldModel;
import view.block.BlockTextureMap;
import view.entity.EntityView;
import view.entity.EntityTextureMap;
import view.entity.GuiEntityView;

import java.util.List;

/**
 * Render the world.
 * The world terrain is rendered as a grid. Each block occupy one cell of the grid.
 */
public class WorldView {
    BlockTextureMap blockTextureMap = BlockTextureMap.getInstance();
    EntityTextureMap entityTextureMap = new EntityTextureMap();
    GuiEntityView guiEntityView;
    WorldModel worldModel;
    GridPane worldGridPane;
    AnchorPane entityPane;
    private ImageView[][] imageViews;


    // AnimationTimer is an abstract class that represent animation in JavaFX application
    // The `handle()` method is called each frame.
    // Reference for AnimationTimer: https://openjfx.io/javadoc/26/javafx.graphics/javafx/animation/AnimationTimer.html
    private final AnimationTimer rerenderingTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            renderWorld();
//            renderEntity();
            guiEntityView.refresh();
        }
    };

    /**
     * Manage the rendering of a worldModel in a GridPane element.
     * @param worldModel The model to render
     * @param worldGridPane The target element
     */
    public WorldView(WorldModel worldModel, GridPane worldGridPane, AnchorPane entityPane) {
        this.worldModel = worldModel;
        this.worldGridPane = worldGridPane;
        this.entityPane = entityPane;
        imageViews = new ImageView[worldModel.getWidth()][worldModel.getLength()];
        guiEntityView = new GuiEntityView(entityTextureMap, entityPane);
        setUpGrid();
    }

    /**
     * Start the rendering loop
     */
    public void startRendering(){
        rerenderingTimer.start();
    }

    /**
     * Set up the world's grid. Each cell is an ImageView that can render an Image to the screen.
     */
    public void setUpGrid(){
       if(worldGridPane == null) return;
       worldGridPane.getChildren().clear();
       int width = worldModel.getWidth();
       int length = worldModel.getLength();
       for(int x = 0; x < width; x++){
           for(int y = 0; y < length; y++){
               imageViews[x][y] = new ImageView();
               worldGridPane.add(imageViews[x][y], x, y);
           }
       }
    }

    /**
     * Render the world to the gridPane.
     * Each time, only the image of each ImageView in the cell is changed,
     * instead of re-render the whole grid, which saves performance
     * significantly.
     * Entities will be rendered on top of the grid. (unimplemented)
     */
    public void renderWorld(){
        if(worldGridPane == null) return;
        BlockModel[][] blocksData = worldModel.getBlocksData();
        for(int x = 0; x < worldModel.getWidth();x++){
            for(int y = 0; y < worldModel.getLength();y++){
                if(blocksData[x][y] == null) {
                    IO.println("null blockModel at " + x + "; " +y);
                    continue;
                }
                imageViews[x][y].setImage(blockTextureMap.getBlockTexture(blocksData[x][y]));
            }
        }
    }

    public void renderEntity(){
        entityPane.getChildren().clear();
        List<EntityView> allEntityRenders =   guiEntityView.getRenderers();
        allEntityRenders.forEach(entityView -> {
            entityView.updateScreenPosition(16, 0, 0);
            ImageView imageView = entityView.getSprite();
            entityPane.getChildren().add(imageView);
        });
    }

    public GridPane getWorldGridPane() {
        return worldGridPane;
    }

    public GuiEntityView getGuiEntityView() {
        return guiEntityView;
    }

}
