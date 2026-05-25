package model.entity;

public enum Direction {
    // Coords start from top left corner, not Cartesian
    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(1, 0),
    WEST(-1, 0);

    private final double dx;
    private final double dy;

    Direction(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }
}
