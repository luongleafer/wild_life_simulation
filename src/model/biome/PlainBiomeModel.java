package model.biome;

import model.block.BlockModel;
import model.generation.DirtBlock;
import model.generation.GrassBlock;
import model.generation.MudBlock;

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
        if(moisture > 0.5f){
            return new MudBlock(xPos, yPos);
        }
        if(random.nextDouble() < 0.3){
            return new GrassBlock(xPos, yPos);
        }
        return new DirtBlock(xPos, yPos);
    }
}
