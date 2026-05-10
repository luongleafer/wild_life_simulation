package model.biome;

import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.generation.WaterBlock;

import java.util.List;

// fills entire rectangle with one block type
public class WaterBiomeModel extends BiomeModel {
    public WaterBiomeModel(BlockCoordinate topLeft, BlockCoordinate bottomRight) {
        super(topLeft, bottomRight);
        // palette could be populated here
        blockPalette = List.of(
                new WaterBlock(0,0,0)
        );
    }

    @Override
    public BlockModel[] generate() {
        int width = getWidth();
        int height = getHeight();
        BlockModel[] generatedBlocks = new BlockModel[width * height];

        generateBaseLayer().toArray(generatedBlocks);
        return generatedBlocks;
    }
}