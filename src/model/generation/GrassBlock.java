package model.generation;

import model.block.BlockModel;
import model.entity.Edible;

public class GrassBlock extends BlockModel implements Edible {
    public GrassBlock(int x, int y) {
        super(x, y, 3);
        this.blockType = "grass";
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
}
