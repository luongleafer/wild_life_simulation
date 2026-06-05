package model.block;

public abstract class FoodBlockModel extends BlockModel {
    private int nutrition;

    // food blocks can be passed through and is consumable.
    public FoodBlockModel(int x, int y,int sinkability, int nutrition) {
        super(x, y, sinkability);
        this.nutrition = nutrition;
    }

    public int getNutrition() {
        return nutrition;
    }

    public boolean blocksMovement() {
        return false;
    }
}

