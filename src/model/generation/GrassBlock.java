package model.generation;

import model.block.BlockModel;
import model.entity.Edible;

import java.util.List;
import java.util.Random;

public class GrassBlock extends BlockModel implements Edible {
    public GrassBlock(int x, int y, int initialState) {
        super(x, y, initialState, 3);
        this.blockType = "grass";
        this.totalStates = 1;
        this.sinkability = 3;
    }



    // Add temporary hunger and energy values for grass as a test
    @Override
    public float getHungerValue() {
        return 10f;
    }

    @Override
    // I assume that grass isn't all that filling
    public float getEnergyValue() {
        return 5f;
    }

    // Obviously
    @Override
    public boolean canBeEaten() {
        return true;
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        return this;
    }
}
