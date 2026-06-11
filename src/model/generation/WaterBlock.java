package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;
import model.entity.Drinkable;

import java.util.List;

public class WaterBlock extends BlockModel implements Drinkable {

    static {
    }

    public WaterBlock(int x, int y) {
        super(x, y, 5);
        this.blockType = "water";
        // water might have 3 states (0, 1, 2) to represent
        // different frames of a flowing animation in the GUI
        // (its way too overkill lol)
        this.sinkability = 5;
    }




    // Sample data for thirst and energy value for water
    @Override
    public double getThirstValue() {
        return 5f;
    }

    @Override
    public double getEnergyValue() {
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