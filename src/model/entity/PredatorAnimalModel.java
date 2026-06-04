package model.animals.entity;

import model.animals.behavior.HuntStrategy;
import model.animals.behavior.RoamStrategy;
import model.animals.species.Species;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

/**
 * Model trung gian cho tất cả các loài động vật là kẻ săn mồi (predator).
 *
 * <p>PredatorAnimalModel kế thừa {@link AnimalModel} và thêm logic chuyển đổi
 * hành vi giữa săn mồi và lang thang:</p>
 * <ul>
 *   <li>Nếu đang có target và target còn sống, còn trong tầm nhìn → {@link HuntStrategy}</li>
 *   <li>Nếu chưa có target: quét FOV để tìm con mồi mới → {@link HuntStrategy}</li>
 *   <li>Không tìm được con mồi nào → {@link RoamStrategy}</li>
 * </ul>
 *
 * <p>Target được giữ nguyên giữa các tick để tránh "flickering" (đổi target
 * liên tục). Target chỉ bị xóa khi con mồi chết hoặc {@link HuntStrategy}
 * báo hết persistence window.</p>
 *
 * <p>Class con (Wolf, Fox...) chỉ cần cung cấp {@link Species} config và
 * {@code entityType}, không cần tự implement logic chọn strategy.</p>
 */
public abstract class PredatorAnimalModel extends AnimalModel {

    /**
     * Con mồi đang bị nhắm tới hiện tại.
     * null nếu kẻ săn mồi đang lang thang chưa tìm được mục tiêu.
     */
    private EntityModel currentTarget;

    // --- Các strategy được tái sử dụng thay vì tạo object mới mỗi tick ---

    /** Strategy lang thang mặc định, dùng khi không tìm được con mồi nào. */
    private final RoamStrategy roamStrategy = new RoamStrategy();

    /**
     * Strategy săn mồi, được khởi tạo sẵn với target = null.
     * Target sẽ được cập nhật mỗi tick thay vì tạo object mới.
     */
    private final HuntStrategy huntStrategy = new HuntStrategy(null);

    // =====================================================================
    // Constructors
    // =====================================================================

    /**
     * Constructor đầy đủ tham số, dùng khi spawn kẻ săn mồi với trạng thái cụ thể.
     */
    public PredatorAnimalModel(
            EntityCoordinate position,
            int health, int age,
            int adultAge, int oldAge, int totalLifespan,
            int currentState,
            float hunger, float thirst, float energy,
            double speed, double directionX, double directionY
    ) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState,
                hunger, thirst, energy, "predator", speed, directionX, directionY);
    }

    /**
     * Constructor tối giản chỉ với position, dùng khi spawn nhanh trong demo/test.
     */
    public PredatorAnimalModel(EntityCoordinate position) {
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
     * Giữ currentState là int để không phá texture mapping trong view.
     */
    public void updateLifeStage() {
        if (adultAge > 0 && age >= adultAge && currentState < 1) {
            currentState = 1;
        }
        if (oldAge > 0 && age >= oldAge && currentState < 2) {
            currentState = 2;
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
    // Strategy switching (core logic của PredatorAnimalModel)
    // =====================================================================

    /**
     * Chọn strategy phù hợp dựa trên target hiện tại và các entity xung quanh,
     * sau đó đặt activeStrategy để {@link #move()} thực thi.
     *
     * <p>Thứ tự ưu tiên:</p>
     * <ol>
     *   <li>Giữ target cũ nếu còn sống và HuntStrategy còn trong persistence window.</li>
     *   <li>Quét FOV tìm con mồi mới nếu không còn target.</li>
     *   <li>Roam nếu không tìm được gì.</li>
     * </ol>
     *
     * @param nearbyEntities danh sách entity trong vùng lân cận từ WorldModel
     */
    @Override
    public void Interact(List<EntityModel> nearbyEntities) {
        Species species = getSpecies();

        // Kiểm tra target cũ còn dùng được không
        if (currentTarget != null) {
            boolean targetDead = currentTarget.getHealth() <= 0;
            boolean huntExpired = !huntStrategy.isStillTracking();

            if (targetDead || huntExpired) {
                currentTarget = null;
            }
        }

        // Nếu chưa có target, quét FOV tìm con mồi mới
        if (currentTarget == null && species != null) {
            currentTarget = findPreyInFOV(nearbyEntities, species);
        }

        if (currentTarget != null) {
            // Có target: săn mồi
            huntStrategy.setTarget(currentTarget);
            activeStrategy = huntStrategy;
        } else {
            // Không có target: lang thang
            activeStrategy = roamStrategy;
        }
    }

    /**
     * Thực thi strategy đã được chọn trong {@link #Interact(List)}.
     */
    @Override
    public void move() {
        if (activeStrategy != null) {
            activeStrategy.tick(this, List.of());
        }
    }

    /**
     * Trả về con mồi đang bị nhắm tới hiện tại.
     *
     * @return EntityModel là target, hoặc null nếu đang lang thang
     */
    public EntityModel getCurrentTarget() {
        return currentTarget;
    }

    // =====================================================================
    // Private helpers
    // =====================================================================

    /**
     * Quét danh sách entity xung quanh để tìm con mồi đầu tiên trong FOV.
     * Con mồi được nhận diện là {@link PreyAnimalModel}.
     * Ưu tiên con mồi gần nhất để tránh đuổi theo con ở xa.
     *
     * @param nearbyEntities danh sách entity cần tìm kiếm
     * @param species        config loài để lấy FOV và viewDistance
     * @return EntityModel là con mồi gần nhất trong FOV, hoặc null nếu không có
     */
    private EntityModel findPreyInFOV(List<EntityModel> nearbyEntities, Species species) {
        EntityModel nearest = null;
        double minDist = Double.MAX_VALUE;

        for (EntityModel entity : nearbyEntities) {
            if (entity == this) continue;
            if (!(entity instanceof PreyAnimalModel)) continue;
            if (entity.getHealth() <= 0) continue;

            boolean inFov = isInFieldOfView(
                    entity.getPosition(),
                    species.getFovRadians(),
                    species.getViewDistance()
            );
            if (!inFov) continue;

            double dist = distanceTo(entity.getPosition());
            if (dist < minDist) {
                minDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }
}