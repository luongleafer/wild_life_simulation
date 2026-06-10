package model.block;

import model.entity.EntityCoordinate;

import java.util.List;

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

    // =========================
    // GAMEPLAY LOGIC
    // =========================

    // Applies damage to the block.
    // Each hit decreases the current state by 1.
    public void damage() {

        // Ignore damage if the block is indestructible.
        if (!destroyable) {
            return;
        }

        // Reduce state only if still above zero.
        if (currentState > 0) {
            currentState--;
        }

        // Destroy the block once state reaches zero.
        if (currentState <= 0) {
            destroy();
        }
    }

    // Marks the block as destroyed.
    public void destroy() {

        // Prevent destruction if block is protected.
        if (destroyable) {
            destroyed = true;
        }
    }

    // Indicates whether gravity should affect this block.
    // Default behavior is false.
    // Child classes can override this.
    public boolean isAffectedByGravity() {
        return false;
    }

    /**
     * Interaction with surrounding blocks
     * @param surroundingBlocks Blocks surrounding this block
     * @return This block or new block
     */
    public abstract BlockModel interact(List<BlockModel> surroundingBlocks);

    public EntityCoordinate blockCenter(){
        return new EntityCoordinate(position.getX() + 0.5, position.getY() + 0.5);
    }

}