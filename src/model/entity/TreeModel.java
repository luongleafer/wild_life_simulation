package model.entity;

import model.block.BlockModel;

import java.util.List;

public class TreeModel extends EntityModel{
    // seed -> sprout -> tree, so that's 3 stages, so I can go like 0, 1, 2?
    private int growthStage; // 0: seed, 1: sprout, 2: tree
    private double height;

    public TreeModel(EntityCoordinate position) {
        super(position);
        this.health = 10;
        this.age = 0;
        this.adultAge = 10;
        this.oldAge = 30;
        this.totalLifespan = 100;
        this.currentState = 0;
        this.growthStage = 0;
        // still not sure about this height thing
        this.height = 1; // smallest height
        this.entityType = "tree";
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(int growthStage) {
        this.growthStage = growthStage;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public void ageUp() {
        super.ageUp();
        if (age >= 10 && growthStage == 0) {
            growthStage = 1;
            currentState = 1;
            height = 2;
        } else if (age >= 30 && growthStage == 1) {
            growthStage = 2;
            currentState = 2;
            height = 4;
        }
    }

    @Override
    public void Interact(BlockModel block) {
        // Trees are not sentient
    }

    @Override
    public void Interact(EntityModel entity) {
        // Trees are not sentient
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        // Trees are not sentient
    }
}