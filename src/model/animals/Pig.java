package model.animals;

import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.entity.Edible;

public class Pig extends AnimalModel implements Edible {
    public Pig(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, AnimalModel.Direction direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, direction);
    }

    public void Interact(EntityModel entity) {
        // Pigs are herbivores: they eat other stuff
        // Will they interact with other pigs? Probably
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
    // You can eat pigs.
    public boolean canBeEaten() {
        return true;
    }

}
