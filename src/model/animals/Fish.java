package model.animals;

import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.AquaticCreature;
import model.entity.Direction;
import model.entity.EntityCoordinate;

public class Fish extends AnimalModel implements AquaticCreature {

    public Fish(EntityCoordinate position) {
        super(position, 10, 20, 20, 10);
        this.entityType = "fish";
        this.currentState = 0; // Maps to fish_0.png
        this.survivalStrategy = "passive";
        this.direction = Direction.SOUTH();
        this.setSpeed(4.0 / 20);
        this.level = 1;
        this.hitBoxLength = 1;
        this.hitBoxWidth = 1;
    }

    @Override
    public void Interact(BlockModel block) {
        super.Interact(block);
        if (!"water".equals(block.getBlockType())) {
            headAwayFrom(block.getPosition(), 2.0);
        }
    }

    @Override
    public void move() {
        roamRandomly(2.0 / 20, 5.0 / 20, Math.PI / 3);
    }
}
