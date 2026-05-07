package model.biome;

import model.block.BlockCoordinate;
import model.block.BlockModel;

import java.util.List;
import java.util.ArrayList;

public abstract class BiomeModel {
    protected List<BlockModel> blockPalette;
    protected BlockCoordinate topLeft;
    protected BlockCoordinate bottomRight;

    public BiomeModel(BlockCoordinate topLeft, BlockCoordinate bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.blockPalette = new ArrayList<>();
    }

    // Returns a 1D array of all blocks generated within this biome's rectangle
    public abstract BlockModel[] generate();

    // Helper methods to get dimensions
    protected int getWidth() {
        return bottomRight.x - topLeft.x;
    }

    protected int getHeight() {
        return bottomRight.y - topLeft.y;
    }
}