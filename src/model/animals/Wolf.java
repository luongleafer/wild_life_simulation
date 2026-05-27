package model.animals;

import model.block.BlockModel;
import model.entity.*;

import java.util.List;

public class Wolf extends AnimalModel implements Edible {
    EntityModel currentTarget = null;
    public Wolf(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, 20, 0, 0);
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
        this.setSpeed(0.111);
        this.setDirection(0,0);
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

    @Override
    public void move() {
        IO.println("Wolf move");
        if(currentTarget == null) {
            roamRandomly(0.111, 0.200, Math.PI / 3);
        }
        else{
            IO.println("Wolf has target");
            moveToward(currentTarget.getPosition(), 1);
        }
    }

    @Override
    public void Interact(List<EntityModel> entities) {
//        IO.println("Wolf is interacting with a list of " + entities.size() + " entities");
       // filter out pigs
        EntityModel toFollow = entities.stream().filter(entity -> entity instanceof Pig).findFirst().orElse(null);
        if(toFollow != null) {
            currentTarget = toFollow;
            if(getPosition().distance(toFollow.getPosition()) < 5) {
                toFollow.receiveDamage(1);
            }
        }
        else{
            currentTarget = null;
        }

    }
}
