package model.generation;

import model.block.BlockModel;

public class GrassBlock extends BlockModel {
    public GrassBlock(int x, int y, int initialState) {
        super(x, y, initialState);
        // unique string identifier required by BlockView
        this.blockType = "grass";

        // grass typically has one visual state, but i dont touch grass so idfk
        this.totalStates = 1;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new GrassBlock(x, y, initialState);
    }
}
