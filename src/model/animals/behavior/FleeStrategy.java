package model.animals.behavior;

import model.animals.species.Species;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

/**
 * Hành vi bỏ chạy khỏi kẻ săn mồi.
 *
 * <p>FleeStrategy được kích hoạt khi con mồi phát hiện kẻ săn mồi trong vùng
 * nguy hiểm. Con vật sẽ di chuyển theo hướng ngược lại với kẻ săn mồi gần
 * nhất, với tốc độ được nhân lên theo {@code fleeSpeedMultiplier} trong Species.</p>
 *
 * <p>Nếu không có kẻ săn mồi nào được truyền vào, strategy sẽ fallback về
 * {@link RoamStrategy} để tránh con vật đứng yên.</p>
 */
public class FleeStrategy implements BehaviorStrategy {

    /**
     * Kẻ săn mồi đang bị con vật né tránh trong tick hiện tại.
     * Được set từ bên ngoài (PreyAnimalModel) trước khi tick chạy.
     */
    private EntityModel threat;

    /**
     * Khởi tạo FleeStrategy với kẻ săn mồi cụ thể cần né tránh.
     *
     * @param threat EntityModel là kẻ săn mồi đang đuổi theo con vật
     */
    public FleeStrategy(EntityModel threat) {
        this.threat = threat;
    }

    /**
     * Cập nhật kẻ săn mồi cần né tránh.
     * Được gọi mỗi tick khi PreyAnimalModel re-evaluate mối nguy hiểm.
     *
     * @param threat EntityModel mới là mối đe dọa
     */
    public void setThreat(EntityModel threat) {
        this.threat = threat;
    }

    @Override
    public void tick(AnimalModel animal, List<EntityModel> nearbyEntities) {
        Species species = animal.getSpecies();
        if (species == null) return;

        if (threat == null) {
            // Không còn mối đe dọa, fallback về lang thang bình thường
            animal.roamRandomly(species.getMinSpeed(), species.getMaxSpeed(), species.getTurnRate());
            return;
        }

        // Tính vector từ kẻ săn mồi đến con vật, tức là hướng bỏ chạy
        EntityCoordinate myPos = animal.getPosition();
        EntityCoordinate threatPos = threat.getPosition();

        double dx = myPos.getPosX() - threatPos.getPosX();
        double dy = myPos.getPosY() - threatPos.getPosY();

        // Nếu trùng vị trí (hiếm) thì chạy ngẫu nhiên
        if (dx == 0.0 && dy == 0.0) {
            animal.randomizeDirection();
        } else {
            animal.setDirection(dx, dy);
        }

        // Di chuyển với tốc độ tối đa nhân hệ số flee
        double fleeSpeed = species.getMaxSpeed() * species.getFleeSpeedMultiplier();
        animal.setSpeed(fleeSpeed);
        animal.move();
    }
}