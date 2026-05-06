package model.biome;

import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.generation.DirtBlock;
import model.generation.GrassBlock;

import java.util.Random;

//places a background block, then randomly scatters another block type
public class PlainBiomeModel extends BiomeModel {
    private Random random = new Random();
    private double scatterChance = 0.15; // 15% chance to spawn grass instead of dirt

    public PlainBiomeModel(BlockCoordinate topLeft, BlockCoordinate bottomRight) {
        super(topLeft, bottomRight);
    }

    @Override
    public BlockModel[] generate() {
        int width = getWidth();
        int height = getHeight();
        BlockModel[] generatedBlocks = new BlockModel[width * height];
        int index = 0;

        for (int x = topLeft.x; x < bottomRight.x; x++) {
            for (int y = topLeft.y; y < bottomRight.y; y++) {
                if (random.nextDouble() < scatterChance) {
                    generatedBlocks[index++] = new GrassBlock(x, y, 0); // Feature block
                } else {
                    generatedBlocks[index++] = new DirtBlock(x, y, 0);  // Background block
                }
            }
        }
        return generatedBlocks;
    }
}