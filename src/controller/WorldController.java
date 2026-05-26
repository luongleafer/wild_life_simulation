package controller;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import model.block.BlockModel;
import model.world.WorldModel;
import view.GuiBlockView;

import java.nio.file.Path;
import java.util.List;

public class WorldController {
    WorldModel worldModel;

    // Update world in a separate thread.
    // ScheduledService is used to define a task that is run periodically
    // The Object is declared with the use of anonymous class.
    // (I use this syntax because I'm lazy, might change to concrete class in the future)
    // Reference for anonymous class: https://dev.java/learn/classes-objects/nested-classes/#anonymous
    // Reference for ScheduledService: https://openjfx.io/javadoc/26/javafx.graphics/javafx/concurrent/ScheduledService.html
    private final ScheduledService<Object> updateWorldService = new ScheduledService<Object>() {
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

    public WorldController(WorldModel model) {
        this.worldModel = model;
    }

    /**
     * Start updating the world
     * @param tps: Tick speed
     */
    public void startUpdateWorldService(long tps) {
        updateWorldService.setPeriod(Duration.seconds(1.0/tps));
        updateWorldService.start();
    }

    public void stopUpdateWorldService() {
        updateWorldService.cancel();
    }

    public void registerBlockTextures(){
        GuiBlockView guiBlockView = GuiBlockView.getInstance();
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

    /**
     * Attempt to place block in the world
     * @param blockType The type of block to place
     * @param xPos The x position to place block, 0 <= x < world's width
     * @param yPos The y position to place block, 0 <= y < world's length
     */
    public void placeBlock(Class<? extends BlockModel> blockType, int xPos, int yPos) {
        if(xPos < 0 || yPos < 0) return; // invalid coordinate
        if(xPos >= worldModel.getWidth() || yPos >= worldModel.getLength()) return;
        BlockModel newBlock = null;
        try {
            newBlock = blockType.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(xPos, yPos, 0);
        }
        catch(Exception e) {
            IO.println("Exception when trying to place block: " + e.getMessage());
        }
        if(newBlock != null){
            worldModel.placeBlock(newBlock);
        }
    }
}
