package model.animals;

import model.block.BlockModel;
import model.entity.*;

public class Wolf extends AnimalModel implements Edible {
    public Wolf(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, Direction direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, direction);
    }

    public Wolf(EntityCoordinate position){
        super(position);
        // Default values for wolves, can be changed later if needed
        this.health = 8;
        this.energy = 10;
        this.hunger = 5;
        this.thirst = 5;
        this.survivalStrategy = "passive"; // Passive behavior, will never attack
        this.direction = Direction.SOUTH();
        this.currentState = 1; // Adult by default
        this.age = 15; // total lifespan is 10
        this.directionChangeChance = 0.1;
    }
    @Override
    public void Interact(BlockModel block) {
        // Wolves usually do nothing with blocks
    }

    @Override
    public void Interact(EntityModel entity) {
        // They eat small animals, like cows and pigs I guess?
        boolean isPrey = entity instanceof Pig || entity instanceof Cow;
        if (isPrey) {
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
