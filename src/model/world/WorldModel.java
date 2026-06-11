package model.world;

import model.biome.BiomeModel;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.generation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class WorldModel {
    private BiomeModel[] biomes;
    private long tickCount;
    private int tickSpeed;
    private BlockModel[][] blocksData;
    private BlockModel[][] overlayBlocks;
    private int width;
    private int length;
    private List<EntityModel> entities =  new ArrayList<>();
    private int entityPadding = 1;
    Random random = new Random();
    Random rand = new Random();
    private Season currentSeason = Season.SPRING;
    private final long seasonLength = 400; // 400 ticks, 20 seconds
    private final int seasonVariance = 100;
    private long ticksSinceLastSeason = 0;
    private long currentSeasonLength = 400;
    

    public WorldModel(int width, int length) {
        this.width = width;
        this.length = length;
        this.blocksData = new BlockModel[width][length];
        this.overlayBlocks = new BlockModel[width][length];
        this.tickCount = 0;
        this.tickSpeed = 1; // 1 tick per update
    }

    public BlockModel[][] getBlocksData() {
        return blocksData;
    }

    public BlockModel[][] getOverlayBlocks() {
        return overlayBlocks;
    }

    public List<EntityModel> getEntities() {
        return entities;
    }

    public void advanceTickCount(){
        tickCount += tickSpeed;
        ticksSinceLastSeason += tickSpeed;
    }

    public void updateSeason(){
        if(ticksSinceLastSeason < currentSeasonLength) return;
        ticksSinceLastSeason = 0;
        currentSeason = switch (currentSeason) {
            case SPRING -> Season.SUMMER;
            case SUMMER -> Season.AUTUMN;
            case AUTUMN -> Season.WINTER;
            case WINTER -> Season.SPRING;
        };
        currentSeasonLength = seasonLength + random.nextInt(seasonVariance);
        AnimalModel.hungerDepletionRate = currentSeason.animalHungerDepletionRate;
        AnimalModel.healthDepletionRate = currentSeason.animalHealthDepletionRate;
        AnimalModel.thirstDepletionRate = currentSeason.animalThirstDepletionRate;
        AnimalModel.mateChance = currentSeason.animalMateChance;
    }

    public void setTickSpeed(int tickSpeed) {
        this.tickSpeed = tickSpeed;
    }

    public void generateTerrain() {
        NoiseGeneration noise = new NoiseGeneration();
        noise.SetNoiseType(NoiseGeneration.NoiseType.OpenSimplex2);
        // Using a random seed for variation
        noise.SetSeed(random.nextInt());

        NoiseGeneration moistureNoise = new NoiseGeneration();
        moistureNoise.SetNoiseType(NoiseGeneration.NoiseType.Cellular);
        moistureNoise.SetSeed(random.nextInt());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                // Get noise values between -1.0 and 1.0 (approx)
                float elevation = noise.GetNoise(x * 5.0f, y * 5.0f); // multiplying by a frequency scale

                BlockModel blockToPlace;

                if (elevation < -0.25f) {
                    // Deep water / Beach
                    if (elevation > -0.3f && random.nextDouble() < 0.5) {
                        blockToPlace = new SandBlock(x, y, 0);
                    } else {
                        blockToPlace = new WaterBlock(x, y, 0);
                    }
                } else if (elevation < 0.4f) {
                    // Plains
                    float moisture = moistureNoise.GetNoise(x * 5.0f, y * 5.0f);
                    if (moisture > 0.5f) {
                        blockToPlace = new MudBlock(x, y, 0);
                    } else if (random.nextDouble() < 0.3) {
                        blockToPlace = new GrassBlock(x, y, 0);
                    } else {
                        blockToPlace = new DirtBlock(x, y, 0);
                    }
                } else {
                    // Forest / High elevation
                    float moisture = moistureNoise.GetNoise(x * 10.0f, y * 10.0f);
                    if (moisture > 0.6f) {
                        blockToPlace = new WoodBlock(x, y, 0);
                    } else {
                        blockToPlace = new GrassBlock(x, y, 0);
                    }
                }

                placeBlock(blockToPlace);
            }
        }
        placeObstacle();

    }

    public void placeObstacle(){
        Random rand = new Random();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                if(blocksData[x][y] instanceof DirtBlock && rand.nextDouble() < 0.02) {
                    overlayBlocks[x][y] = new CobbleStoneBlock(x,y,0,0);
                }
                if ((blocksData[x][y] instanceof GrassBlock || blocksData[x][y] instanceof DirtBlock) && rand.nextDouble() < 0.03) {
                    overlayBlocks[x][y] = new SeedBlock(x, y, 0);
                }
            }
        }
    }

    public void placeBlock(BlockModel newBlock) {
        int x = newBlock.getPosition().x;
        int y = newBlock.getPosition().y;

        // check world boundaries before placing
        if (x >= 0 && x < width && y >= 0 && y < length) {
            blocksData[x][y] = newBlock;
        }
    }

    public void update() {
        System.out.println("Tick: " + tickCount);

        advanceTickCount();
        updateSeason();

        if (tickCount % 50 == 0) {
            updateTerrain();
        }

        updateEntities();
    }

    public List<EntityModel> getDeadEntities(){
        return entities.stream()
                .filter(entityModel -> entityModel.getHealth() <= 0).toList();
    }

    private void removeDeadEntities(){
        entities.removeAll(getDeadEntities());
    }

    private void entitiesMovement(){
        entities.stream()
                .filter(entity -> entity instanceof AnimalModel)
                .map(entity -> (AnimalModel) entity)
                .forEach(AnimalModel::move);

        entities.forEach(entityModel -> {
            EntityCoordinate position = entityModel.getPosition();
            if(position.getPosX() > width - entityPadding){
                position.setPosX(width - entityPadding - 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(-1, 0);
                }
            }
            if(position.getPosX() < entityPadding){
                position.setPosX(entityPadding + 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(1, 0);
                }
            }
            if(position.getPosY() > length - entityPadding){
                position.setPosY(length - entityPadding - 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(0, -1);
                }
            }
            if(position.getPosY() < entityPadding){
                position.setPosY(entityPadding + 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(0, 1);
                }
            }
        });
    }

    /**
     * List all Entities in a circle area
     * @param origin The center of the area
     * @param radius The radius of the area
     * @return All Entities in the area
     */
    List<EntityModel> getEntitiesInAnArea(EntityCoordinate origin, double radius){
        return entities.stream().filter(
                entityModel -> entityModel.getPosition().distance(origin) <= radius
        ).sorted(Comparator.comparing(entityModel -> entityModel.getPosition().distance(origin))).toList();
    }

    List<BlockModel> getBlocksInAnArea(EntityCoordinate origin, double reachRadius){
        List<BlockModel> blocks = new ArrayList<>();
        for(int x = 0 ; x < width ; x++){
            for(int y = 0 ; y < length ; y++){
                if(origin.distance(new EntityCoordinate(x + 0.5, y + 0.5)) <= reachRadius){
                    blocks.add(blocksData[x][y]);
                    if(overlayBlocks[x][y] != null){
                        blocks.add(overlayBlocks[x][y]);
                    }
                }
            }
        }
        return blocks;
    }

    private void entitiesInteraction(){
        entities.forEach(
                entityModel -> {
                    List<EntityModel> surrounding = getEntitiesInAnArea(entityModel.getPosition(), 5);
                    entityModel.Interact(surrounding);
                    List<BlockModel> surroundingBlocks = getBlocksInAnArea(entityModel.getPosition(), 2);
                    surroundingBlocks.forEach(entityModel::Interact);
                }
        );
    }

    private void updateEntities(){
        removeDeadEntities();
        entities.forEach(EntityModel::ageUp);
        entitiesMovement();
        entitiesInteraction();
    }

    private List<BlockModel> getSurroundingBlocks(BlockCoordinate origin){
        List<BlockModel> surroundingBlocks = new ArrayList<>();
        int x = origin.x;
        int y = origin.y;
        if(x > 0) surroundingBlocks.add(blocksData[x-1][y]);
        if(y > 0) surroundingBlocks.add(blocksData[x][y-1]);
        if(x < width - 1) surroundingBlocks.add(blocksData[x+1][y]);
        if(y < length - 1) surroundingBlocks.add(blocksData[x][y+1]);
        return surroundingBlocks;
    }

    private void updateTerrain(){
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                blocksData[x][y] = blocksData[x][y].interact(getSurroundingBlocks(blocksData[x][y].getPosition()));
                if (overlayBlocks[x][y] != null) {
                    overlayBlocks[x][y] = overlayBlocks[x][y].interact(getSurroundingBlocks(overlayBlocks[x][y].getPosition()));
                }
            }
        }
    }

    public long getTickCount() {
        return tickCount;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public String getCurrentSeason(){
        return currentSeason.getName();
    }
    public BlockModel getBlock(int x, int y){

        if(x < 0 || x >= width ||
           y < 0 || y >= length){

            return null;
        }

        return blocksData[x][y];
    }
    public void setBlock(
            int x,
            int y,
            BlockModel block
    ){
        if(x < 0 || x >= width ||
           y < 0 || y >= length){

            return;
        }

        blocksData[x][y] = block;
    }

}
