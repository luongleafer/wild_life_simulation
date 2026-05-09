package test;

import model.block.BlockModel;
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

        // console output
        System.out.println("--- Wildlife Simulation Terrain Test ---");
        renderConsole(world, width, height);
    }

    private static void renderConsole(WorldModel world, int width, int height) {
        BlockModel[][] grid = world.getBlocksData();

        // iterate thru grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BlockModel block = grid[x][y];
                // block render
                System.out.print(blockView.getBlockDisplay(block) + " ");
            }

            System.out.println();
        }
    }
}
