package model.biome;

import model.block.BlockModel;

import java.util.Random;

public abstract class BiomeModel {
    public abstract BiomeType getBiomeType();

    public abstract BlockModel createBlock(int xPos,
                                           int yPos,
                                           float elevation,
                                           float moisture,
                                           boolean shallowWater,
                                           Random random);
}
