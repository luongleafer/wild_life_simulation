package model.animals;

import model.block.BlockModel;
import model.entity.*;

import java.util.List;

public class Pig extends AnimalModel implements Edible {
    public Pig(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, 10, 0, 0);
    }

    public Pig(EntityCoordinate position){
        super(position);
        // Default values for pigs, can be changed later if needed
        this.health = 10;
        this.energy = 5;
        this.hunger = 5;
        this.thirst = 5;
        this.survivalStrategy = "passive"; // Passive behavior, will never attack
        this.direction = Direction.SOUTH();
        this.currentState = 1; // Adult by default
        this.age = 10; // total lifespan is 10
        this.directionChangeChance = 0.3;
        this.setSpeed(5.0/20);
        this.setDirection(1, 1);
    }

    @Override
    public void Interact(EntityModel entity) {
        // Pigs are herbivores: they eat other stuff
        // Will they interact with other pigs? Probably
        // Just make it walk randomly for now
        // Which means this class is uh, blank
    }

    @Override
    public void Interact(BlockModel block) {
        // Temporarily blank
    }

    @Override
    public float getHungerValue() {
        return 5f;
    }

    @Override
    public float getEnergyValue() {
        return 10f;
    }

    @Override
    // You can eat pigs.
    public boolean canBeEaten() {
        return true;
    }

    @Override
    public void move() {
        roamRandomly(5.0/20, 13.41/20, Math.PI/3);
    }

    @Override
    public void Interact(List<EntityModel> entities) {

    }
}
