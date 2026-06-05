package model.biome;

import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.block.BlockModels;
import model.generation.DirtBlock;
import model.generation.GrassBlock;
import model.generation.WaterBlock;

import java.util.List;
import java.util.Random;

//places a background block, then randomly scatters another block type
public class PlainBiomeModel extends BiomeModel {
    private Random random = new Random();
    private double grassChance = 0.15; // 15% chance to spawn grass
    private double waterChance = 0.05; // 5% chance for water

    public PlainBiomeModel(BlockCoordinate topLeft, BlockCoordinate bottomRight) {
        super(topLeft, bottomRight);
        blockPalette = List.of(
                new DirtBlock(0,0),
                new GrassBlock(0, 0),
                new WaterBlock(0,0)
        );
    }

    @Override
    public BlockModel[] generate() {
        int width = getWidth();
        int height = getHeight();
        BlockModel[] generatedBlocks = new BlockModel[width * height];
        generateBaseLayer().toArray(generatedBlocks);
        int index = 0;

        for (int x = topLeft.x; x < bottomRight.x; x++) {
            for (int y = topLeft.y; y < bottomRight.y; y++) {
                double roll = random.nextDouble();
                if (roll < waterChance) {
                    generatedBlocks[index++] = BlockModels.from(blockPalette.get(2),x,y); // Water block
                } else if (roll < waterChance + grassChance) {
                    generatedBlocks[index++] = BlockModels.from(blockPalette.get(1),x,y); // Grass block
                } else {
                    generatedBlocks[index++] = BlockModels.from(blockPalette.getFirst(),x,y);  // Dirt block
                }
            }
        }
        return generatedBlocks;
    }
}