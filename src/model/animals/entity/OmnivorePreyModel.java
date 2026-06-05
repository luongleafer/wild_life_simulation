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
import model.entity.Edible;

import java.util.List;

/**
 * Model cho các loài động vật ăn mọi thứ (omnivore prey).
 *
 * <p>OmnivorePreyModel là lớp trung gian kế thừa từ {@link PreyAnimalModel},
 * dành riêng cho các loài con mồi ăn cả cỏ lẫn côn trùng/động vật nhỏ
 * như Pig.</p>
 *
 * <p>Chức năng chính:</p>
 * <ul>
 *   <li>Kế thừa toàn bộ behavior switching từ PreyAnimalModel (Flee/Follow/Roam)</li>
 *   <li>Thêm khả năng ăn cỏ (FoodBlock) như herbivore</li>
 *   <li>Thêm khả năng ăn côn trùng (InsectBlock) hoặc thây con vật chết</li>
 *   <li>Có thêm {@code scavengeRange} để tìm thức ăn trong vùng lân cận</li>
 * </ul>
 *
 * <p>OmnivorePreyModel khác PreyAnimalModel ở chỗ:</p>
 * <ul>
 *   <li>Có thể tìm kiếm và ăn các loại thức ăn diverse hơn</li>
 *   <li>Có thể scavenge (ăn thây động vật đã chết) nếu implement</li>
 *   <li>Có thể được mở rộng để tương tác với InsectModel sau</li>
 * </ul>
 *
 */
public abstract class OmnivorePreyModel extends PreyAnimalModel {

    /**
     * Tốc độ mất đói mỗi tick, thường cao hơn herbivore vì omnivore tích cực hơn.
     * Mặc định 0.03f.
     */
    protected float hungerRate = 0.03f;

    /**
     * Tốc độ mất nước mỗi tick.
     * Mặc định 0.012f.
     */
    protected float thirstRate = 0.012f;

    /**
     * Khoảng cách tìm kiếm thức ăn khi scavenging.
     * Nếu tìm thấy FoodBlock trong vùng này → có thể di chuyển về đó.
     * Để dành cho mở rộng sau.
     */
    protected double scavengeRange = 5.0;

    // =====================================================================
    // Constructors
    // =====================================================================

    /**
     * Constructor đầy đủ với tất cả thông số.
     */
    public OmnivorePreyModel(
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
    public OmnivorePreyModel(EntityCoordinate position) {
        super(position);
    }

    // =====================================================================
    // Omnivore-specific behaviors
    // =====================================================================

    /**
     * Ăn thực vật (cỏ, lá, rễ).
     * Tương tự HerbivorePreyModel.
     *
     * @param foodBlock block thực vật cần ăn
     */
    public void eatPlant(BlockModel foodBlock) {
        // Placeholder — sẽ implement khi có FoodBlock class
    }

    /**
     * Ăn côn trùng hoặc động vật nhỏ.
     * Để dành cho InsectModel hoặc small prey interaction sau.
     *
     * <p>Hiện tại có thể scavenge thây động vật chết (EntityModel.health <= 0).</p>
     *
     * @param insectOrSmallPrey entity là côn trùng hoặc động vật nhỏ cần ăn
     */
    public void eatInsect(EntityModel insectOrSmallPrey) {
        if (insectOrSmallPrey == null) return;

        // Nếu là Edible và còn sống/chết → có thể ăn
        if (insectOrSmallPrey instanceof Edible edible && edible.canBeEaten()) {
            this.hunger -= edible.getHungerValue();
            this.energy += edible.getEnergyValue();
        }
    }

    /**
     * Uống nước từ block hoặc entity.
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
     * Scavenge (ăn thây) các động vật chết trong vùng lân cận.
     * Để dành cho mở rộng — sẽ được gọi từ Interact(List) nếu không tìm được
     * thức ăn khác.
     *
     * @param nearbyEntities danh sách entity lân cận
     * @return true nếu tìm được và ăn thây nào đó
     */
    protected boolean scavengeNearby(List<EntityModel> nearbyEntities) {
        for (EntityModel entity : nearbyEntities) {
            if (entity == this) continue;
            if (entity.getHealth() > 0) continue; // chỉ ăn thây chết

            if (distanceTo(entity.getPosition()) <= scavengeRange) {
                if (entity instanceof Edible edible && edible.canBeEaten()) {
                    eatInsect(entity);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Cập nhật trạng thái đói/khát mỗi tick.
     *
     * @see HerbivorePreyModel#updateMetabolism()
     */
    public void updateMetabolism() {
        float speedFactor = (float) getSpeed();
        this.hunger += hungerRate * speedFactor;
        this.thirst += thirstRate * speedFactor;

        this.hunger = Math.min(hunger, 100f);
        this.thirst = Math.min(thirst, 100f);

        if (hunger > 80f) {
            this.health -= 1;
        }
        if (thirst > 80f) {
            this.health -= 1;
        }
    }

    /**
     * Trả về tốc độ mất đói hiện tại.
     */
    public float getHungerRate() {
        return hungerRate;
    }

    /**
     * Thiết lập tốc độ mất đói tùy chỉnh.
     */
    public void setHungerRate(float hungerRate) {
        this.hungerRate = Math.max(0f, hungerRate);
    }

    /**
     * Trả về tốc độ mất nước hiện tại.
     */
    public float getThirstRate() {
        return thirstRate;
    }

    /**
     * Thiết lập tốc độ mất nước tùy chỉnh.
     */
    public void setThirstRate(float thirstRate) {
        this.thirstRate = Math.max(0f, thirstRate);
    }

    /**
     * Trả về khoảng cách scavenge.
     */
    public double getScavengeRange() {
        return scavengeRange;
    }

    /**
     * Thiết lập khoảng cách scavenge.
     */
    public void setScavengeRange(double scavengeRange) {
        this.scavengeRange = Math.max(0, scavengeRange);
    }

    // =====================================================================
    // Override từ PreyAnimalModel
    // =====================================================================

    /**
     * OmnivorePreyModel không override Interact — để PreyAnimalModel
     * xử lý strategy switching.
     */
    @Override
    public abstract void Interact(BlockModel block);

    @Override
    public void Interact(EntityModel entity) {
        // Không tương tác trực tiếp với entity đơn lẻ
    }

    /**
     * Giao cho PreyAnimalModel chọn strategy.
     * Class con có thể override để thêm scavenge logic.
     */
    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }
}