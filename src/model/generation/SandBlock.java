package model.generation;

import model.block.BlockModel;

public class SandBlock extends BlockModel {
    public SandBlock(int x, int y, int initialState) {
        super(x, y, initialState);
        this.blockType = "sand";
        this.sinkability = 4;
        this.totalStates = 1;
    }

    @Override
    public boolean isAffectedByGravity() {
        return true;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new SandBlock(x, y, initialState);
    }
}