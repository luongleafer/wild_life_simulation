package model.biome;

import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.generation.GrassBlock;
import model.generation.WoodBlock;

import java.util.List;
import java.util.Random;
// clusters of feature blocks are placed inside rectangle
public class ForestBiomeModel extends BiomeModel {
    private Random random = new Random();
    private int numberOfBlobs = 5;
    private int blobRadius = 3;

    public ForestBiomeModel(BlockCoordinate topLeft, BlockCoordinate bottomRight) {
        super(topLeft, bottomRight);
        blockPalette = List.of(new GrassBlock(0,0,0));
    }

    @Override
    public BlockModel[] generate() {
        int width = getWidth();
        int height = getHeight();
        BlockModel[] generatedBlocks = new BlockModel[width * height];

        generateBaseLayer().toArray(generatedBlocks);// put base layer into generatedBlocks

        // generate blobs (WoodBlocks) strictly within bounds
        for (int i = 0; i < numberOfBlobs; i++) {
            int centerX = topLeft.x + random.nextInt(width);
            int centerY = topLeft.y + random.nextInt(height);

            // carve out a circular blob
            for (int dx = -blobRadius; dx <= blobRadius; dx++) {
                for (int dy = -blobRadius; dy <= blobRadius; dy++) {
                    int targetX = centerX + dx;
                    int targetY = centerY + dy;

                    // ensure the blob doesn't bleed outside this biome's rectangle
                    if (targetX >= topLeft.x && targetX < bottomRight.x &&
                            targetY >= topLeft.y && targetY < bottomRight.y) {

                        // distance check
                        if (dx*dx + dy*dy <= blobRadius*blobRadius) {
                            // find the block in the 1D array and replace it
                            int localX = targetX - topLeft.x;
                            int localY = targetY - topLeft.y;
                            int arrayIndex = localX * height + localY;
                            generatedBlocks[arrayIndex] = new WoodBlock(targetX, targetY, 0);
                        }
                    }
                }
            }
        }
        return generatedBlocks;
    }
}