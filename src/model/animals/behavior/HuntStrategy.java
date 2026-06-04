package model.animals.behavior;

import model.animals.entity.PredatorAnimalModel;
import model.animals.species.Species;
import model.entity.AnimalModel;
import model.entity.Edible;
import model.entity.EntityModel;

import java.util.List;

/**
 * Hành vi săn mồi của kẻ săn mồi.
 *
 * <p>HuntStrategy được kích hoạt khi {@link PredatorAnimalModel} đã xác định
 * được con mồi mục tiêu. Kẻ săn mồi sẽ:</p>
 * <ol>
 *   <li>Kiểm tra xem con mồi còn trong tầm nhìn hay không.</li>
 *   <li>Nếu có, di chuyển về phía con mồi với tốc độ nhân {@code huntSpeedMultiplier}.</li>
 *   <li>Nếu đủ gần (trong {@code attackRange}), tấn công và ăn con mồi.</li>
 *   <li>Nếu mất dấu con mồi, giữ hướng cũ thêm {@code PERSISTENCE_TICKS} tick
 *       trước khi từ bỏ.</li>
 * </ol>
 *
 * <p>Target được giữ nguyên giữa các tick cho đến khi con mồi chết hoặc ra
 * khỏi tầm nhìn quá lâu, tránh việc kẻ săn mồi liên tục đổi mục tiêu.</p>
 */
public class HuntStrategy implements BehaviorStrategy {

    /**
     * Số tick kẻ săn mồi tiếp tục đuổi theo hướng cũ sau khi mất dấu con mồi,
     * trước khi chuyển sang RoamStrategy.
     */
    private static final int PERSISTENCE_TICKS = 40;

    /**
     * Khoảng cách tính bằng block để kẻ săn mồi thực hiện tấn công con mồi.
     */
    private static final double ATTACK_RANGE = 1.5;

    /**
     * Con mồi đang bị nhắm tới trong tick hiện tại.
     * Được set từ PredatorAnimalModel trước khi tick chạy.
     */
    private EntityModel target;

    /**
     * Đếm số tick kể từ lần cuối nhìn thấy con mồi.
     * Reset về 0 mỗi khi con mồi còn trong tầm nhìn.
     */
    private int ticksSinceSeen = 0;

    /**
     * Khởi tạo HuntStrategy với con mồi mục tiêu ban đầu.
     *
     * @param target EntityModel là con mồi đang bị săn
     */
    public HuntStrategy(EntityModel target) {
        this.target = target;
    }

    /**
     * Cập nhật con mồi mục tiêu và reset bộ đếm mất dấu.
     *
     * @param target EntityModel mới là con mồi mục tiêu
     */
    public void setTarget(EntityModel target) {
        this.target = target;
        this.ticksSinceSeen = 0;
    }

    /**
     * Trả về con mồi hiện đang bị nhắm tới.
     *
     * @return EntityModel là con mồi, hoặc null nếu chưa có mục tiêu
     */
    public EntityModel getTarget() {
        return target;
    }

    /**
     * Kiểm tra xem HuntStrategy có đang thực sự đuổi theo con mồi không,
     * hay đã mất dấu quá lâu và sắp hết persistence.
     *
     * @return true nếu vẫn còn trong persistence window
     */
    public boolean isStillTracking() {
        return ticksSinceSeen < PERSISTENCE_TICKS;
    }

    @Override
    public void tick(AnimalModel animal, List<EntityModel> nearbyEntities) {
        Species species = animal.getSpecies();
        if (species == null) return;

        // Con mồi đã chết hoặc bị xóa
        if (target == null || target.getHealth() <= 0) {
            target = null;
            animal.roamRandomly(species.getMinSpeed(), species.getMaxSpeed(), species.getTurnRate());
            return;
        }

        boolean inView = animal.isInFieldOfView(
                target.getPosition(),
                species.getFovRadians(),
                species.getViewDistance()
        );

        if (inView) {
            // Thấy con mồi: reset bộ đếm, tiến về phía con mồi
            ticksSinceSeen = 0;
            double distance = animal.distanceTo(target.getPosition());

            if (distance <= ATTACK_RANGE) {
                // Đủ gần: tấn công nếu con mồi có thể ăn được
                if (target instanceof Edible edible && edible.canBeEaten()) {
                    ((AnimalModel) animal).eat(edible); // cast an toàn vì Edible
                    target.receiveDamage(target.getHealth()); // kill prey
                    target = null;
                }
            } else {
                // Chưa đủ gần: tiếp tục di chuyển về phía con mồi
                animal.setSpeed(species.getMaxSpeed());
                animal.moveToward(target.getPosition(), species.getHuntSpeedMultiplier(), ATTACK_RANGE);
            }
        } else {
            // Mất dấu: tiếp tục đi theo hướng cũ trong PERSISTENCE_TICKS tick
            ticksSinceSeen++;
            if (ticksSinceSeen < PERSISTENCE_TICKS) {
                animal.setSpeed(species.getMaxSpeed() * species.getHuntSpeedMultiplier());
                animal.move();
            } else {
                // Hết persistence: từ bỏ, chuyển về lang thang
                target = null;
                animal.roamRandomly(species.getMinSpeed(), species.getMaxSpeed(), species.getTurnRate());
            }
        }
    }
}