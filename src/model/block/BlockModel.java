package model.block;

public abstract class BlockModel {
    protected int totalStates;
    protected int currentState;
    protected BlockCoordinate position;
    protected String blockType;

    public int getCurrentState() {
        return this.currentState;
    }

    public BlockModel(int x, int y, int initialState) {
        this.position = new BlockCoordinate(x, y);
        this.currentState = initialState;
    }

    public void setState(int newState) {
        this.currentState = newState;
    }

    public BlockCoordinate getPosition() {
        return position;
    }

    public String getBlockType() {
        return blockType;
    }
}
