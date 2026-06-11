package model.biome;

import model.block.BlockModel;
import model.generation.*;

import java.util.Random;

public class ForestBiomeModel extends BiomeModel {
    @Override
    public BiomeType getBiomeType() {
        return BiomeType.FOREST;
    }

    @Override
    public BlockModel createBlock(
            int xPos,
            int yPos,
            float elevation,
            float moisture,
            float forestDensity,
            boolean shallowWater,
            Random random) {

        if (forestDensity > 0.55f) {
            return new TreeBlock(xPos, yPos);
        }

        if (forestDensity > 0.45f) {
            if (random.nextDouble() < 0.6) {
                return new TreeBlock(xPos, yPos);
            }
        }

        if (forestDensity > 0.35f) {
            if (random.nextDouble() < 0.3) {
                return new SaplingBlock(xPos, yPos);
            }
        }

        return new GrassBlock(xPos, yPos);
    }}