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
    // new method: use enums for direction
    private enum Direction {
        // Coords start from top left corner, not Cartesian
        NORTH(0, -1),
        SOUTH(0, 1),
        EAST(1, 0),
        WEST(-1, 0);

        private final double dx;
        private final double dy;

        Direction(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public double getDx() {
            return dx;
        }

        public double getDy() {
            return dy;
        }
    }

    private final Direction direction;

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

    // Move to specified coordinate, basic implementation
    public void move(double x, double y) {
        getPosition().setPosX(x);
        getPosition().setPosY(y);
    }

    // Move in the current specified direction for some number of steps
    private static final Random moveSteps = new Random();
    public void move() {
        // Move a random number of blocks from 1-5 blocks
        double steps = moveSteps.nextDouble(5) + 1;

        double newX = getPosition().getPosX() + direction.getDx() * steps;
        double newY = getPosition().getPosY() + direction.getDy() * steps;

        move(newX, newY);
    }

    public AnimalModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, Direction direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState);
        this.hunger = hunger;
        this.thirst = thirst;
        this.energy = energy;
        this.survivalStrategy = survivalStrategy;
        this.direction = direction;
    }
}
