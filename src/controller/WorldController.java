package controller;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import model.block.BlockModel;
import model.block.BlockModels;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.world.WorldModel;
import view.GuiBlockView;
import view.entity.GuiEntityView;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Controls everything
 */
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
                    updateWorld();
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

    /**
     * Remove views for dead entities
     */
    private void refreshEntities(){
        worldModel.getEntities().stream().filter(entityModel -> entityModel.getHealth() <= 0).forEach(entityModel -> {
            GuiEntityView.getInstance().removeView(entityModel);
        });
    }

    public void registerBlockTextures(){
        GuiBlockView guiBlockView = GuiBlockView.getInstance();
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
     * @param <T> Type of entity
     */
    public <T extends EntityModel> void spawnEntity(T entity) {
        worldModel.getEntities().add(entity);
        GuiEntityView.getInstance().addView(entity);
    }

    /**
     * List all Entities in a circle area
     * @param origin The center of the area
     * @param radius The radius of the area
     * @return All Entities in the area
     */
    List<EntityModel> getEntitiesInAnArea(EntityCoordinate origin, double radius){
        return worldModel.getEntities().stream().filter(
                entityModel -> entityModel.getPosition().distance(origin) <= radius
        ).sorted(Comparator.comparing(entityModel -> entityModel.getPosition().distance(origin))).toList();
    }

    /**
     * Perform world update
     */
    private void updateWorld(){

        if(worldModel == null) return;
        worldModel.advanceTickCount();
        // remove the view and model of dead Entities
        refreshEntities();
        worldModel.getEntities().removeIf(entityModel -> entityModel.getHealth() <= 0);
        // Every entity age up after each tick
        worldModel.getEntities().forEach(EntityModel::ageUp);
        // Entity interact with other entity
        for (EntityModel entity : worldModel.getEntities()) {
            List<EntityModel> surrounding = getEntitiesInAnArea(entity.getPosition(), 70);
            entity.Interact(surrounding);
        }
        // Every animal move somewhere
        worldModel.getEntities().stream()
                .filter(entity -> entity instanceof AnimalModel)
                .map(entity -> (AnimalModel) entity)
                .forEach(AnimalModel::move);
        // keep entities in bound
        worldModel.getEntities().forEach(entityModel -> {
            EntityCoordinate position = entityModel.getPosition();
            if(position.getPosX() > worldModel.getWidth()){
                position.setPosX(worldModel.getWidth() - 2);
            }
            if(position.getPosX() < 0){
                position.setPosX(0);
            }
            if(position.getPosY() > worldModel.getLength()){
                position.setPosY(worldModel.getLength() - 2);
            }
            if(position.getPosY() < 0){
                position.setPosY(0);
            }
        });
    }

}
