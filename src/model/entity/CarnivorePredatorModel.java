package model.entity;

import controller.WorldController;
import model.block.BlockModel;

import java.util.List;

/**
 * Lớp trung gian chuyên biệt cho các loài thú ăn thịt thuần chủng (Carnivore Predator như Wolf, Fox).
 */
public abstract class CarnivorePredatorModel extends PredatorAnimalModel {

    protected int huntSuccessCount = 0;  // Số lần săn mồi thành công
    protected int huntAttemptCount = 0;  // Tổng số lần cố gắng săn mồi

    public CarnivorePredatorModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, double speed, double directionX, double directionY) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, speed, directionX, directionY);
    }

    public CarnivorePredatorModel(EntityCoordinate position) {
        super(position);
    }

    /**
     * Kiểm tra thực thể khác có phải là con mồi hợp lệ hay không (phải là con mồi PreyAnimalModel và còn sống).
     */
    protected boolean isValidPrey(EntityModel entity) {
        if (entity == null) return false;
        if (entity == this) return false;
        if (entity.getHealth() <= 0) return false;

        // Chỉ ăn các con mồi Prey, không ăn thú ăn thịt khác
        if (!(entity instanceof PreyAnimalModel)) return false;

        if (entity instanceof Edible edible) {
            return edible.canBeEaten();
        }

        return false;
    }

    /**
     * Săn và ăn con mồi, đồng thời cập nhật thống kê săn bắn.
     */
    public void huntAndEat(Edible prey) {
        if (prey == null || !prey.canBeEaten()) return;

        this.eat(prey);
        this.huntSuccessCount++;
    }

    protected void recordHuntAttempt() {
        this.huntAttemptCount++;
    }

    /**
     * Trả về tỷ lệ săn mồi thành công (phần trăm).
     */
    public double getHuntSuccessRate() {
        if (huntAttemptCount == 0) return 0.0;
        return (double) huntSuccessCount / huntAttemptCount * 100.0;
    }

    public int getHuntSuccessCount() {
        return huntSuccessCount;
    }

    public int getHuntAttemptCount() {
        return huntAttemptCount;
    }

    @Override
    public abstract void Interact(BlockModel block);

    @Override
    public void Interact(EntityModel entity) {
        // Xử lý thông qua tương tác danh sách để chọn con mồi
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }

    @Override
    public void move() {
        updateAndMove(WorldController.getController().getWorldModel().getBlocksData());
    }
}