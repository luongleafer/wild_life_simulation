package controller;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import model.animals.Wolf;
import model.block.BlockModel;
import model.block.BlockFactory;
import model.entity.EntityModel;
import model.world.WorldModel;
import view.audio.SoundEngine;
import view.block.BlockTextureMap;
import view.WorldView;
import view.entity.EntityTextureMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controls everything
 */
public class WorldController {
    private final List<EntityModel> waitToSpawn;
    public static int WORLD_TILE_SIZE = 16;
    public static WorldController controller;

    public static WorldController getController() {
        if(controller == null) {
            controller = new WorldController();
        }
        return controller;
    }

    public void setWorldModel(WorldModel worldModel) {
        this.worldModel = worldModel;
    }

    public void setWorldView(WorldView worldView) {
        this.worldView = worldView;
    }

    WorldModel worldModel;
    WorldView worldView;
    Random random = new Random();

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
                    playSoundEvents();
                    if(!waitToSpawn.isEmpty()) {
                        waitToSpawn.forEach(WorldController.this::spawnEntity);
                        waitToSpawn.clear();
                    }
                    return null;
                }
            };
        }
    };

    private WorldController(){
        waitToSpawn = new ArrayList<>();

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
                entityModel -> {
                    worldView.getAllEntitiesView().removeView(entityModel);
                    SoundEngine.getEngine().playSound(entityModel.getEntityType() + "_death");
                }
        );
    }

    private void playSoundEvents(){
        if(worldModel.getEntities().stream()
                .anyMatch(entityModel ->
                                  entityModel instanceof Wolf wolf && wolf.hasJustAttacked())){
            SoundEngine.getEngine().playSound("wolf_eat");
        }
        worldModel.getEntities().forEach(
                entityModel -> {
                    if(random.nextDouble() < 0.001) {
                        SoundEngine.getEngine().playSound(entityModel.getEntityType()+"_idle");
                    }
                }
        );
    }

    public void registerBlockTextures(){
        BlockTextureMap blockTextureMap = new BlockTextureMap();
        blockTextureMap.registerTextures("dirt", List.of(
                Path.of("assets/minecraft_based/dirt.png")
        ));
        blockTextureMap.registerTextures("grass", List.of(
                Path.of("assets/minecraft_based/grass_block_top.png")
        ));
        blockTextureMap.registerTextures("sand", List.of(
                Path.of("assets/minecraft_based/sand.png")
        ));
        blockTextureMap.registerTextures("water", List.of(
                Path.of("assets/minecraft_based/water_still_oneblock.png")
        ));
        blockTextureMap.registerTextures("wood", List.of(
                Path.of("assets/minecraft_based/oak_log.png")
        ));
        blockTextureMap.registerTextures("mud", List.of(
                Path.of("assets/minecraft_based/mud.png")
        ));
        blockTextureMap.registerTextures("cobble_stone", List.of(
                Path.of("assets/minecraft_based/cobblestone.png")
        ));
        blockTextureMap.registerTextures("seed", List.of(
                Path.of("assets/minecraft_based/seed.png")
        ));
        blockTextureMap.registerTextures("sapling", List.of(
                Path.of("assets/minecraft_based/acacia_sapling.png")
        ));
        blockTextureMap.registerTextures("tree", List.of(
                Path.of("assets/minecraft_based/tree.png")
        ));
        worldView.getTerrainView().setTextureMap(blockTextureMap);
    }

    public void registerEntityTextures(){
        EntityTextureMap entityTextureMap = new EntityTextureMap();
        // temporary values generated by Claude, kind of derpy, might change later
        entityTextureMap.registerEntity("pig",
                                        Paths.get("assets/minecraft_based/pig.png"), 1,1.2, 0.9);
        entityTextureMap.registerEntity("wolf",
                                        Paths.get("assets/minecraft_based/wolf.png"),1, 1.5, 0.8
        );

        entityTextureMap.registerEntity("cow",
                                        Paths.get("assets/minecraft_based/calf.png"),0, 1.2, 0.75
        );

        entityTextureMap.registerEntity("cow",
                                        Paths.get("assets/minecraft_based/cow.png"),1, 2.4, 1.5
        );
        worldView.getAllEntitiesView().setEntityTextureMap(entityTextureMap);
    }

    public void registerSound(){
        SoundEngine soundEngine = SoundEngine.getEngine();
        soundEngine.registerSound("grass_step", Paths.get("assets/audio/grass1.mp3"));
        soundEngine.registerSound("wolf_eat", Paths.get("assets/audio/wolf/growl1.mp3"));
        soundEngine.registerSound("pig_idle", Paths.get("assets/audio/pig/pig_idle.mp3"));
        soundEngine.registerSound("cow_idle", Paths.get("assets/audio/cow/idle.mp3"));
        soundEngine.registerSound("wolf_idle", Paths.get("assets/audio/wolf/bark.mp3"));
        soundEngine.registerSound("wolf_death", Paths.get("assets/audio/wolf/death.mp3"));
        soundEngine.registerSound("pig_death", Paths.get("assets/audio/pig/death.mp3"));
        soundEngine.registerSound("cow_death", Paths.get("assets/audio/cow/death.mp3"));
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
            newBlock = BlockFactory.create(block.getBlockType(), xPos, yPos, 0);
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
        if(entity == null) {
            IO.println("Attempt to spawn null entity!");
            return;
        }
        worldModel.getEntities().add(entity);
//        worldView.getAllEntitiesView().addView(entity);
        worldView.getAllEntitiesView().requestRender(entity);
        IO.println("Spawned new " + entity.getEntityType() + ".");
    }

    public void requestSpawnEntity(EntityModel entity) {
        waitToSpawn.add(entity);
    }

}