package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;
import model.block.ObstacleBlockModel;

import java.util.List;

public class CobbleStoneBlock extends ObstacleBlockModel {


    public CobbleStoneBlock(int x, int y) {
        this(x, y, 0);
    }

    public CobbleStoneBlock(int x, int y, int sinkability) {
        super(x, y, sinkability);
        this.blockType = "cobble_stone";
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        return this;
    }
}
