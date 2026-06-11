package model.block;

import java.util.List;

public class ObstacleBlock extends ObstacleBlockModel {
    public ObstacleBlock(int x, int y, String blockType) {
        super(x, y, 1);
        this.blockType = blockType;
    }

    public BlockModel newBlock(int x, int y) {
        return new ObstacleBlock(x, y, blockType);
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        return this;
    }
}

