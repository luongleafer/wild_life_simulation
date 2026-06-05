package model.animals.entity;

import model.animals.behavior.HuntStrategy;
import model.animals.behavior.RoamStrategy;
import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.entity.Edible;

import java.util.List;

/**
 * Model cho các loài động vật ăn mọi thứ (omnivore predator).
 *
 * <p>OmnivorePredatorModel là lớp trung gian kế thừa từ {@link PredatorAnimalModel},
 * dành riêng cho các loài kẻ săn mồi ăn cả thịt lẫn thực vật như gấu, hoặc
 * các loài khác được thêm sau.</p>
 *
 * <p>Chức năng chính:</p>
 * <ul>
 *   <li>Kế thừa behavior switching từ PredatorAnimalModel (Hunt/Roam)</li>
 *   <li>Thêm khả năng ăn fruit/plant block ngoài thịt</li>
 *   <li>Thêm logic chọn lựa thức ăn — ăn thịt khi đói, ăn cây khi no (energy high)</li>
 *   <li>Linh hoạt hơn carnivore — có thể sống sót với plant khi không săn được</li>
 * </ul>
 *
 * <p>OmnivorePredatorModel khác CarnivorePredatorModel ở chỗ:</p>
 * <ul>
 *   <li>Có thể tìm và ăn FoodBlock ngoài PreyAnimalModel</li>
 *   <li>hungerDecay thấp hơn — không cần ăn thường xuyên như carnivore</li>
 *   <li>Có cơ chế "food preference" — chọn loại thức ăn dựa trên tình trạng</li>
 * </ul>
 *
 * <p><strong>Ghi chú:</strong> Class này là optional và để dành cho mở rộng sau.
 * Hiện tại simulation chỉ có Wolf (carnivore) và Fox (carnivore). Có thể thêm
 * Bear hoặc Badger sau nếu cần.</p>
 *
 * @see PredatorAnimalModel
 * @see CarnivorePredatorModel
 */
public abstract class OmnivorePredatorModel extends PredatorAnimalModel {

    /**
     * Tốc độ giảm đói mỗi tick.
     * Thấp hơn carnivore (0.04f vs 0.08f) vì omnivore có thêm nguồn thức ăn.
     */
    protected float hungerDecay = 0.04f;

    /**
     * Tốc độ tiêu hao năng lượng.
     * Tương tự carnivore.
     */
    protected float energyDrain = 0.05f;

    /**
     * Ngưỡng energy để omnivore chuyển sang ăn plant thay vì hunting.
     * Nếu energy > foodPreferenceThreshold → ưu tiên ăn plant (nếu tìm được).
     * Mặc định 60 (trên 100).
     */
    protected float foodPreferenceThreshold = 60f;

    /**
     * Số lần săn thành công.
     */
    protected int huntSuccessCount = 0;

    /**
     * Tổng số lần cố gắng săn.
     */
    protected int huntAttemptCount = 0;

    // =====================================================================
    // Constructors
    // =====================================================================

