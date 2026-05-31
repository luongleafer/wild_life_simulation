package model.animals;

import model.block.BlockModel;
import model.entity.*;

import java.util.List;
import java.util.Random;

public class Cow extends AnimalModel implements Edible {
    private int maxThirst = 10;
    public Cow(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, 10, 0, 0);
    }

    public Cow(EntityCoordinate position){
        super(position);
        // Default values for cows, can be changed later if needed
        this.health = 10;
        this.energy = 10;
        this.hunger = 5;
        this.thirst = 2;
        this.survivalStrategy = "passive"; // Passive behavior, will never attack
        this.direction = Direction.NORTH();
        this.currentState = 0; // Adult by default
        this.age = 0; // total lifespan is 10
        this.setSpeed(7.5/20);
        this.setDirection(0, 0);
        this.entityType = "cow";
    }

    @Override
    public void Interact(EntityModel entity) {
        // Cows are herbivores: they eat grass
        // Will they interact with other cows? Probably
        // Just make it walk randomly for now
        // Which means this class is uh, blank
    }

    @Override
    public void Interact(BlockModel block) {
        if(block.getBlockType().equals("water")){
            if(thirst <= maxThirst / 2.0) {
                setDirection(0, 0);
                thirst += 1;
            }
            else{
                headAwayFrom(block.getPosition());
            }
        }
        // Temporarily blank
    }

    @Override
    public void Interact(List<EntityModel> entities) {

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
    // You can eat cows.
    public boolean canBeEaten() {
        return true;
    }

    @Override
    public void move() {
        roamRandomly(7.5/20, 11.11/20,Math.PI/3);
        thirst -= 0.1f;
    }

    @Override
    public void ageUp() {
        super.ageUp();
        if(currentState == 0 && age >= 100){
            Random ageRandom = new Random();
            currentState = 1;
        }
    }
}
