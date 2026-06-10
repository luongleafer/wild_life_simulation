package model.animals.behavior;

import model.animals.species.Species;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;

/**
 * Chiến lược tìm và uống nước đơn giản.
 *
 * <p>Nguồn nước duy nhất trong simulation là WaterBlock ({@code blockType = "water"})
 * nằm trong {@code blocksData[][]} của WorldModel. Strategy sẽ:</p>
 * <ol>
 *   <li>Quét {@code blocksData} trong phạm vi {@code SEARCH_RADIUS} block
 *       quanh vị trí con vật để tìm water block gần nhất.</li>
 *   <li>Lưu tọa độ water block đó vào {@code targetWaterPos}.</li>
 *   <li>Di chuyển về phía water block.</li>
 *   <li>Khi đủ gần ({@code DRINK_DISTANCE}), gọi {@code animal.drink()} để
 *       giảm thirst và tăng energy.</li>
 *   <li>Sau khi uống xong (thirst &lt;= {@code SATISFIED_THIRST}), xóa target
 *       và kết thúc strategy.</li>
 * </ol>
 *
 * <p><strong>Lưu ý về Drinkable:</strong> {@code Drinkable} interface trong codebase
 * hiện tại không phải là interface cho block nước — nó mô tả entity có thể
 * cung cấp nước uống (chỉ số thirst/energy). WaterBlock chưa implement
 * Drinkable, nên strategy này tự giảm thirst trực tiếp thay vì gọi
 * {@code animal.drink(Drinkable)}.</p>
 *
 * <p>Nếu sau này WaterBlock implement Drinkable, chỉ cần đổi đoạn
 * applyDrinkEffect() để gọi {@code animal.drink(waterBlock)}.</p>
 *
 * @see DrinkStrategy
 */
public class SimpleWaterSeekStrategy implements DrinkStrategy {

    /**
     * Bán kính quét tìm water block quanh con vật, tính theo block.
     * Mặc định 15 — con vật có thể "ngửi" thấy nước từ 15 block.
     */
    private static final int SEARCH_RADIUS = 15;

    /**
     * Khoảng cách tính bằng block để con vật bắt đầu uống.
     * Mặc định 1.2 — phải đứng rất gần block nước mới uống được.
     */
    private static final double DRINK_DISTANCE = 1.2;

    /**
     * Hệ số nhân tốc độ khi đang tìm đường đến nước.
     * Mặc định 1.1 — đi hơi nhanh hơn bình thường nhưng không chạy.
     */
    private static final double WATER_SEEK_SPEED = 1.1;

    /**
     * Lượng thirst giảm mỗi tick khi đang uống.
     * Mặc định 2.0f — uống khoảng 5 tick để no.
     */
    private static final float THIRST_REDUCE_PER_TICK = 2.0f;

    /**
     * Lượng energy tăng mỗi tick khi đang uống.
     * Mặc định 0.5f — uống nước cũng hồi phục một chút năng lượng.
     */
    private static final float ENERGY_RESTORE_PER_TICK = 0.5f;

    /**
     * Ngưỡng thirst để coi là đã uống đủ và dừng strategy.
     * Mặc định 2.0f — đã no nước, không cần tìm nữa.
     */
    private static final float SATISFIED_THIRST = 90.0f; // day 90 % nuoc thi ko tim kiem nuoc

    /**
     * Tọa độ world (block coordinate) của water block đang được nhắm tới.
     * null nếu chưa tìm được hoặc đã uống xong.
     */
    private EntityCoordinate targetWaterPos = null;

    /**
     * Số tick đã đi tìm nước hiện tại.
     * Nếu vượt {@code GIVE_UP_TICKS} mà chưa đến nơi → bỏ cuộc, tìm nước khác.
     */
    private int seekingTimer = 0;

    /**
     * Số tick tối đa tìm một water block trước khi từ bỏ và quét lại.
     * Tránh con vật bị kẹt đi mãi về một điểm không đến được.
     */
    private static final int GIVE_UP_TICKS = 80;

