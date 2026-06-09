package model.animals;

import controller.WorldController;
import model.block.BlockModel;
import model.entity.*;
import model.generation.DirtBlock;
import model.generation.GrassBlock;

import java.util.List;
import java.util.Random;

public class Cow extends LandAnimal implements Edible {

    static {
        EntityFactory.register("cow", Cow::new);
    }


    public Cow(EntityCoordinate position){
        super(position, 10, 10, 10, 10);
        // Default values for cows, can be changed later if needed
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
        super.Interact(entity);
        // Cows are herbivores: they eat grass
        // Will they interact with other cows? Probably
        // Just make it walk randomly for now
        // Which means this class is uh, blank
    }

    @Override
    public void Interact(BlockModel block) {
        super.Interact(block);
        if(block.getBlockType().equals("grass")){
            if(isHungry()) {
                GrassBlock grassBlock = (GrassBlock)block;
                setDirection(0, 0);
                eat(grassBlock);
                WorldController.getController().placeBlock(new DirtBlock(0,0,0), block.getPosition().x, block.getPosition().y);
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
