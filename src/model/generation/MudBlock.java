package model.generation;

import model.block.BlockModel;

public class MudBlock extends BlockModel {

    private boolean isWet;

    public MudBlock(int x, int y, int initialState) {
        super(x, y, initialState, 4);
        this.blockType = "mud";
        this.sinkability = 4;
        this.totalStates = 2; // could be 2 for wet/dry
        this.isWet = true; // assume it starts as wet
    }

    public boolean isWet() {
        return isWet;
    }

    public void setWet(boolean wet) {
        isWet = wet;
    }

    /**
     * Dries out the mud block, turning it into a dirt block.
     * @return a new DirtBlock at the same position.
     */
    public BlockModel dryOut() {
        return new DirtBlock(this.position.getX(), this.position.getY(), 0);
    }


}