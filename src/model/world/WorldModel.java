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
import view.entity.GuiEntityView;

import java.util.ArrayList;
import java.util.List;

public class WorldModel {
    private BiomeModel[] biomes;
    private long tickCount;
    private int tickSpeed;
    private BlockModel[][] blocksData;
    private int width;
    private int length;
    private List<EntityModel> entities =  new ArrayList<>();

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

    void advanceTickCount(){
        tickCount += tickSpeed;
    }

    void setTickSpeed(int tickSpeed) {
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

    public <T extends EntityModel> void spawnEntity(T entity) {
        entities.add(entity);
        GuiEntityView.getInstance().addView(entity);
    }

    public void update() {
        tickCount += tickSpeed;
        // more code to loop through blocksData and update block states or trigger entity updates
        // Every entity age up after each tick
        entities.forEach(EntityModel::ageUp);
        // Every animal move somewhere
        entities.stream()
                .filter(entity -> entity instanceof AnimalModel)
                .map(entity -> (AnimalModel) entity)
                .forEach(AnimalModel::move);
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }
}
