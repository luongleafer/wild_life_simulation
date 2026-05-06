package model.biome;

import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.generation.WaterBlock;

// fills entire rectangle with one block type
public class WaterBiomeModel extends BiomeModel {
    public WaterBiomeModel(BlockCoordinate topLeft, BlockCoordinate bottomRight) {
        super(topLeft, bottomRight);
        // palette could be populated here
    }

    @Override
    public BlockModel[] generate() {
        int width = getWidth();
        int height = getHeight();
        BlockModel[] generatedBlocks = new BlockModel[width * height];
        int index = 0;

        for (int x = topLeft.x; x < bottomRight.x; x++) {
            for (int y = topLeft.y; y < bottomRight.y; y++) {
                // 100% fill with model.generation.WaterBlock
                generatedBlocks[index++] = new WaterBlock(x, y, 0);
            }
        }
        return generatedBlocks;
    }
}