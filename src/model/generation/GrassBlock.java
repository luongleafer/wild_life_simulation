package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;
import model.entity.Edible;

import java.util.List;

public class GrassBlock extends BlockModel implements Edible {

    static {
        BlockFactory.register("grass", GrassBlock::new);
    }

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
