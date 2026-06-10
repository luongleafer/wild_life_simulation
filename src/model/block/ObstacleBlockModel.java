package model.block;

public abstract class ObstacleBlockModel extends BlockModel {
    // obstacles are passively blocks movement
    public ObstacleBlockModel(int x, int y, int sinkability) {
        super(x, y, sinkability);
    }

    public boolean blocksMovement() {
        return true;
    }
}

