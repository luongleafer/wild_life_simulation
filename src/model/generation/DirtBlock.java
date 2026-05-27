package model.generation;

import model.block.BlockModel;

public class DirtBlock extends BlockModel {

    public DirtBlock(int x, int y, int initialState) {
        super(x, y, initialState, 2);
        this.blockType = "dirt";
        this.totalStates = 1;
        this.sinkability = 2;
    }


}