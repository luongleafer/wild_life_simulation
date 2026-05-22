package model.animals;

import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.entity.Edible;

public class Wolf extends AnimalModel implements Edible {
    public Wolf(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, Direction direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, direction);
    }

    @Override
    public void Interact(BlockModel block) {
        // Wolves usually do nothing with blocks
    }

    @Override
    public void Interact(EntityModel entity) {
        // They eat small animals, like cows and pigs I guess?
        if (entity instanceof Pig || entity instanceof Cow) {
            this.eat((Edible) entity);
        }
    }

    @Override
    // Can't eat wolves.
    public float getHungerValue() {
        return 0;
    }

    @Override
    // Can't gain energy from wolves.
    public float getEnergyValue() {
        return 0;
    }

    @Override
    // Not sure if it's acceptable to eat wolves.
    public boolean canBeEaten() {
        return false;
    }
}
