package model.animals;

import model.block.BlockModel;
import model.entity.*;
import view.audio.SoundEngine;

import java.util.List;

public class Wolf extends CarnivoreLandAnimal {
    EntityModel currentTarget = null;

    @Override
    public void ageUp() {
        super.ageUp();
    }

    public Wolf(EntityCoordinate position){
        super(position, 8, 30, 20, 10);
        // Default values for wolves, can be changed later if needed
        this.survivalStrategy = "passive"; // Passive behavior, will never attack
        this.direction = Direction.SOUTH();
        this.currentState = 1; // Adult by default
        this.age = 15; // total lifespan is 10
        this.directionChangeChance = 0.1;
        this.setSpeed(0.111);
        this.setDirection(0,0);
        this.entityType = "wolf";
        this.preyTypes = List.of("pig");
    }
    @Override
    public void Interact(BlockModel block) {
        // Wolves usually do nothing with blocks
        super.Interact(block);
    }

    @Override
    public void Interact(EntityModel entity) {
        super.Interact(entity);
        // They eat small animals, like cows and pigs I guess?
//        boolean isPrey = entity instanceof Pig || entity instanceof Cow;
//        if (isPrey) {
//            this.eat((Edible) entity);
//        }
    }

    @Override
    public void move() {
        roamRandomly(0.111, 0.200, Math.PI / 3);
        super.move();
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }


}
