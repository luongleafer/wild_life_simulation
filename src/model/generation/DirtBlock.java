package model.generation;

import model.block.BlockModel;

public class DirtBlock extends BlockModel {

    public DirtBlock(int x, int y, int initialState) {
        super(x, y, initialState);
        // unique string identifier required by BlockView
        this.blockType = "dirt";
        // not sure about this one, i dont know shit abt ecology
        this.totalStates = 1;
    }
}