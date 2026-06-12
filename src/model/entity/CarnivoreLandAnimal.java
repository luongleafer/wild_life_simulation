package model.entity;

import model.animals.Pig;
import view.audio.SoundEngine;

import java.util.ArrayList;
import java.util.List;

public class CarnivoreLandAnimal extends LandAnimal{
    protected List<String> preyTypes;
    protected  int attackCooldown = 20; // 1 second cooldown
    private int lastAttack = 0;
    protected int attackStrength = 1;
    protected int attackRange = 5;
    protected EntityModel currentTarget;
    public CarnivoreLandAnimal(EntityCoordinate position) {
        super(position);
        currentTarget = null;
        preyTypes = new ArrayList<>();
    }

    public CarnivoreLandAnimal(EntityCoordinate position, double maxHealth, double maxHunger, double maxThirst, double maxEnergy) {
        super(position, maxHealth, maxHunger, maxThirst, maxEnergy);
        currentTarget = null;
        lastAttack = 0;
    }

    @Override
    public void Interact(EntityModel entity) {
        super.Interact(entity);
    }

    @Override
    public void ageUp() {
        super.ageUp();
        lastAttack++;
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        EntityModel toFollow = entities.stream().filter(entity -> preyTypes.contains(entity.getEntityType())).findFirst().orElse(null);
        if(toFollow != null) {
            if(!isHungry()) return;
            currentTarget = toFollow;
            attack();
        }
        else{
            currentTarget = null;
        }

        entities.forEach(this::Interact);
    }

    private void attack(){
        if(currentTarget == null) return;
        if(getPosition().distance(currentTarget.getPosition()) <= attackRange) {
            if(lastAttack >= attackCooldown) {
                currentTarget.receiveDamage(attackStrength);
                if(currentTarget.getHealth() <= 0) {
                    eat((Edible) currentTarget);
                }
                SoundEngine.getEngine().playSound(entityType + "_eat");
                lastAttack = 0;
            }
        }
    }

    @Override
    public void move() {
        if(currentTarget != null){
            moveToward(currentTarget.getPosition(), 2.0);
        }
    }
}
