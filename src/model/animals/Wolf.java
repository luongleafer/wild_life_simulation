package model.animals;

import model.block.BlockModel;
import model.entity.*;
import view.audio.SoundEngine;

import java.util.List;

public class Wolf extends LandAnimal implements Edible {
    EntityModel currentTarget = null;
    private final int attackCooldown = 20; // 1 second cooldown
    private int lastAttack = 0;

    @Override
    public void ageUp() {
        super.ageUp();
        lastAttack++;
    }

    public Wolf(EntityCoordinate position){
        super(position);
        // Default values for wolves, can be changed later if needed
        this.health = 8;
        this.energy = 10;
        this.hunger = 5;
        this.thirst = 5;
        this.maxThirst = 20;
        this.maxHunger = 30;
        this.survivalStrategy = "passive"; // Passive behavior, will never attack
        this.direction = Direction.SOUTH();
        this.currentState = 1; // Adult by default
        this.age = 15; // total lifespan is 10
        this.directionChangeChance = 0.1;
        this.setSpeed(0.111);
        this.setDirection(0,0);
        this.entityType = "wolf";
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
    // Can't eat wolves.
    public float getHungerValue() {
        return 0;
    }

    @Override
    // Can't gain energy from wolves.
    public float getEnergyValue() {
        return 0;
    }

    @Override
    // Not sure if it's acceptable to eat wolves.
    public boolean canBeEaten() {
        return false;
    }

    @Override
    public void move() {
        if(currentTarget == null) {
            roamRandomly(0.111, 0.200, Math.PI / 3);
            setSpeed(0.111);
        }
        else{
            moveToward(currentTarget.getPosition(), 2);
        }
        hunger -= 0.01f;
    }

    @Override
    public void Interact(List<EntityModel> entities) {
//        IO.println("Wolf is interacting with a list of " + entities.size() + " entities");
        entities.forEach(this::Interact);
       // filter out pigs
        EntityModel toFollow = entities.stream().filter(entity -> entity instanceof Pig).findFirst().orElse(null);
        if(toFollow != null) {
            if(hunger >= maxHunger) return;
            currentTarget = toFollow;
            if(getPosition().distance(toFollow.getPosition()) < 5) {
                if(lastAttack >= attackCooldown) {
                    toFollow.receiveDamage(1);
                    if(toFollow.getHealth() <= 0) {
                        hunger += 5;
                    }
                    SoundEngine.getEngine().playSound("wolf_eat");
                    lastAttack = 0;
                }
            }
        }
        else{
            currentTarget = null;
        }

    }

    public boolean hasJustAttacked(){
        return lastAttack == 0;
    }
}
