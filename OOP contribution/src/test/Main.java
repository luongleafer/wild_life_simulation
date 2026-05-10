package test;

import model.block.BlockModel;
import model.world.WorldModel;

public class Main {
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

                // in case grid wasn't fully populated
                if (block == null) {
                    System.out.print("? ");
                    continue;
                }

                // map block type string to a specific console character
                String type = block.getBlockType();
                char symbol = ' ';

                switch (type) {
                    case "water":
                        symbol = '~'; // blue water
                        break;
                    case "dirt":
                        symbol = '.'; // plain dirt
                        break;
                    case "grass":
                        symbol = ','; // Scattered grass
                        break;
                    case "wood":
                        symbol = 'T'; // tree/forest Blobs
                        break;
                    default:
                        symbol = '?'; // unknown block for later testing
                }
                System.out.print(symbol + " ");
            }

            System.out.println();
        }
    }
}
