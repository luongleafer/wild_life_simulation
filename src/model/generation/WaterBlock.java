package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;
import model.entity.Drinkable;

import java.util.List;

public class WaterBlock extends BlockModel implements Drinkable {

    static {
        BlockFactory.register("water", WaterBlock::new);
    }

    public WaterBlock(int x, int y, int initialState) {
        super(x, y, initialState, 5);
        this.blockType = "water";
        // water might have 3 states (0, 1, 2) to represent
        // different frames of a flowing animation in the GUI
        // (its way too overkill lol)
        this.totalStates = 3;
        this.sinkability = 5;
    }



    // example method to cycle water animation state
    public void animate() {
        this.currentState = (this.currentState + 1) % this.totalStates;
    }

    // Sample data for thirst and energy value for water
    @Override
    public float getThirstValue() {
        return 5f;
    }

    @Override
    public float getEnergyValue() {
        return 5f;
    }

    @Override
    public boolean canBeDrank() {
        return true;
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        return this;
    }
}