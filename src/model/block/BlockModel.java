package model.block;

public abstract class BlockModel {

    // Keeps track of the block position on the game board.
    protected BlockCoordinate position;

    // Identifies the category/type of block.
    // Example values:
    // "STONE", "ICE", "WOOD"
    protected String blockType;

    // Indicates how resistant the block is.
    // Lower values mean the block is tougher.
    protected int sinkability;

    // Tracks whether the block has already been removed.
    protected boolean destroyed;

    // Determines if the block is allowed to move.
    // Useful for gravity or swap mechanics.
    protected boolean movable;

    // Determines if the block can be destroyed.
    protected boolean destroyable;

    // Constructor used to initialize a new block object.
    public BlockModel(
            int x,
            int y,
            int sinkability
    ) {

        // Create and assign block coordinates.
        this.position = new BlockCoordinate(x, y);

        // Set the starting state of the block.

        // Assign resistance level.
        this.sinkability = sinkability;

        // Newly created blocks are not destroyed.
        this.destroyed = false;

        // By default, blocks are movable.
        this.movable = true;

        // By default, blocks are destroyable.
        this.destroyable = true;
    }

    // =========================
    // GETTER METHODS
    // =========================



    // Returns the current position of the block.
    public BlockCoordinate getPosition() {
        return position;
    }

    // Returns the type/category of the block.
    public String getBlockType() {
        return blockType;
    }

    // Returns the resistance value.
    public int getSinkability() {
        return sinkability;
    }

    // Checks whether the block has been destroyed.
    public boolean isDestroyed() {
        return destroyed;
    }

    // Checks if the block can move.
    public boolean isMovable() {
        return movable;
    }

    // Checks if the block is allowed to be destroyed.
    public boolean isDestroyable() {
        return destroyable;
    }

    // =========================
    // SETTER METHODS
    // =========================

    // Updates block coordinates.
    // Typically used when blocks fall or swap positions.
    public void setPosition(int x, int y) {
        this.position = new BlockCoordinate(x, y);
    }

}