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
import model.generation.DirtBlock;
import model.generation.GrassBlock;
import model.generation.MudBlock;
import model.generation.WaterBlock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class WorldModel {
    private BiomeModel[] biomes;
    private long tickCount;
    private int tickSpeed;
    private BlockModel[][] blocksData;
    private int width;
    private int length;
    private List<EntityModel> entities =  new ArrayList<>();
    private int entityPadding = 5;
    private Random random = new Random();

    public WorldModel(int width, int length) {
        this.width = width;
        this.length = length;
        this.blocksData = new BlockModel[width][length];
        this.tickCount = 0;
        this.tickSpeed = 1; // 1 tick per update
    }

    public BlockModel[][] getBlocksData() {
        return blocksData;
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

        // Slow down block updates so it's visible to the human eye.
        // Terrain updates once every 50 ticks (approx. 1 second if tick is 20ms)
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

    private void entitiesInteraction(){
        entities.forEach(
                entityModel -> {
                    List<EntityModel> surrounding = getEntitiesInAnArea(entityModel.getPosition(), 10);
                    entityModel.Interact(surrounding);
                }
        );
    }

    private void updateEntities(){
        removeDeadEntities();
        entities.forEach(EntityModel::ageUp);
        entitiesInteraction();
        entitiesMovement();
    }

    private void updateTerrain(){
        BlockModel[][] nextBlocksData = new BlockModel[width][length];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                BlockModel currentBlock = blocksData[x][y];
                if (currentBlock == null) {
                    continue;
                }

                if (currentBlock instanceof DirtBlock) {
                    int grassNeighbors = countNeighborsOfType(x, y, GrassBlock.class);
                    if (grassNeighbors > 0) {
                        double chance = grassNeighbors * 0.10;
                        if (random.nextDouble() < chance) {
                            // GrassBlock constructor expects (x, y, initialState)
                            nextBlocksData[x][y] = new GrassBlock(x, y, 0);
                        } else {
                            nextBlocksData[x][y] = currentBlock;
                        }
                    } else {
                        nextBlocksData[x][y] = currentBlock;
                    }
                } else if (currentBlock instanceof MudBlock) {
                    int waterNeighbors = countNeighborsOfType(x, y, WaterBlock.class);
                    if (waterNeighbors == 0) {
                        if (random.nextDouble() < 0.05) {
                            // DirtBlock constructor expects (x, y, initialState)
                            nextBlocksData[x][y] = new DirtBlock(x, y, 0);
                        } else {
                            nextBlocksData[x][y] = currentBlock;
                        }
                    } else {
                        nextBlocksData[x][y] = currentBlock;
                    }
                } else {
                    nextBlocksData[x][y] = currentBlock;
                }
            }
        }

        this.blocksData = nextBlocksData;
    }

    private int countNeighborsOfType(int x, int y, Class<? extends BlockModel> type) {
        int count = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // 4-way neighbors (N, S, E, W)
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < width && ny >= 0 && ny < length) {
                if (blocksData[nx][ny] != null && type.isInstance(blocksData[nx][ny])) {
                    count++;
                }
            }
        }
        return count;
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
