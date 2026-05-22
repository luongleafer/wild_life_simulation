package view;

import javafx.animation.AnimationTimer;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import model.block.BlockModel;
import model.entity.EntityCoordinate;
import model.entity.Wolf;
import model.world.WorldModel;
import view.entity.EntityRenderer;
import view.entity.EntityTextureMap;
import view.entity.GuiEntityView;

import java.nio.file.Path;
import java.util.List;

/**
 * Render the world.
 * The world terrain is rendered as a grid. Each block occupy one cell of the grid.
 */
public class WorldRenderer {
    GuiBlockView guiBlockView = GuiBlockView.getInstance();
    EntityTextureMap entityTextureMap = EntityTextureMap.getInstance();
    GuiEntityView guiEntityView = GuiEntityView.getInstance();
    WorldModel worldModel;
    GridPane worldGridPane;
    AnchorPane entityPane;
    private ImageView[][] imageViews;

    // Update world in a separate thread.
    // ScheduledService is used to define a task that is run periodically
    // The Object is declared with the use of anonymous class.
    // (I use this syntax because I'm lazy, might change to concrete class in the future)
    // Reference for anonymous class: https://dev.java/learn/classes-objects/nested-classes/#anonymous
    // Reference for ScheduledService: https://openjfx.io/javadoc/26/javafx.graphics/javafx/concurrent/ScheduledService.html
    // Note: this should belong to a Controller object, will change in the future.
    private ScheduledService<Object> updateWorldService = new ScheduledService<Object>() {
        @Override
        protected Task<Object> createTask() {
            return new Task<Object>() {
                @Override
                protected Object call() throws Exception {
                    if(worldModel == null) return null;
//                    worldModel.generateTerrain();
                    worldModel.update();
                    return null;
                }
            };
        }
    };

    // AnimationTimer is an abstract class that represent animation in JavaFX application
    // The `handle()` method is called each frame.
    // Reference for AnimationTimer: https://openjfx.io/javadoc/26/javafx.graphics/javafx/animation/AnimationTimer.html
    private AnimationTimer rerenderingTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            renderWorld();
            renderEntity();
        }
    };

    /**
     * Manage the rendering of a worldModel in a GridPane element.
     * @param worldModel The model to render
     * @param worldGridPane The target element
     */
    public WorldRenderer(WorldModel worldModel, GridPane worldGridPane, AnchorPane entityPane) {
        this.worldModel = worldModel;
        this.worldGridPane = worldGridPane;
        this.entityPane = entityPane;
        imageViews = new ImageView[worldModel.getWidth()][worldModel.getLength()];
        setUpGrid();
    }

    /**
     * Start updating the world
     * @param tps: Tick speed
     */
    public void startUpdateWorldService(long tps) {
        updateWorldService.setPeriod(Duration.seconds(1.0/tps));
        updateWorldService.start();
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
                imageViews[x][y].setImage(guiBlockView.getBlockTexture(blocksData[x][y]));
            }
        }
    }

    public void renderEntity(){
        entityPane.getChildren().clear();
        List<EntityRenderer> allEntityRenders =   guiEntityView.getRenderers();
        allEntityRenders.forEach(entityRenderer -> {
            entityRenderer.updateScreenPosition(1,0,0);
            ImageView imageView = new ImageView(entityRenderer.getSprite());
            imageView.setLayoutX(entityRenderer.getScreenX());
            imageView.setLayoutY(entityRenderer.getScreenY());
            entityPane.getChildren().add(imageView);
        });
    }

    public GridPane getWorldGridPane() {
        return worldGridPane;
    }

    public void registerBlockTextures(){
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

    public void registerEntityTextures(){
        entityTextureMap.registerEntity(new Wolf(new EntityCoordinate(0,0)),
                                        Path.of("assets/acacia_sapling.png"));
    }
}
