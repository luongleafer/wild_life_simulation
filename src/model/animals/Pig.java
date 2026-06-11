package model.animals;

import controller.WorldController;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.entity.*;

import java.util.List;
import java.util.Random;

public class Pig extends LandAnimal implements Edible {

    static {
    }

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
        if(entity instanceof Wolf wolf){
            setSpeed(7.0/20);
            headAwayFrom(new BlockCoordinate((int)wolf.getPosition().posX, (int)wolf.getPosition().posY), 2.0);
        }
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
        setSpeed(5.5 / 20);
        if(shouldSeekWater() && moveTowardNearestWater(10.0, 1.2, 0.8)){
            return;
        }
        if(shouldSeekFood() && moveTowardNearestFood(7.0, 1.0, 0.8)){
            return;
        }
        headRandomly();
        moveByDistance(getSpeed());
    }

    @Override
    protected EntityCoordinate findNearestFoodTarget(double radius) {
        return findNearestBlockCenterInRadius(radius, block -> {
            String blockType = block.getBlockType();
            return "grass".equals(blockType) || "seed".equals(blockType) || "sapling".equals(blockType);
        });
    }

    public void mate(){
        birthCooldown = 0;
    }
}
