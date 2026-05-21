package model.block;

public class ObstacleBlock extends ObstacleBlockModel {
    public ObstacleBlock(int x, int y, String blockType) {
        super(x, y, 1, 0);
        this.blockType = blockType;
        this.totalStates = 1;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new ObstacleBlock(x, y, blockType);
    }
}

