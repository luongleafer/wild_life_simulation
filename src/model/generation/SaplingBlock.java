package model.generation;

import model.block.BlockFactory;
import model.block.BlockModel;
import model.block.ObstacleBlockModel;
import model.entity.Edible;

import java.util.List;

public class SaplingBlock extends ObstacleBlockModel implements Edible {
    private int age;
    private static final int growUpAge = 30;

    static {
    }

    public SaplingBlock(int x, int y) {
        super(x, y,  1);
        this.blockType = "sapling";
        this.age = 0;
    }

    public void ageUp() {
        age++;
    }

    // Some animals actually EAT saplings.
    @Override
    public float getHungerValue() {
        return 5f;
    }

    @Override
    // I assume that leaves (and wood) isn't all that filling
    public float getEnergyValue() {
        return 5f;
    }

    @Override
    public boolean canBeEaten() {
        return true;
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        ageUp();
        if (age >= growUpAge) {
            return new TreeBlock(position.getX(), position.getY());
        }
        return this;
    }
}
