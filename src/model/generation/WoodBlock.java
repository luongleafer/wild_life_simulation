package model.generation;

import model.block.BlockModel;

public class WoodBlock extends BlockModel {

    public WoodBlock(int x, int y) {
        super(x, y, 0);
        this.blockType = "wood";
        this.sinkability = 0;
    }


}