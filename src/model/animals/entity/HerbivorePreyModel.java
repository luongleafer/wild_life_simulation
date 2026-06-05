package model.animals.entity;

import model.animals.behavior.FleeStrategy;
import model.animals.behavior.FollowStrategy;
import model.animals.behavior.RoamStrategy;
import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.Drinkable;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

/**
 * Model cho các loài động vật ăn cỏ (herbivore prey).
 *
 * <p>HerbivorePreyModel là lớp trung gian kế thừa từ {@link PreyAnimalModel},
 * dành riêng cho các loài con mồi ăn cỏ và lá cây như Rabbit, Sheep, Cow, Deer.</p>
 *
 * <p>Chức năng chính:</p>
 * <ul>
 *   <li>Kế thừa toàn bộ behavior switching từ PreyAnimalModel (Flee/Follow/Roam)</li>
 *   <li>Thêm khả năng ăn cây (FoodBlock) và uống nước (WaterBlock) — để mở rộng sau</li>
 *   <li>Có thể cấu hình riêng {@code hungerRate}, {@code thirstRate} theo loài</li>
 * </ul>
 *
 * <p>Các class con (Rabbit, Sheep, Cow, Turtle, Deer) chỉ cần:</p>
 * <ol>
 *   <li>Khai báo static Species config</li>
 *   <li>Gán các stats mặc định trong constructor</li>
 *   <li>Implement {@link model.entity.Edible} để có thể bị ăn</li>
 * </ol>
 *
 *
 */
public abstract class HerbivorePreyModel extends PreyAnimalModel {

    /**
     * Tốc độ mất đói mỗi tick.
     * Giá trị dương → hunger tăng (thích ngược lại, nhưng giữ theo code cũ).
     * Mặc định 0.02 × tốc độ di chuyển.
     */
    protected float hungerRate = 0.02f;

    /**
     * Tốc độ mất nước mỗi tick.
     * Mặc định 0.01 × tốc độ di chuyển.
     */
    protected float thirstRate = 0.01f;

    // =====================================================================
    // Constructors
    // =====================================================================

    /**
     * Constructor đầy đủ với tất cả thông số.
     * Dùng khi spawn herbivore với các giá trị tùy chỉnh.
     */
    public HerbivorePreyModel(
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
     * Các giá trị khác sẽ được thiết lập trong constructor của class con.
     */
    public HerbivorePreyModel(EntityCoordinate position) {
        super(position);
    }

    // =====================================================================
    // Herbivore-specific behaviors
    // =====================================================================

    /**
     * Ăn thực vật (cỏ, lá cây, hoa, rễ).
     * Để dành cho food block interaction sau khi có FoodBlock system.
     *
     * <p>Sẽ được gọi từ Interact(BlockModel) khi con vật gặp FoodBlock.</p>
     *
     * @param foodBlock block thực vật cần ăn
     */
    public void eatPlant(BlockModel foodBlock) {
        // Placeholder — sẽ implement khi có FoodBlock class
        // if (foodBlock instanceof FoodBlockModel) {
        //     FoodBlockModel food = (FoodBlockModel) foodBlock;
        //     this.hunger -= food.getNutrition();
        // }
    }

    /**
     * Uống nước từ block hoặc entity.
     * Để dành cho water block interaction sau.
     *
     * @param waterSource block/entity nước cần uống
     */
    public void drinkWater(Drinkable waterSource) {
        if (waterSource == null || !waterSource.canBeDrank()) {
            return;
        }
        this.thirst -= waterSource.getThirstValue();
        this.energy += waterSource.getEnergyValue();
    }

    /**
     * Cập nhật trạng thái đói/khát mỗi tick.
     * Được gọi từ WorldModel.update() hoặc ageUp().
     *
     * <p>Càng di chuyển nhanh (speed cao) → càng mất năng lượng nhanh.</p>
     */
    public void updateMetabolism() {
        float speedFactor = (float) getSpeed();
        this.hunger += hungerRate * speedFactor;
        this.thirst += thirstRate * speedFactor;

        // Limit để tránh overflow
        this.hunger = Math.min(hunger, 100f);
        this.thirst = Math.min(thirst, 100f);

        // Nếu đói hoặc khát quá nhiều → giảm health
        if (hunger > 80f) {
            this.health -= 1; // giảm 1 health/tick nếu đói nghiêm trọng
        }
        if (thirst > 80f) {
            this.health -= 1; // tương tự với khát
        }
    }

    /**
     * Trả về tốc độ mất đói hiện tại.
     *
     * @return hungerRate
     */
    public float getHungerRate() {
        return hungerRate;
    }

    /**
     * Thiết lập tốc độ mất đói tùy chỉnh theo loài.
     * Ví dụ: Rabbit mất đói nhanh hơn Cow.
     *
     * @param hungerRate tốc độ mất đói mới
     */
    public void setHungerRate(float hungerRate) {
        this.hungerRate = Math.max(0f, hungerRate);
    }

    /**
     * Trả về tốc độ mất nước hiện tại.
     *
     * @return thirstRate
     */
    public float getThirstRate() {
        return thirstRate;
    }

    /**
     * Thiết lập tốc độ mất nước tùy chỉnh theo loài.
     *
     * @param thirstRate tốc độ mất nước mới
     */
    public void setThirstRate(float thirstRate) {
        this.thirstRate = Math.max(0f, thirstRate);
    }

    // =====================================================================
    // Override từ PreyAnimalModel
    // =====================================================================

    /**
     * HerbivorePreyModel không override Interact — để PreyAnimalModel
     * xử lý strategy switching. Class con sẽ override để xử lý block nếu cần.
     */
    @Override
    public abstract void Interact(BlockModel block);

    /**
     * HerbivorePreyModel không override Interact(EntityModel) —
     * để PreyAnimalModel xử lý qua List<EntityModel>.
     */
    @Override
    public void Interact(EntityModel entity) {
        // Không tương tác với entity khác (ngoài Flee/Follow)
    }

    /**
     * Giao cho PreyAnimalModel chọn strategy.
     */
    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }
}