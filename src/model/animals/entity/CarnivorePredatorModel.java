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
 * Model cho các loài động vật ăn thịt thuần chủng (carnivore predator).
 *
 * <p>CarnivorePredatorModel là lớp trung gian kế thừa từ {@link PredatorAnimalModel},
 * dành riêng cho các loài kẻ săn mồi ăn thịt như Wolf và Fox.</p>
 *
 * <p>Chức năng chính:</p>
 * <ul>
 *   <li>Kế thừa toàn bộ behavior switching từ PredatorAnimalModel (Hunt/Roam)</li>
 *   <li>Thêm logic phân loại prey — chỉ săn PreyAnimalModel, bỏ qua carnivore khác</li>
 *   <li>Có thể cấu hình {@code hungerDecay}, {@code energyDrain} theo loài</li>
 *   <li>Theo dõi "hunt success rate" — tỷ lệ lần săn thành công</li>
 * </ul>
 *
 * <p>CarnivorePredatorModel khác PredatorAnimalModel ở chỗ:</p>
 * <ul>
 *   <li>Tự động từ chối ăn carnivore khác (safety check trong {@code isValidPrey()})</li>
 *   <li>Có thể theo dõi nutrition stats — câu hỏi "cần ăn bao nhiêu để sống"</li>
 *   <li>Có thể mở rộng pack hunting behavior sau</li>
 * </ul>
 *
 *
 *
 *
 */
public abstract class CarnivorePredatorModel extends PredatorAnimalModel {

    /**
     * Tốc độ giảm đói mỗi tick (khi không ăn).
     * Carnivore phải ăn thường xuyên nếu không sẽ chết.
     * Mặc định 0.08f (cao hơn herbivore).
     */
    protected float hungerDecay = 0.08f;

    /**
     * Tốc độ tiêu hao năng lượng khi di chuyển.
     * Săn mồi tốn năng lượng nhiều hơn lang thang.
     * Mặc định 0.05f.
     */
    protected float energyDrain = 0.05f;

    /**
     * Số lần săn được con mồi — dùng thống kê behavior.
     */
    protected int huntSuccessCount = 0;

    /**
     * Tổng số lần cố gắng săn (bắt đầu HuntStrategy) — dùng tính tỷ lệ thành công.
     */
    protected int huntAttemptCount = 0;

    // =====================================================================
    // Constructors
    // =====================================================================

    /**
     * Constructor đầy đủ với tất cả thông số.
     */
    public CarnivorePredatorModel(
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
    public CarnivorePredatorModel(EntityCoordinate position) {
        super(position);
    }

    // =====================================================================
    // Carnivore-specific behaviors
    // =====================================================================

    /**
     * Kiểm tra xem entity có phải là con mồi hợp lệ hay không.
     *
     * <p>Con mồi hợp lệ:</p>
     * <ul>
     *   <li>Là PreyAnimalModel (không ăn carnivore khác)</li>
     *   <li>Còn sống (health > 0)</li>
     *   <li>Có thể ăn được (canBeEaten() == true)</li>
     * </ul>
     *
     * @param entity entity cần kiểm tra
     * @return true nếu entity là con mồi hợp lệ
     */
    protected boolean isValidPrey(EntityModel entity) {
        if (entity == null) return false;
        if (entity == this) return false;
        if (entity.getHealth() <= 0) return false;

        // Chỉ ăn PreyAnimalModel, không ăn carnivore khác
        if (!(entity instanceof PreyAnimalModel)) return false;

        // Kiểm tra canBeEaten
        if (entity instanceof Edible edible) {
            return edible.canBeEaten();
        }

        return false;
    }

    /**
     * Ăn con mồi — ngoài eat() từ AnimalModel, ghi nhận thành công săn.
     *
     * <p>Được gọi từ HuntStrategy.tick() khi predator đủ gần prey.</p>
     *
     * @param prey con mồi là Edible cần ăn
     */
    public void huntAndEat(Edible prey) {
        if (prey == null || !prey.canBeEaten()) return;

        this.eat(prey);
        this.huntSuccessCount++;
    }

    /**
     * Ghi nhận một lần cố gắng săn mồi.
     * Được gọi từ {@link #Interact(List)} khi bắt đầu HuntStrategy.
     */
    protected void recordHuntAttempt() {
        this.huntAttemptCount++;
    }

    /**
     * Trả về tỷ lệ thành công săn mồi (0-100%).
     * Dùng để đánh giá hiệu suất của predator.
     *
     * @return phần trăm thành công, hoặc 0 nếu chưa từng săn
     */
    public double getHuntSuccessRate() {
        if (huntAttemptCount == 0) return 0.0;
        return (double) huntSuccessCount / huntAttemptCount * 100.0;
    }

    /**
     * Cập nhật trạng thái đói/khát mỗi tick.
     * Carnivore mất đói nhanh hơn — cần ăn thường xuyên.
     */
    public void updateMetabolism() {
        float speedFactor = (float) getSpeed();
        this.hunger += hungerDecay * speedFactor;
        this.energy -= energyDrain * speedFactor;

        this.hunger = Math.min(hunger, 100f);
        this.energy = Math.max(energy, 0f);

        // Nếu đói quá nhiều → giảm health
        if (hunger > 85f) {
            this.health -= 2; // carnivore chết nhanh nếu đói
        }

        // Nếu hết năng lượng → giảm health
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
     * Fox có thể có hungerDecay cao hơn Wolf (săn khó hơn).
     *
     * @param hungerDecay tốc độ mới
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
     *
     * @param energyDrain tốc độ mới
     */
    public void setEnergyDrain(float energyDrain) {
        this.energyDrain = Math.max(0f, energyDrain);
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
     * CarnivorePredatorModel không override Interact — để PredatorAnimalModel
     * xử lý strategy switching.
     */
    @Override
    public abstract void Interact(BlockModel block);

    @Override
    public void Interact(EntityModel entity) {
        // Xử lý qua List để dùng PredatorAnimalModel logic
    }

    /**
     * Giao cho PredatorAnimalModel chọn strategy.
     * Class con có thể override để thêm logic đặc biệt nếu cần.
     */
    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }
}