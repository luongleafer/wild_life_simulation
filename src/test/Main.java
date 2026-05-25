package test;

import model.block.BlockModel;
import model.block.FoodBlock;
import model.block.FoodBlockModel;
import model.block.ObstacleBlock;
import model.block.ObstacleBlockModel;
import model.world.WorldModel;
import view.BlockView;

public class Main {
    private static BlockView blockView = new BlockView();

    public static void main(String[] args) {
        // initialize 40x20 matrix
        int width = 40;
        int height = 20;
        WorldModel world = new WorldModel(width, height);

        world.generateTerrain();

        // sample obstacle/food blocks placed on top of terrain
        world.placeObstacle(new ObstacleBlock(2, 2, "rock"));
        world.placeObstacle(new ObstacleBlock(6, 5, "bush"));
        world.placeFood(new FoodBlock(10, 3, "berry", 2));
        world.placeFood(new FoodBlock(12, 6, "berry", 2));

        // console output
        System.out.println("--- Wildlife Simulation Terrain Test ---");
        renderConsole(world, width, height);
    }

    private static void renderConsole(WorldModel world, int width, int height) {
        BlockModel[][] grid = world.getBlocksData();
        ObstacleBlockModel[][] obstacles = world.getObstacleData();
        FoodBlockModel[][] foods = world.getFoodData();

        // iterate thru grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BlockModel block = grid[x][y];
                ObstacleBlockModel obstacle = obstacles[x][y];
                FoodBlockModel food = foods[x][y];

                if (obstacle != null) {
                    System.out.print(blockView.getBlockDisplay(obstacle) + " ");
                } else if (food != null) {
                    System.out.print(blockView.getBlockDisplay(food) + " ");
                } else {
                    // block render
                    System.out.print(blockView.getBlockDisplay(block) + " ");
                }
            }

            System.out.println();
        }
    }
}
