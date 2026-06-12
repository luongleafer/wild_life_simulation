package model.animals;

import controller.WorldController;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.entity.*;

import java.util.List;
import java.util.Random;

public class Pig extends HerbivoreLandAnimal implements Edible {


    private int birthCooldown = 0;

    public Pig(EntityCoordinate position){
        super(position, 10, 20, 15, 5);
        this.hungerDepletionMultiplier = 0.2;
        this.survivalStrategy = "passive"; // Passive behavior, will never attack
        this.direction = Direction.SOUTH();
        this.currentState = 1; // Adult by default
        this.age = 10; // total lifespan is 10
        this.directionChangeChance = 0.3;
        this.setSpeed(5.0/20);
        this.setDirection(1, 1);
        this.entityType = "pig";
        this.threatTypes = List.of("wolf", "fox");
        this.fleeingSpeed = 13.0 / 20;
        birthCooldown = 0;
    }

    @Override
    public void ageUp() {
        super.ageUp();
    }

    @Override
    public void Interact(EntityModel entity) {
        // Pigs are herbivores: they eat other stuff
        // Will they interact with other pigs? Probably
        // Just make it walk randomly for now
        // Which means this class is uh, blank
        super.Interact(entity);
    }

    @Override
    public void Interact(BlockModel block) {
        super.Interact(block);
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
        roamRandomly(5.0/20, 7.2/20, Math.PI/3);
//        headRandomly();
//        moveByDistance(getSpeed());
    }
    public void mate(){
        birthCooldown = 0;
    }
}
