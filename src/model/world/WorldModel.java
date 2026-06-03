package model.world;

import model.biome.BiomeModel;
import model.biome.ForestBiomeModel;
import model.biome.PlainBiomeModel;
import model.biome.WaterBiomeModel;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.generation.CobbleStoneBlock;
import model.generation.GrassBlock;

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
    private int entityPadding = 5;
    Random rand = new Random();

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
    }

    public void setTickSpeed(int tickSpeed) {
        this.tickSpeed = tickSpeed;
    }

    public void generateTerrain() {
        // world partitioning
        // split 100x100 matrix into 4 quadrants as an example
        int midX = width / 2;
        int midY = length / 2;

        biomes = new BiomeModel[4];

        // top-left quadrant: Forest
        biomes[0] = new ForestBiomeModel(new BlockCoordinate(0, 0), new BlockCoordinate(midX, midY));
        // top-right quadrant: Water
        biomes[1] = new WaterBiomeModel(new BlockCoordinate(midX, 0), new BlockCoordinate(width, midY));
        // bottom-left quadrant: Plains
        biomes[2] = new PlainBiomeModel(new BlockCoordinate(0, midY), new BlockCoordinate(midX, length));
        // Bottom-Right quadrant: Plains
        biomes[3] = new PlainBiomeModel(new BlockCoordinate(midX, midY), new BlockCoordinate(width, length));

        // generate blocks for each biome and place them in World's 2D array
        for (BiomeModel biome : biomes) {
            BlockModel[] biomeBlocks = biome.generate();

            for (BlockModel block : biomeBlocks) {
                if (block != null) {
                    placeBlock(block);
                }
            }
        }
        overlayBlocks[5][5] = new CobbleStoneBlock(5,5,0,0);

    }

    public void placeBlock(BlockModel newBlock) {
        int x = newBlock.getPosition().x;
        int y = newBlock.getPosition().y;

        // check world boundaries before placing
        if (x >= 0 && x < width && y >= 0 && y < length) {
            blocksData[x][y] = newBlock;
        }
    }

    public void update(){
        advanceTickCount();
        updateTerrain();
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
                position.setPosX(width - entityPadding);
            }
            if(position.getPosX() < entityPadding){
                position.setPosX(entityPadding);
            }
            if(position.getPosY() > length - entityPadding){
                position.setPosY(length - entityPadding);
            }
            if(position.getPosY() < entityPadding){
                position.setPosY(entityPadding);
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

    List<BlockModel> getBlocksInAnArea(EntityCoordinate origin, int reachRadius){
        List<BlockModel> blocks = new ArrayList<>();
        for(int x = 0 ; x < width ; x++){
            for(int y = 0 ; y < length ; y++){
                if(origin.distance(new EntityCoordinate(x, y)) <= reachRadius){
                    blocks.add(blocksData[x][y]);
                }
            }
        }
        return blocks;
    }

    private void entitiesInteraction(){
        entities.forEach(
                entityModel -> {
                    List<EntityModel> surrounding = getEntitiesInAnArea(entityModel.getPosition(), 10);
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
}
