package model.animals.behavior;

import model.animals.species.Species;
import model.entity.AnimalModel;
import model.entity.EntityModel;

import java.util.List;

/**
 * Hành vi di lang thang mặc định khi không có hành vi sinh tồn ưu tiên hơn.
 *
 * <p>RoamStrategy được dùng như fallback cho cả con mồi lẫn kẻ săn mồi khi
 * không có mối đe dọa hay mục tiêu nào trong tầm nhìn. Tốc độ và góc xoay
 * được lấy từ {@link Species} để tránh hard-code trong từng class con vật.</p>
 */
public class RoamStrategy implements BehaviorStrategy {
    @Override
    public void tick(AnimalModel animal, List<EntityModel> nearbyEntities) {
        Species species = animal.getSpecies();
        if (species == null) {
            return;
        }

        animal.roamRandomly(species.getMinSpeed(), species.getMaxSpeed(), species.getTurnRate());
    }
}