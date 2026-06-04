package model.animals.behavior;

import model.animals.species.Species;
import model.entity.AnimalModel;
import model.entity.EntityModel;

import java.util.List;

/**
 * Hành vi bám theo con trưởng thành cùng loài, dùng cho giai đoạn baby.
 *
 * <p>FollowStrategy được kích hoạt khi một con vật ở giai đoạn baby ({@code currentState == 0})
 * và phát hiện có con trưởng thành cùng loài trong vùng lân cận. Con baby sẽ
 * di chuyển về phía con trưởng thành đó, dừng lại khi đã đủ gần theo
 * {@code followDistance} trong Species config.</p>
 *
 * <p>Nếu không tìm được con trưởng thành nào, fallback về {@link RoamStrategy}.</p>
 */
public class FollowStrategy implements BehaviorStrategy {

    /**
     * Con trưởng thành đang được baby bám theo trong tick hiện tại.
     * Được set từ PreyAnimalModel trước khi tick chạy.
     */
    private AnimalModel leader;

    /**
     * Khởi tạo FollowStrategy với con trưởng thành cụ thể cần bám theo.
     *
     * @param leader AnimalModel là con trưởng thành dẫn đầu
     */
    public FollowStrategy(AnimalModel leader) {
        this.leader = leader;
    }

    /**
     * Cập nhật con trưởng thành đang được bám theo.
     * PreyAnimalModel gọi method này mỗi tick để cập nhật leader gần nhất.
     *
     * @param leader AnimalModel mới là con trưởng thành cần theo
     */
    public void setLeader(AnimalModel leader) {
        this.leader = leader;
    }

    /**
     * Trả về con trưởng thành hiện đang được baby bám theo.
     *
     * @return AnimalModel là leader, hoặc null nếu chưa xác định
     */
    public AnimalModel getLeader() {
        return leader;
    }

    @Override
    public void tick(AnimalModel animal, List<EntityModel> nearbyEntities) {
        Species species = animal.getSpecies();
        if (species == null) return;

        if (leader == null || leader.getHealth() <= 0) {
            // Không có leader: fallback về lang thang
            animal.roamRandomly(species.getMinSpeed(), species.getMaxSpeed(), species.getTurnRate());
            return;
        }

        double distance = animal.distanceTo(leader.getPosition());

        if (distance > species.getFollowDistance()) {
            // Còn xa: di chuyển về phía leader, dừng khi vào đúng followDistance
            animal.setSpeed(species.getMaxSpeed());
            animal.moveToward(leader.getPosition(), 1.0, species.getFollowDistance());
        }
        // Đủ gần rồi: không di chuyển thêm, đứng yên cạnh leader
    }
}