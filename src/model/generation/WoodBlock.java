package model.generation;

import model.block.BlockModel;

public class WoodBlock extends BlockModel {

    public WoodBlock(int x, int y, int initialState) {
        super(x, y, initialState);
        // unique string identifier required by BlockView
        this.blockType = "wood";
        this.totalStates = 1;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new WoodBlock(x, y, initialState);
    }
}