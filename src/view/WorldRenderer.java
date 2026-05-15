package view;

import javafx.animation.AnimationTimer;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.block.BlockModel;
import model.world.WorldModel;

import java.nio.file.Path;
import java.util.List;

/**
 * Lazy-loading Image of difference block
 */
public class WorldRenderer {
    GuiBlockView guiBlockView = new GuiBlockView();
    WorldModel worldModel;
    GridPane worldGridPane;
    private ImageView[][] imageViews;

    private ScheduledService<Object> updateWorldService = new ScheduledService<Object>() {
        @Override
        protected Task<Object> createTask() {
            return new Task<Object>() {
                @Override
                protected Object call() throws Exception {
                    if(worldModel == null) return null;
                    worldModel.update();
                    return null;
                }
            };
        }
    };

    private AnimationTimer rerenderingTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            renderWorld();
        }
    };

    public WorldRenderer(WorldModel worldModel, GridPane worldGridPane) {
        this.worldModel = worldModel;
        this.worldGridPane = worldGridPane;
        imageViews = new ImageView[worldModel.getWidth()][worldModel.getLength()];
        setUpGrid();
    }

    public void startUpdateWorldService() {
        updateWorldService.start();
    }

    public void startRendering(){
        rerenderingTimer.start();
    }

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
}
