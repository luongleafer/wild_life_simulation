package model.block;

public class FoodBlock extends FoodBlockModel {
    public FoodBlock(int x, int y, String blockType, int nutrition) {
        super(x, y, 1, 0, nutrition);
        this.blockType = blockType;
        this.totalStates = 1;
    }

    @Override
    public BlockModel newBlock(int x, int y, int initialState) {
        return new FoodBlock(x, y, blockType, getNutrition());
    }
}

