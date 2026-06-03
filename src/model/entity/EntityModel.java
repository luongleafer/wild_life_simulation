package model.entity;

import model.block.*;

import java.util.List;

public abstract class EntityModel {
    // work in progress, shoddy implementation

    // its coordinates
    protected EntityCoordinate position;
    // its health
    protected int health;
    // its age and grow speed
    protected int age;
    // TEMPORARY METHOD: use 2 more values to dictate when switch to next grow stage
    protected int adultAge;
    protected int oldAge;
    // total lifespan
    protected int totalLifespan;
    // its current state as 0, 1, 2 (baby -> adult -> old)
    protected int currentState;
    // I think we also should have a variable that time it takes to switch to a new state
    protected String entityType;

    protected double hitBoxWidth; // along x-axis
    protected double hitBoxLength; // along y-axis


    public EntityModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState) {
        this.position = position;
        this.health = health;
        this.age = age;
        this.adultAge = adultAge;
        this.oldAge = oldAge;
        this.totalLifespan = totalLifespan;
        this.currentState = currentState;
    }

    // Constructor for position ONLY
    public EntityModel(EntityCoordinate position) {
        this.position = position;
    }

    // Unlike blocks, I think everything here should be able to be modified if required

    public EntityCoordinate getPosition() {
        return position;
    }

    public void setPosition(EntityCoordinate position) {
        this.position = position;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAdultAge() {
        return adultAge;
    }

    public void setAdultAge(int adultAge) {
        this.adultAge = adultAge;
    }

    public int getOldAge() {
        return oldAge;
    }

    public void setOldAge(int oldAge) {
        this.oldAge = oldAge;
    }

    public int getTotalLifespan() {
        return totalLifespan;
    }

    public void setTotalLifespan(int totalLifespan) {
        this.totalLifespan = totalLifespan;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public void ageUp() {
        this.age += 1;
    }

    public void receiveDamage(int damage) {
        this.health -= damage;
    }

    public void advanceLifeStage() {
        // currently I see no factor that allows something to grow so
        // Either the statement that switches the stage as soon as adult_age or old_age is reached
        // can be implemented elsewhere, or here
        this.currentState += 1;
    }

    public String getEntityType() {
        return entityType;
    }

    // UPDATE: still unsure about this one
    public abstract void Interact(BlockModel block);
    public abstract void Interact(EntityModel entity);
    public abstract void Interact(List<EntityModel> entities);

    public double getHitBoxWidth() {
        return hitBoxWidth;
    }

    public double getHitBoxLength() {
        return hitBoxLength;
    }
}