    /**
     * Constructor đầy đủ với tất cả thông số.
     */
    public OmnivorePredatorModel(
            EntityCoordinate position,
            int health, int age,
            int adultAge, int oldAge, int totalLifespan,
            int currentState,
            float hunger, float thirst, float energy,
            double speed, double directionX, double directionY
    ) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState,
                hunger, thirst, energy, speed, directionX, directionY);
    }

    /**
     * Constructor tối giản chỉ với position.
     */
    public OmnivorePredatorModel(EntityCoordinate position) {
        super(position);
    }

    // =====================================================================
    // Omnivore-specific behaviors
    // =====================================================================

    /**
     * Kiểm tra xem entity có phải là con mồi hợp lệ hay không.
     * Tương tự CarnivorePredatorModel — chỉ ăn PreyAnimalModel.
     *
     * @param entity entity cần kiểm tra
     * @return true nếu entity là con mồi hợp lệ
     */
    protected boolean isValidPrey(EntityModel entity) {
        if (entity == null) return false;
        if (entity == this) return false;
        if (entity.getHealth() <= 0) return false;

        if (!(entity instanceof PreyAnimalModel)) return false;

        if (entity instanceof Edible edible) {
            return edible.canBeEaten();
        }

        return false;
    }

    /**
     * Ăn con mồi (thịt).
     *
     * @param prey con mồi là Edible cần ăn
     */
    public void huntAndEat(Edible prey) {
        if (prey == null || !prey.canBeEaten()) return;

        this.eat(prey);
        this.huntSuccessCount++;
    }

    /**
     * Ăn thực vật (cỏ, lá, quả, rễ).
     * Để dành cho FoodBlock interaction sau.
     *
     * <p>Giá trị thức ăn từ plant thấp hơn thịt nhưng vẫn giúp sống sót.</p>
     *
     * @param foodBlock block thực vật cần ăn
     */
    public void eatPlant(BlockModel foodBlock) {
        // Placeholder — sẽ implement khi có FoodBlock class
    }

    /**
     * Ghi nhận một lần cố gắng săn mồi.
     */
    protected void recordHuntAttempt() {
        this.huntAttemptCount++;
    }

    /**
     * Trả về tỷ lệ thành công săn mồi (0-100%).
     */
    public double getHuntSuccessRate() {
        if (huntAttemptCount == 0) return 0.0;
        return (double) huntSuccessCount / huntAttemptCount * 100.0;
    }

    /**
     * Chọn chiến lược ăn dựa trên tình trạng hiện tại.
     *
     * <p>Nếu energy cao (> threshold) → ưu tiên ăn plant nếu tìm được.</p>
     * <p>Nếu energy thấp hoặc không tìm được plant → hunting.</p>
     *
     * @return true nếu nên hunt, false nếu nên ăn plant
     */
    protected boolean shouldHuntOrEatPlant() {
        // Nếu energy cao → ưu tiên plant (save energy, avoid hunt danger)
        if (energy > foodPreferenceThreshold) {
            return false;
        }
        // Nếu energy thấp hoặc hunger cao → hunt
        return true;
    }

    /**
     * Cập nhật trạng thái đói/khát mỗi tick.
     */
    public void updateMetabolism() {
        float speedFactor = (float) getSpeed();
        this.hunger += hungerDecay * speedFactor;
        this.energy -= energyDrain * speedFactor;

        this.hunger = Math.min(hunger, 100f);
        this.energy = Math.max(energy, 0f);

        // Giảm health chậm hơn carnivore vì có thêm nguồn thức ăn
        if (hunger > 85f) {
            this.health -= 1;
        }

        if (energy <= 0f) {
            this.health -= 1;
        }
    }

    /**
     * Trả về tốc độ giảm đói hiện tại.
     */
    public float getHungerDecay() {
        return hungerDecay;
    }

    /**
     * Thiết lập tốc độ giảm đói tùy chỉnh.
     */
    public void setHungerDecay(float hungerDecay) {
        this.hungerDecay = Math.max(0f, hungerDecay);
    }

    /**
     * Trả về tốc độ tiêu hao năng lượng hiện tại.
     */
    public float getEnergyDrain() {
        return energyDrain;
    }

    /**
     * Thiết lập tốc độ tiêu hao năng lượng tùy chỉnh.
     */
    public void setEnergyDrain(float energyDrain) {
        this.energyDrain = Math.max(0f, energyDrain);
    }

    /**
     * Trả về ngưỡng energy để ưu tiên ăn plant.
     */
    public float getFoodPreferenceThreshold() {
        return foodPreferenceThreshold;
    }

    /**
     * Thiết lập ngưỡng energy để ưu tiên ăn plant.
     */
    public void setFoodPreferenceThreshold(float threshold) {
        this.foodPreferenceThreshold = Math.max(0f, Math.min(threshold, 100f));
    }

    /**
     * Trả về số lần săn thành công.
     */
    public int getHuntSuccessCount() {
        return huntSuccessCount;
    }

    /**
     * Trả về số lần cố gắng săn.
     */
    public int getHuntAttemptCount() {
        return huntAttemptCount;
    }

    // =====================================================================
    // Override từ PredatorAnimalModel
    // =====================================================================

    /**
     * OmnivorePredatorModel không override Interact — để PredatorAnimalModel
     * xử lý strategy switching.
     */
    @Override
    public abstract void Interact(BlockModel block);

    @Override
    public void Interact(EntityModel entity) {
    }

    /**
     * Giao cho PredatorAnimalModel chọn strategy.
     */
    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }
}