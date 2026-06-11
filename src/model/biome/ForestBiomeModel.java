package model.biome;

import model.block.BlockModel;
import model.generation.WoodBlock;

import java.util.Random;

public class ForestBiomeModel extends BiomeModel {
    @Override
    public BiomeType getBiomeType() {
        return BiomeType.FOREST;
    }

    @Override
    public BlockModel createBlock(int xPos,
                                  int yPos,
                                  float elevation,
                                  float moisture,
                                  boolean shallowWater,
                                  Random random) {
        return new WoodBlock(xPos, yPos);
    }
}
