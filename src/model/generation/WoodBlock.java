package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;

import java.util.List;

public class WoodBlock extends BlockModel {

    static {
        BlockFactory.register("wood", WoodBlock::new);
    }

    public WoodBlock(int x, int y, int initialState) {
        super(x, y, 0);
        this.blockType = "wood";
        this.sinkability = 0;
    }


    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        return this;
    }
}