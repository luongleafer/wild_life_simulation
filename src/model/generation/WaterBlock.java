package model.generation;

import model.block.BlockModel;

public class WaterBlock extends BlockModel {

    public WaterBlock(int x, int y, int initialState) {
        super(x, y, initialState);

        this.blockType = "water";

        // water might have 3 states (0, 1, 2) to represent
        // different frames of a flowing animation in the GUI
        // (its way too overkill lol)
        this.totalStates = 3;
    }

    // example method to cycle water animation state
    public void animate() {
        this.currentState = (this.currentState + 1) % this.totalStates;
    }
}