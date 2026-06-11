package model.biome;

import model.block.BlockModel;
import model.generation.DirtBlock;
import model.generation.GrassBlock;

import java.util.Random;

public class PlainBiomeModel extends BiomeModel {
    @Override
    public BiomeType getBiomeType() {
        return BiomeType.PLAIN;
    }

    @Override
    public BlockModel createBlock(int xPos,
                                  int yPos,
                                  float elevation,
                                  float moisture,
                                  boolean shallowWater,
                                  Random random) {
        if(moisture > 0.2f && random.nextDouble() < 0.4){
            return new GrassBlock(xPos, yPos);
        }
        return new DirtBlock(xPos, yPos);
    }
}
