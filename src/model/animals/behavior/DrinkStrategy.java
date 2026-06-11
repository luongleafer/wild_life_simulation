package model.animals.behavior;

import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityModel;

import java.util.List;

/**
 * Chiến lược uống nước của động vật.
 *
 * <p>DrinkStrategy khác các strategy khác ở chỗ nguồn nước không phải là
 * {@link EntityModel} mà là {@link BlockModel} kiểu "water" trong thế giới.
 * Vì vậy strategy cần nhận thêm {@code blocksData} từ WorldModel để tìm
 * block nước gần nhất.</p>
 *
 * <p>Strategy được kích hoạt khi thirst của con vật vượt ngưỡng nhất định,
 * và bị ngắt ngay khi con vật đã uống đủ (thirst giảm xuống dưới ngưỡng).</p>
 *
 * @see SimpleWaterSeekStrategy
 */
public interface DrinkStrategy {

    /**
     * Thực thi chiến lược tìm và uống nước trong một tick.
     *
     * @param animal     con vật cần uống nước
     * @param blocksData mảng block 2D từ WorldModel để quét tìm water block
     */
    void tick(AnimalModel animal, BlockModel[][] blocksData);
}