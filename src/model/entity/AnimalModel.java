package model.entity;

import java.util.Random;

public abstract class AnimalModel extends EntityModel {

    // since we are following the Minecraft model here...
    // the values will be floats
    private float hunger;
    private float thirst;
    private float energy;
    // As seen in issue #8, this will be temporarily implemented using String.
    // though I don't know a better way to do this yet.
    // Possible acceptable keywords: predator, camouflage, defensive, etc...
    // but that is for later, when those behaviors are defined better
    private final String survivalStrategy;
    // direction may mean that this animal is chasing/fleeing from other entities.
    // Possible acceptable keywords: north, south, east, west???
    private final String direction;

    // Main methods
    // eat(food) food can be other Entity or Block, depend on the specific implementation of the animal
    public void eat(Edible food) {
        if (food == null || !food.canBeEaten()) {
            return;
        }
        this.hunger += food.getHungerValue();
        this.energy += food.getEnergyValue();
    }

    public void drink(Drinkable block) {
        if (block == null || !block.canBeDrank()) {
            return;
        }
        this.thirst += block.getThirstValue();
        this.energy += block.getEnergyValue();
    }

    // Move in the current specified direction for some number of steps
    private static final Random moveSteps = new Random();
    public void move() {
        if (direction == null) {
            return;
        }
        // Move a random number of steps from 1-5 blocks
        int steps = moveSteps.nextInt(5) + 1;
        switch (direction.toLowerCase()) {
            case "north":
                move(getPosition().getPosX(), getPosition().getPosY() - steps);
                break;
            case "south":
                move(getPosition().getPosX(), getPosition().getPosY() + steps);
                break;
            case "east":
                move(getPosition().getPosX() + steps, getPosition().getPosY());
                break;
            case "west":
                move(getPosition().getPosX() - steps, getPosition().getPosY());
                break;
            default:
                return;
        }
    }

    // Move to specified coordinate, basic implementation
    public void move(double x, double y) {
        getPosition().setPosX(x);
        getPosition().setPosY(y);
    }

    public AnimalModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, String direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState);
        this.hunger = hunger;
        this.thirst = thirst;
        this.energy = energy;
        this.survivalStrategy = survivalStrategy;
        this.direction = direction;
    }
}
