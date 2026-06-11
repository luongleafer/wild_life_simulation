package model.block;

import java.util.List;

public class FoodBlock extends FoodBlockModel {
    public FoodBlock(int x, int y, String blockType, int nutrition) {
        super(x, y , 0, nutrition);
        this.blockType = blockType;
    }

    public BlockModel newBlock(int x, int y) {
        return new FoodBlock(x, y, blockType, getNutrition());
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        return this;
    }
}

