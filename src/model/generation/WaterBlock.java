package model.generation;

import model.block.BlockModel;
import model.entity.Drinkable;

public class WaterBlock extends BlockModel implements Drinkable {

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
}