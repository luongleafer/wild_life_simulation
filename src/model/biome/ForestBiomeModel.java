package model.biome;

import model.block.BlockModel;
import model.generation.GrassBlock;
import model.generation.MudBlock;
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
        if(moisture > 0.6f){
            return new WoodBlock(xPos, yPos);
        }
        if(random.nextDouble() < 0.15){
            return new MudBlock(xPos, yPos);
        }
        return new GrassBlock(xPos, yPos);
    }
}
