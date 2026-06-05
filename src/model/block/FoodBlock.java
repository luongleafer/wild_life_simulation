package model.block;

public class FoodBlock extends FoodBlockModel {
    public FoodBlock(int x, int y, String blockType, int nutrition) {
        super(x, y , 0, nutrition);
        this.blockType = blockType;
    }

    public BlockModel newBlock(int x, int y) {
        return new FoodBlock(x, y, blockType, getNutrition());
    }
}

