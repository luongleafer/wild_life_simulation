package model.generation;

import model.block.BlockModel;
import model.block.ObstacleBlockModel;

import java.util.List;

public class TreeBlock extends ObstacleBlockModel {
    private int age;
    private static final int finalLifespan = 100;

    public TreeBlock(int x, int y, int initialState) {
        super(x, y, initialState, 0);
        this.blockType = "tree";
        this.totalStates = 3; // seed, sapling, tree
        this.age = 0;
    }

    public void ageUp() {
        age++;
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        ageUp();
        if (age >= finalLifespan) {
            return new DirtBlock(position.getX(), position.getY(), 0);
        }
        return this;
    }
}