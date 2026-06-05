package model.animals.entity;

import model.animals.behavior.FleeStrategy;
import model.animals.behavior.FollowStrategy;
import model.animals.behavior.RoamStrategy;
import model.animals.species.Species;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

/**
 * Model trung gian cho tất cả các loài động vật là con mồi (prey).
 *
 * <PreyAnimalModel kế thừa { AnimalModel} và thêm logic chuyển đổi
 * hành vi dựa trên tình huống sinh tồn:
 *
 *   Nếu là baby và có con trưởng thành cùng loài gần đó → { FollowStrategy}
 *   Nếu có kẻ săn mồi trong vùng nguy hiểm → {FleeStrategy}
 *   Mặc định → { RoamStrategy}
 *
 *
 * Class con (Rabbit, Deer, Turtle...) chỉ cần cung cấp {@link Species} config
 * và { entityType}, không cần tự implement logic chuyển strategy.</p>
 */
public abstract class PreyAnimalModel extends AnimalModel {

    /**
     * Kẻ săn mồi đang đuổi theo con vật này trong tick hiện tại.
     * null nếu không có mối đe dọa nào trong tầm nhìn.
     */
    private EntityModel currentThreat;

    /**
     * Con trưởng thành cùng loài mà baby đang bám theo.
     * Chỉ có giá trị khi { isBaby()} trả về true.
     */
    private AnimalModel currentLeader;

    // --- Các strategy được tái sử dụng thay vì tạo object mới mỗi tick ---

    /** Strategy lang thang mặc định, dùng khi không có tình huống đặc biệt. */
    private final RoamStrategy roamStrategy = new RoamStrategy();

    /**
     * Strategy bỏ chạy, được khởi tạo sẵn với threat = null.
     * Threat sẽ được cập nhật mỗi tick
     */
    private final FleeStrategy fleeStrategy = new FleeStrategy(null);

    /**
     * Strategy bám theo leader, được khởi tạo sẵn với leader = null.
     * Leader sẽ được cập nhật mỗi tick thay vì tạo object mới.
     */
    private final FollowStrategy followStrategy = new FollowStrategy(null);

    /**
     * Khoảng cách tính bằng block để con vật bắt đầu bỏ chạy khi phát hiện
     * kẻ săn mồi. Độc lập với viewDistance của Species.
     */
    private static final double FLEE_TRIGGER_DISTANCE = 8.0;

    // =====================================================================
    // Constructors
    // =====================================================================

