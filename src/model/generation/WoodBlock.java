package model.generation;

import model.block.BlockModel;

public class WoodBlock extends BlockModel {

    public WoodBlock(int x, int y, int initialState) {
        super(x, y, initialState, 0);
        this.blockType = "wood";
        this.totalStates = 1;
        this.sinkability = 0;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new WoodBlock(x, y, initialState);
    }
}