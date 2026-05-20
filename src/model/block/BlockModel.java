package model.block;

public abstract class BlockModel {

    // Represents how many stages this block can go through
    // before being completely destroyed.
    // Example:
    // 3 = intact
    // 2 = cracked
    // 1 = nearly broken
    protected int totalStates;

    // Stores the block's current stage/state.
    protected int currentState;

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
            int initialState,
            int sinkability
    ) {

        // Create and assign block coordinates.
        this.position = new BlockCoordinate(x, y);

        // Set the starting state of the block.
        this.currentState = initialState;

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

    // Returns the current state of the block.
    public int getCurrentState() {
        return currentState;
    }

    // Returns the maximum number of states.
    public int getTotalStates() {
        return totalStates;
    }

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

    // Updates the current state manually.
    public void setState(int newState) {
        this.currentState = newState;
    }

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

    // Factory-style method used to create another block
    // of the same concrete type.
    public abstract BlockModel newBlock(
            int x,
            int y,
            int initialState
    );
}