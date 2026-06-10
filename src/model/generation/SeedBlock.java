package model.generation;

import model.block.BlockFactory;
import model.block.ObstacleBlockModel;
import model.entity.Edible;

public class SeedBlock extends ObstacleBlockModel implements Edible {
    private int age;
    private static final int growUpAge = 10;

    static {
        BlockFactory.register("seed", SeedBlock::new);
    }

    public SeedBlock(int x, int y) {
        super(x, y, 2);
        this.blockType = "seed";
        this.age = 0;
    }

    public void ageUp() {
        age++;
    }

    // Some animals actually EAT seeds.
    @Override
    public float getHungerValue() {
        return 3f;
    }

    @Override
    // I assume that leaves (and wood) isn't all that filling
    public float getEnergyValue() {
        return 3f;
    }

    @Override
    public boolean canBeEaten() {
        return true;
    }

    @Override
    public model.block.BlockModel interact(java.util.List<model.block.BlockModel> surroundingBlocks) {
        ageUp();
        if (age >= growUpAge) {
            return new SaplingBlock(position.getX(), position.getY());
        }
        return this;
    }
}
