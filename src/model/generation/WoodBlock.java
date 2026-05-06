package model.generation;

import model.block.BlockModel;

public class WoodBlock extends BlockModel {

    public WoodBlock(int x, int y, int initialState) {
        super(x, y, initialState);
        // unique string identifier required by BlockView
        this.blockType = "wood";
        this.totalStates = 1;
    }
}