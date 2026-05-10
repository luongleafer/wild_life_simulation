package model.generation;

import model.block.BlockModel;

public class GrassBlock extends BlockModel {
    public GrassBlock(int x, int y, int initialState, int sinkability) {
        super(x, y, initialState);
        this.blockType = "grass";
        this.totalStates = 1;
        this.sinkability = 3
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new GrassBlock(x, y, initialState);
    }
}
