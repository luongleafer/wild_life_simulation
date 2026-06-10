package model.generation;

import model.block.BlockFactory;
import model.block.BlockModel;
import model.block.ObstacleBlockModel;

import java.util.List;

public class TreeBlock extends ObstacleBlockModel {
    private int age;
    private static final int finalLifespan = 100;

    static {
        BlockFactory.register("tree", TreeBlock::new);
    }

    public TreeBlock(int x, int y) {
        super(x, y, 0);
        this.blockType = "tree";
        this.age = 0;
    }

    public void ageUp() {
        age++;
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        ageUp();
        if (age >= finalLifespan) {
            return new DirtBlock(position.getX(), position.getY());
        }
        return this;
    }
}