    @Override
    public void tick(AnimalModel animal, BlockModel[][] blocksData) {
        // Đã uống đủ rồi → dừng
        if (animal.getThirst() <= SATISFIED_THIRST) {
            targetWaterPos = null;
            seekingTimer = 0;
            return;
        }

        // Nếu chưa có target hoặc đã tìm quá lâu → quét lại
        if (targetWaterPos == null || seekingTimer >= GIVE_UP_TICKS) {
            targetWaterPos = findNearestWaterBlock(animal.getPosition(), blocksData);
            seekingTimer = 0;

            if (targetWaterPos == null) {
                // Không tìm được nước trong SEARCH_RADIUS → lang thang chờ
                roamTowardWater(animal);
                return;
            }
        }

        double distance = animal.distanceTo(targetWaterPos);

        if (distance <= DRINK_DISTANCE) {
            // Đủ gần → uống nước trực tiếp
            applyDrinkEffect(animal);
            seekingTimer = 0;

            // Đã uống đủ → reset target
            if (animal.getThirst() <= SATISFIED_THIRST) {
                targetWaterPos = null;
            }
        } else {
            // Chưa đến nơi → tiếp tục di chuyển về phía water block
            seekingTimer++;
            Species species = animal.getSpecies();
            if (species != null) {
                animal.setSpeed(species.getMaxSpeed() * WATER_SEEK_SPEED);
            }
            animal.moveToward(targetWaterPos, 1.0, DRINK_DISTANCE);
        }
    }

    /**
     * Quét {@code blocksData} trong SEARCH_RADIUS quanh con vật để tìm
     * water block gần nhất.
     *
     * <p>Đây là cách duy nhất đúng để tìm nước vì WaterBlock là BlockModel
     * trong world grid, không phải EntityModel trong entity list.</p>
     *
     * @param animalPos  vị trí hiện tại của con vật (đơn vị block)
     * @param blocksData mảng block 2D từ WorldModel
     * @return EntityCoordinate tọa độ water block gần nhất,
     *         hoặc null nếu không tìm được trong SEARCH_RADIUS
     */
    private EntityCoordinate findNearestWaterBlock(EntityCoordinate animalPos, BlockModel[][] blocksData) {
        if (blocksData == null) return null;

        int worldWidth = blocksData.length;
        int worldHeight = blocksData[0].length;

        // Tính vùng quét giới hạn theo SEARCH_RADIUS, clamp theo biên world
        int startX = Math.max(0, (int)(animalPos.getPosX() - SEARCH_RADIUS));
        int endX   = Math.min(worldWidth - 1, (int)(animalPos.getPosX() + SEARCH_RADIUS));
        int startY = Math.max(0, (int)(animalPos.getPosY() - SEARCH_RADIUS));
        int endY   = Math.min(worldHeight - 1, (int)(animalPos.getPosY() + SEARCH_RADIUS));

        EntityCoordinate nearest = null;
        double minDist = Double.MAX_VALUE;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                BlockModel block = blocksData[x][y];
                if (block == null) continue;
                if (!"water".equals(block.getBlockType())) continue;

                // Tạo tọa độ tâm của block (x + 0.5, y + 0.5) để di chuyển chính xác
                EntityCoordinate blockCenter = new EntityCoordinate(x + 0.5, y + 0.5);
                double dist = animalPos.distance(blockCenter);

                if (dist < minDist) {
                    minDist = dist;
                    nearest = blockCenter;
                }
            }
        }
        return nearest;
    }

    /**
     * Áp dụng hiệu ứng uống nước trực tiếp lên con vật.
     *
     * <p>Giảm thirst và tăng energy mỗi tick khi đứng cạnh water block.
     * Nếu WaterBlock sau này implement Drinkable, đổi sang
     * {@code animal.drink(waterBlock)} để nhất quán.</p>
     *
     * @param animal con vật đang uống nước
     */
    private void applyDrinkEffect(AnimalModel animal) {
        animal.setThirst(animal.getThirst() + THIRST_REDUCE_PER_TICK);
        animal.setEnergy(animal.getEnergy() + ENERGY_RESTORE_PER_TICK);
    }

    /**
     * Đi lang thang khi không tìm được nước trong vùng quét.
     * Tốc độ giảm một chút — con vật mệt mỏi vì khát.
     *
     * @param animal con vật cần di chuyển
     */
    private void roamTowardWater(AnimalModel animal) {
        Species species = animal.getSpecies();
        if (species == null) return;
        // Di chuyển chậm hơn khi khát để tiết kiệm năng lượng
        animal.roamRandomly(
                species.getMinSpeed() * 0.6,
                species.getMaxSpeed() * 0.6,
                species.getTurnRate()
        );
    }

    /**
     * Trả về tọa độ water block đang được nhắm tới.
     * null nếu chưa tìm được target.
     *
     * @return EntityCoordinate là tọa độ water block, hoặc null
     */
    public EntityCoordinate getTargetWaterPos() {
        return targetWaterPos;
    }

    /**
     * Kiểm tra con vật có đang trên đường tìm nước không.
     *
     * @return true nếu đã có water block target
     */
    public boolean isSeeking() {
        return targetWaterPos != null;
    }
}