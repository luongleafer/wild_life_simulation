package model.entity;

import model.block.BlockModel;

import java.util.List;

/**
 * Lớp trung gian chuyên biệt cho các con mồi ăn cỏ (Herbivore Prey như Cow, Pig, Rabbit).
 */
public abstract class HerbivorePreyModel extends PreyAnimalModel {

    public HerbivorePreyModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, double speed, double directionX, double directionY) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, speed, directionX, directionY);
    }

    public HerbivorePreyModel(EntityCoordinate position) {
        super(position);
    }

    /**
     * Ăn thực vật (dành cho tương tác với FoodBlock cây cỏ).
     */
    public void eatPlant(BlockModel foodBlock) {
        // Có thể mở rộng thêm logic tương tác trực tiếp với block cỏ
    }

    /**
     * Uống nước từ block hoặc nguồn nước.
     */
    public void drinkWater(Drinkable waterSource) {
        if (waterSource == null || !waterSource.canBeDrank()) {
            return;
        }
        setThirst(this.getThirst() + waterSource.getThirstValue());
        setEnergy(this.getEnergy() + waterSource.getEnergyValue());
    }

    @Override
    public abstract void Interact(BlockModel block);

    @Override
    public void Interact(EntityModel entity) {
        // Con mồi ăn cỏ không chủ động tương tác đơn lẻ với các thực thể khác
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }
}