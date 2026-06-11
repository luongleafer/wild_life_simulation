package model.animals;

import controller.WorldController;
import model.block.BlockModel;
import model.entity.*;
import model.generation.*;

public class Elephant extends LandAnimal implements Edible {

    public Elephant(EntityCoordinate position) {
        super(position, 50, 40, 30, 20); // maxHealth, maxHunger, maxThirst, maxEnergy
        this.survivalStrategy = "passive";
        this.direction = Direction.NORTH();
        this.currentState = 1; // Adult by default
        this.age = 0;
        this.setSpeed(2.0 / 20); // di chuyển rất chậm
        this.setDirection(0, 0);
        this.entityType = "elephant";
    }

    @Override
    public void Interact(BlockModel block) {
        if (isHungry()) {
            String blockType = block.getBlockType();
            // Tập tính ăn cỏ: ăn các block cỏ, cây non, hạt giống
            if (blockType.equals("grass") || blockType.equals("sapling") || blockType.equals("seed")) {
                setDirection(0, 0);
                eat((Edible) block);
                WorldController.getController().placeBlock(new DirtBlock(0, 0), block.getPosition().x, block.getPosition().y);
                return;
            } else if (blockType.equals("tree")) {
                setDirection(0, 0);
                // Voi cũng ăn được cây gỗ to (giả lập ăn trực tiếp)
                this.setHunger(this.getHunger() + 10);
                this.setEnergy(this.getEnergy() + 10);
                WorldController.getController().placeBlock(new DirtBlock(0, 0), block.getPosition().x, block.getPosition().y);
                return;
            }
        }
        super.Interact(block);
    }

    @Override
    public float getHungerValue() {
        return 25f;
    }

    @Override
    public float getEnergyValue() {
        return 25f;
    }

    @Override
    public boolean canBeEaten() {
        return false; // Voi có kích thước lớn và không bị các loại động vật khác săn/ăn thịt
    }

    @Override
    public void move() {
        roamRandomly(2.0 / 20, 4.0 / 20, Math.PI / 4); // Di chuyển chậm
    }

    @Override
    public void ageUp() {
        super.ageUp();
        if (currentState == 0 && age >= 150) {
            currentState = 1;
        }
    }
}
