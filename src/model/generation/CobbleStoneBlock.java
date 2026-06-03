package model.generation;

import model.block.BlockModel;
import model.block.ObstacleBlock;
import model.block.ObstacleBlockModel;

import java.util.List;

public class CobbleStoneBlock extends ObstacleBlockModel {
    public CobbleStoneBlock(int x, int y, int initialState, int sinkability) {
        super(x, y, initialState, sinkability);
        this.blockType = "cobble_stone";
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        return this;
    }
}
