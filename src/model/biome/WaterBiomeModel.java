package model.biome;

import model.block.BlockModel;
import model.generation.SandBlock;
import model.generation.WaterBlock;

import java.util.Random;

public class WaterBiomeModel extends BiomeModel {
    @Override
    public BiomeType getBiomeType() {
        return BiomeType.WATER;
    }

    @Override
    public BlockModel createBlock(int xPos,
                                  int yPos,
                                  float elevation,
                                  float moisture,
                                  boolean shallowWater,
                                  Random random) {
        if(shallowWater){
            if(random.nextDouble() < 0.75){
                return new SandBlock(xPos, yPos);
            }
            return new WaterBlock(xPos, yPos);
        }
        if(elevation > -0.30f && random.nextDouble() < 0.5){
            return new SandBlock(xPos, yPos);
        }
        return new WaterBlock(xPos, yPos);
    }
}
