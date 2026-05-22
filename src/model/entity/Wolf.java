package model.entity;

import model.block.BlockModel;

public class Wolf extends AnimalModel {
    public Wolf(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, Direction direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, direction);
        directionChangeChance = 0.15;
    }

    public Wolf(EntityCoordinate position){
        super(
                position,
                100,
                0,
                30,
                80,
                100,
                0,
                100,
                100,
                100,
                "Hunting",
                new Direction(1,0)
        );
        directionChangeChance = 0.15;
    }

    @Override
    public void Interact(BlockModel block) {

    }

    @Override
    public void Interact(EntityModel entity) {

    }

    @Override
    public void move() {
        super.move();
        IO.println("Wolf move to " + getPosition().getPosX() + " " + getPosition().getPosY());
    }
}
