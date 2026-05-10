package model.generation;

import model.block.BlockModel;

public class DirtBlock extends BlockModel {

    public DirtBlock(int x, int y, int initialState, int sinkability) {
        super(x, y, initialState);
        this.blockType = "dirt";
        this.totalStates = 1;
        this.sinkability = 2;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new DirtBlock(x, y, initialState);
    }
}