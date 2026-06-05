package model.generation;

import model.block.BlockModel;

public class SandBlock extends BlockModel {
    public SandBlock(int x, int y) {
        super(x, y, 4);
        this.blockType = "sand";
        this.sinkability = 4;
    }

    public boolean isAffectedByGravity() {
        return true;
    }

}