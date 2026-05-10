package model.block;

public abstract class BlockModel {
    protected int totalStates;
    protected int currentState;
    protected BlockCoordinate position;
    protected String blockType;
    protected int sinkability; // block is harder as sinkability approaches 0

    public int getCurrentState() {
        return this.currentState;
    }

    public BlockModel(int x, int y, int initialState, int sinkability) {
        this.position = new BlockCoordinate(x, y);
        this.currentState = initialState;
        this.sinkability = sinkability;
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

    /**
     * Returns whether this block should be affected by gravity.
     * Subclasses should override this method to return true if they are affected by gravity.
     * @return true if the block is affected by gravity, false otherwise.
     */
    public boolean isAffectedByGravity() {
        return false;
    }

    /**
     * Create a new block of the same type.
     * Implementation can choose to just call the constructor, or do some extra things.
     * @param x New block x-coordinate
     * @param y New block y-coordinate
     * @param initialState New block initial state
     * @return The newly created block
     */
    abstract public BlockModel newBlock(int x, int y, int initialState);
}
