package model.animals.behavior;

import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.Edible;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

public class SimpleEatStrategy implements EatStrategy {

    // Khoảng cách đủ gần để ăn
    private static final double EAT_DISTANCE = 1.2;

    // Ngưỡng đói tối thiểu để ngừng tìm thức ăn
    private static final float SATISFIED_HUNGER = 2f;

    // Bán kính tìm kiếm thức ăn
    private static final int SEARCH_RADIUS = 15;

    // Vị trí thức ăn đang nhắm tới
    private EntityCoordinate targetFoodPos;

    @Override
    public void tick(
            AnimalModel animal,
            List<EntityModel> nearbyEntities,
            BlockModel[][] blocksData
    ) {

        // Đã no thì không cần tìm thức ăn
        if (animal.getHunger() <= SATISFIED_HUNGER) {
            targetFoodPos = null;
            return;
        }

        // Tìm Entity ăn được gần nhất
        EntityModel foodEntity = findNearestEdibleEntity(animal, nearbyEntities);

        if (foodEntity != null) {

            double distance = animal.getPosition().distance(foodEntity.getPosition());

            // Đủ gần thì ăn
            if (distance <= EAT_DISTANCE) {
                eatEntity(animal, foodEntity);
            } else {
                // Di chuyển tới mục tiêu
                animal.moveToward(foodEntity.getPosition(), 1.0, EAT_DISTANCE);
            }

            return;
        }

        // Nếu không có Entity thì tìm Block thức ăn
        EntityCoordinate blockFood = findNearestEdibleBlock(animal, blocksData);

        // Không tìm thấy thức ăn
        if (blockFood == null) {
            return;
        }

        double distance = animal.getPosition().distance(blockFood);

        // Đủ gần thì ăn
        if (distance <= EAT_DISTANCE) {
            eatBlock(animal);
        } else {
            // Di chuyển tới Block thức ăn
            animal.moveToward(blockFood, 1.0, EAT_DISTANCE);
        }
    }

    // Tìm Entity ăn được gần nhất
    private EntityModel findNearestEdibleEntity(
            AnimalModel animal,
            List<EntityModel> nearbyEntities
    ) {

        EntityModel nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (EntityModel entity : nearbyEntities) {

            // Bỏ qua đối tượng không ăn được
            if (!(entity instanceof Edible edible)) {
                continue;
            }

            // Bỏ qua đối tượng không thể bị ăn
            if (!edible.canBeEaten()) {
                continue;
            }

            double distance = animal.getPosition().distance(entity.getPosition());

            // Cập nhật mục tiêu gần nhất
            if (distance < minDistance) {
                minDistance = distance;
                nearest = entity;
            }
        }

        return nearest;
    }

    // Tìm Block ăn được gần nhất
    private EntityCoordinate findNearestEdibleBlock(
            AnimalModel animal,
            BlockModel[][] blocksData
    ) {

        // Không có dữ liệu bản đồ
        if (blocksData == null) {
            return null;
        }

        EntityCoordinate animalPos = animal.getPosition();

        EntityCoordinate nearest = null;
        double minDistance = Double.MAX_VALUE;

        // Giới hạn vùng tìm kiếm theo bán kính
        int startX = Math.max(0, (int) animalPos.getPosX() - SEARCH_RADIUS);
        int endX = Math.min(blocksData.length - 1, (int) animalPos.getPosX() + SEARCH_RADIUS);
        int startY = Math.max(0, (int) animalPos.getPosY() - SEARCH_RADIUS);
        int endY = Math.min(blocksData[0].length - 1, (int) animalPos.getPosY() + SEARCH_RADIUS);

        // Quét toàn bộ khu vực tìm kiếm
        for (int x = startX; x <= endX; x++) {

            for (int y = startY; y <= endY; y++) {

                BlockModel block = blocksData[x][y];

                // Bỏ qua block không ăn được
                if (!(block instanceof Edible edible)) {
                    continue;
                }

                // Bỏ qua block không thể bị ăn
                if (!edible.canBeEaten()) {
                    continue;
                }

                EntityCoordinate blockPos = new EntityCoordinate(x + 0.5, y + 0.5);

                double distance = animalPos.distance(blockPos);

                // Cập nhật block gần nhất
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = blockPos;
                }
            }
        }

        return nearest;
    }

    // Ăn một Entity
    private void eatEntity(
            AnimalModel animal,
            EntityModel target
    ) {

        Edible edible = (Edible) target;

        // Giảm mức đói
        animal.setHunger(Math.max(0, animal.getHunger() - edible.getHungerValue()));

        // Tăng năng lượng
        animal.setEnergy(animal.getEnergy() + edible.getEnergyValue());

        // Tiêu diệt mục tiêu sau khi ăn
        target.setHealth(0);
    }

    // Ăn một Block thức ăn
    private void eatBlock(AnimalModel animal) {

        // Giảm mức đói
        animal.setHunger(Math.max(0, animal.getHunger() - 5));

        // Tăng năng lượng
        animal.setEnergy(animal.getEnergy() + 2);
    }
}