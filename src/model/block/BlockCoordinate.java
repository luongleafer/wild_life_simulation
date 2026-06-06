package model.block;

import model.Vector2;

public class BlockCoordinate extends Vector2 {

    public BlockCoordinate(double x, double y) {
        super(x, y);
        setX(x);
        setY(y);
    }

    @Override
    public void setX(double x) {
        super.setX(Math.floor(x));
    }

    @Override
    public void setY(double y) {
        super.setY(Math.floor(y));
    }
}
