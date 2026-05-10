package model.generation;

import model.block.BlockModel;

public class WaterBlock extends BlockModel {

    public WaterBlock(int x, int y, int initialState, int sinkability) {
        super(x, y, initialState);
        this.blockType = "water";
        // water might have 3 states (0, 1, 2) to represent
        // different frames of a flowing animation in the GUI
        // (its way too overkill lol)
        this.totalStates = 3;
        this.sinkability = 5;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new WaterBlock(x, y, initialState);
    }

    // example method to cycle water animation state
    public void animate() {
        this.currentState = (this.currentState + 1) % this.totalStates;
    }
}