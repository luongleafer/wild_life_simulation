package model.world;

import model.biome.BiomeModel;
import model.biome.ForestBiomeModel;
import model.biome.PlainBiomeModel;
import model.biome.WaterBiomeModel;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.block.FoodBlockModel;
import model.block.ObstacleBlockModel;

public class WorldModel {
    private BiomeModel[] biomes;
    private long tickCount;
    private int tickSpeed;
    private BlockModel[][] blocksData;
    // separate layers for obstacles and food blocks.
    private ObstacleBlockModel[][] obstacleData;
    private FoodBlockModel[][] foodData;
    private int width;
    private int length;

    public WorldModel(int width, int length) {
        this.width = width;
        this.length = length;
        this.blocksData = new BlockModel[width][length];
        this.obstacleData = new ObstacleBlockModel[width][length];
        this.foodData = new FoodBlockModel[width][length];
        this.tickCount = 0;
        this.tickSpeed = 1; // 1 tick per update
    }

    public BlockModel[][] getBlocksData() {
        return blocksData;
    }

    public ObstacleBlockModel[][] getObstacleData() {
        return obstacleData;
    }

    public FoodBlockModel[][] getFoodData() {
        return foodData;
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

    // Place an obstacle block without modifying the base terrain.
    public void placeObstacle(ObstacleBlockModel obstacleBlock) {
        int x = obstacleBlock.getPosition().x;
        int y = obstacleBlock.getPosition().y;

        // check world boundaries before placing
        if (x >= 0 && x < width && y >= 0 && y < length) {
            obstacleData[x][y] = obstacleBlock;
        }
    }

    // Place a food block without modifying the base terrain.
    public void placeFood(FoodBlockModel foodBlock) {
        int x = foodBlock.getPosition().x;
        int y = foodBlock.getPosition().y;

        // check world boundaries before placing
        if (x >= 0 && x < width && y >= 0 && y < length) {
            foodData[x][y] = foodBlock;
        }
    }

    public void update() {
        tickCount += tickSpeed;
        // more code to loop through blocksData and update block states or trigger entity updates
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }
}
