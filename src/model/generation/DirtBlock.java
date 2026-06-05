package model.generation;

import model.block.BlockModel;

public class DirtBlock extends BlockModel {

    public DirtBlock(int x, int y) {
        super(x, y, 2);
        this.blockType = "dirt";
        this.sinkability = 2;
    }


}