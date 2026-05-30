package controller;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import model.block.BlockModel;
import model.block.BlockModels;
import model.entity.EntityModel;
import model.world.WorldModel;
import view.block.BlockTextureMap;
import view.WorldView;
import view.entity.EntityTextureMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controls everything
 */
public class WorldController {
    WorldModel worldModel;
    WorldView worldView;

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
//                    updateWorld();
                    refreshEntityViews();
                    worldModel.update();
                    return null;
                }
            };
        }
    };

    public WorldController(WorldModel model, WorldView worldView) {
        this.worldModel = model;
        this.worldView = worldView;
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

    /**
     * Remove views for dead entities
     */
    private void refreshEntityViews(){
        worldModel.getDeadEntities().forEach(
                entityModel ->
                        worldView.getAllEntitiesView().removeView(entityModel)
        );
    }

    public void registerBlockTextures(){
        BlockTextureMap blockTextureMap = new BlockTextureMap();
        blockTextureMap.registerTextures("dirt", List.of(
                Path.of("assets/dirt.png")
        ));
        blockTextureMap.registerTextures("grass", List.of(
                Path.of("assets/grass_block_top.png")
        ));
        blockTextureMap.registerTextures("sand", List.of(
                Path.of("assets/sand.png")
        ));
        blockTextureMap.registerTextures("water", List.of(
                Path.of("assets/water_still_oneblock.png")
        ));
        blockTextureMap.registerTextures("wood", List.of(
                Path.of("assets/oak_log.png")
        ));
        blockTextureMap.registerTextures("mud", List.of(
                Path.of("assets/mud.png")
        ));
        worldView.getTerrainView().setTextureMap(blockTextureMap);
    }

    public void registerEntityTextures(){
        EntityTextureMap entityTextureMap = new EntityTextureMap();
        entityTextureMap.registerEntity("pig",
                                        Paths.get("assets/pig.png"), 1);
        entityTextureMap.registerEntity("wolf",
                                        Paths.get("assets/wolf.png"),1
        );

        entityTextureMap.registerEntity("cow",
                                        Paths.get("assets/calf.png"),0
        );

        entityTextureMap.registerEntity("cow",
                                        Paths.get("assets/cow.png"),1
        );
        worldView.getAllEntitiesView().setEntityTextureMap(entityTextureMap);
    }

    /**
     * Attempt to place a new block in the world
     * @param block The type of block be cloned and place
     * @param xPos The x position to place block, 0 <= x < world's width
     * @param yPos The y position to place block, 0 <= y < world's length
     */
    public <T extends BlockModel> void placeBlock(T block, int xPos, int yPos) {
        if(xPos < 0 || yPos < 0) return; // invalid coordinate
        if(xPos >= worldModel.getWidth() || yPos >= worldModel.getLength()) return;
        BlockModel newBlock = null;
        try {
            newBlock = BlockModels.from(block, xPos, yPos,0);
        }
        catch(Exception e) {
            IO.println("Exception when trying to place block: " + e.getMessage());
        }
        if(newBlock != null){
            worldModel.placeBlock(newBlock);
        }
    }

    /**
     * Spawn new Entity in the world
     * @param entity Entity to spawn
     */
    public void spawnEntity(EntityModel entity) {
        worldModel.getEntities().add(entity);
        worldView.getAllEntitiesView().addView(entity);
    }

}
