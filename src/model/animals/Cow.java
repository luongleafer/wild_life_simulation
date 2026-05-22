package model.animals;

import model.block.BlockModel;
import model.entity.*;

public class Cow extends AnimalModel implements Edible {
    public Cow(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, Direction direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, direction);
    }

    public void Interact(EntityModel entity) {
        // Cows are herbivores: they eat grass
        // Will they interact with other cows? Probably
        // Just make it walk randomly for now
        // Which means this class is uh, blank
    }

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
    // You can eat cows.
    public boolean canBeEaten() {
        return true;
    }
}
