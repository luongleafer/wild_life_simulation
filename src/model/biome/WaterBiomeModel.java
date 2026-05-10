package model.biome;

import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.generation.SandBlock;
import model.generation.WaterBlock;

import java.util.List;
import java.util.Random;

// fills entire rectangle with one block type
public class WaterBiomeModel extends BiomeModel {
    public WaterBiomeModel(BlockCoordinate topLeft, BlockCoordinate bottomRight) {
        super(topLeft, bottomRight);
        // palette could be populated here
        blockPalette = List.of(
                new WaterBlock(0,0,0),
                new SandBlock(0,0,0)
        );
    }

    @Override
    public BlockModel[] generate() {
        int width = getWidth();
        int height = getHeight();
        BlockModel[] generatedBlocks = new BlockModel[width * height];
        Random random = new Random();
        int i = 0;

        for(int x = topLeft.x; x <  bottomRight.x; x++){
            for(int y =  topLeft.y; y <  bottomRight.y; y++){
                if (random.nextDouble() < 0.2) { // 20% chance of sand
                    generatedBlocks[i++] = blockPalette.get(1).newBlock(x, y, 0);
                } else {
                    generatedBlocks[i++] = blockPalette.get(0).newBlock(x, y, 0);
                }
            }
        }
        return generatedBlocks;
    }
}