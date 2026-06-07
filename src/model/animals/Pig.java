package model.animals;

import controller.WorldController;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.entity.*;

import java.util.List;
import java.util.Random;

public class Pig extends LandAnimal implements Edible {

    private int birthCooldown = 0;

    public Pig(EntityCoordinate position){
        super(position);
        // Default values for pigs, can be changed later if needed
        this.health = 10;
        this.energy = 5;
        this.hunger = 5;
        this.thirst = 5;
        this.maxThirst = 15;
        this.maxHunger = 20;
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
        birthCooldown++;
    }

    @Override
    public void Interact(EntityModel entity) {
        // Pigs are herbivores: they eat other stuff
        // Will they interact with other pigs? Probably
        // Just make it walk randomly for now
        // Which means this class is uh, blank
        if(entity instanceof Wolf wolf){
            setSpeed(10.0/20);
            headAwayFrom(new BlockCoordinate((int)wolf.getPosition().posX, (int)wolf.getPosition().posY), 2.0);
        }
        if(entity instanceof Pig pig && pig.readyToMate() && readyToMate()){
            if(distanceTo(pig.getPosition()) <= 1){
                if(new Random().nextDouble() <= 0.001){
                    WorldController.getController().requestSpawnEntity(new Pig(new EntityCoordinate(position.posX + 1, position.posY + 1)));
                    mate();
                    pig.mate();
                }
            }
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
//        roamRandomly(5.0/20, 13.41/20, Math.PI/3);
        headRandomly();
        moveByDistance(getSpeed());
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        entities.forEach(this::Interact);

    }

    public boolean readyToMate(){
        int maxTicksBetweenBirth = 400;
        return birthCooldown >= maxTicksBetweenBirth;
    }

    public void mate(){
        IO.println("mated");
        birthCooldown = 0;
    }
}