    /**
     * Constructor đầy đủ tham số, dùng khi spawn con vật với trạng thái cụ thể.
     */
    public PreyAnimalModel(
            EntityCoordinate position,
            int health, int age,
            int adultAge, int oldAge, int totalLifespan,
            int currentState,
            float hunger, float thirst, float energy,
            double speed, double directionX, double directionY
    ) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState,
                hunger, thirst, energy, "prey", speed, directionX, directionY);
    }

    /**
     * Constructor tối giản chỉ với position, dùng khi spawn nhanh trong demo/test.
     * Các giá trị còn lại được thiết lập trong constructor của class con.
     */
    public PreyAnimalModel(EntityCoordinate position) {
        super(position);
    }

    // =====================================================================
    // Life stage helpers (Task 2)
    // =====================================================================

    /**
     * Kiểm tra con vật có đang ở giai đoạn baby không (currentState == 0).
     *
     * @return true nếu là baby
     */
    public boolean isBaby() {
        return currentState == 0;
    }

    /**
     * Kiểm tra con vật có đang ở giai đoạn trưởng thành không (currentState == 1).
     *
     * @return true nếu là adult
     */
    public boolean isAdult() {
        return currentState == 1;
    }

    /**
     * Kiểm tra con vật có đang ở giai đoạn già không (currentState == 2).
     *
     * @return true nếu là old
     */
    public boolean isOld() {
        return currentState == 2;
    }

    /**
     * Cập nhật life stage dựa trên tuổi hiện tại.
     * Được gọi trong {@link #ageUp()} để tự động chuyển stage.
     * Giữ currentState là int để không phá texture mapping trong view.
     */
    public void updateLifeStage() {
        if (adultAge > 0 && age >= adultAge && currentState < 1) {
            currentState = 1; // Baby → Adult
        }
        if (oldAge > 0 && age >= oldAge && currentState < 2) {
            currentState = 2; // Adult → Old
        }
    }

    /**
     * Override ageUp để tự động cập nhật life stage sau mỗi tick.
     */
    @Override
    public void ageUp() {
        super.ageUp();
        updateLifeStage();
    }

    // =====================================================================
    // Strategy switching (core logic của PreyAnimalModel)
    // =====================================================================

    /**
     * Chọn strategy phù hợp dựa trên trạng thái hiện tại và các entity xung quanh,
     * sau đó thực thi strategy đó.
     *
     * <p>Thứ tự ưu tiên:</p>
     * <ol>
     *   <li>Flee — nếu có kẻ săn mồi trong {@code FLEE_TRIGGER_DISTANCE}</li>
     *   <li>Follow — nếu là baby và có adult cùng loài gần đó</li>
     *   <li>Roam — mặc định</li>
     * </ol>
     *
     * @param nearbyEntities danh sách entity trong vùng lân cận từ WorldModel
     */
    @Override
    public void Interact(List<EntityModel> nearbyEntities) {
        Species species = getSpecies();

        // Tìm kẻ săn mồi gần nhất trong vùng nguy hiểm
        currentThreat = findNearestThreat(nearbyEntities);

        if (currentThreat != null) {
            // Ưu tiên 1: Bỏ chạy
            fleeStrategy.setThreat(currentThreat);
            activeStrategy = fleeStrategy;
        } else if (isBaby() && species != null) {
            // Ưu tiên 2: Baby bám theo adult cùng loài
            currentLeader = findNearestAdultSameSpecies(nearbyEntities);
            if (currentLeader != null) {
                followStrategy.setLeader(currentLeader);
                activeStrategy = followStrategy;
            } else {
                activeStrategy = roamStrategy;
            }
        } else {
            // Mặc định: lang thang
            activeStrategy = roamStrategy;
        }
    }

    /**
     * Thực thi strategy đã được chọn trong {@link #Interact(List)}.
     * WorldModel gọi move() sau Interact() nên thứ tự này khớp với flow hiện tại.
     */
    @Override
    public void move() {
        if (activeStrategy != null) {
            activeStrategy.tick(this, List.of());
        }
    }

    // =====================================================================
    // Private helpers
    // =====================================================================

    /**
     * Tìm kẻ săn mồi gần nhất trong danh sách entity xung quanh.
     * Kẻ săn mồi được nhận diện là {@link PredatorAnimalModel}.
     *
     * @param nearbyEntities danh sách entity cần tìm kiếm
     * @return EntityModel là kẻ săn mồi gần nhất, hoặc null nếu không có
     */
    private EntityModel findNearestThreat(List<EntityModel> nearbyEntities) {
        EntityModel nearest = null;
        double minDist = FLEE_TRIGGER_DISTANCE;

        for (EntityModel entity : nearbyEntities) {
            if (!(entity instanceof PredatorAnimalModel)) continue;
            if (entity == this) continue;

            double dist = distanceTo(entity.getPosition());
            if (dist < minDist) {
                minDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    /**
     * Tìm con trưởng thành cùng loài gần nhất để baby bám theo.
     * "Cùng loài" được xác định qua {@code entityType} giống nhau.
     *
     * @param nearbyEntities danh sách entity cần tìm kiếm
     * @return AnimalModel là adult cùng loài gần nhất, hoặc null nếu không có
     */
    private AnimalModel findNearestAdultSameSpecies(List<EntityModel> nearbyEntities) {
        AnimalModel nearest = null;
        double minDist = Double.MAX_VALUE;

        for (EntityModel entity : nearbyEntities) {
            if (entity == this) continue;
            if (!(entity instanceof PreyAnimalModel prey)) continue;

            // Kiểm tra cùng loài qua entityType
            if (!matchesEntityType(prey)) continue;

            // Chỉ theo adult hoặc old, không theo baby khác
            if (!prey.isAdult() && !prey.isOld()) continue;

            double dist = distanceTo(prey.getPosition());
            if (dist < minDist) {
                minDist = dist;
                nearest = prey;
            }
        }
        return nearest;
    }

    /**
     * Kiểm tra xem entity khác có cùng entityType với con vật này không.
     *
     * @param other entity cần so sánh
     * @return true nếu cùng loài
     */
    private boolean matchesEntityType(EntityModel other) {
        String myType = getEntityType();
        String otherType = other.getEntityType();
        return myType != null && myType.equals(otherType);
    }
}