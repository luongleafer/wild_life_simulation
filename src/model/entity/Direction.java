package model.entity;

public class Direction {
    Direction(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }
    // Coords start from top left corner, not Cartesian
//        public NORTH = new Direction(0, -1);
//        public SOUTH = new Direction(0, 1);
//        public EAST = new Direction(1,0);
//        public WEST = new Direction(-1,0);

    private double dx;
    private double dy;

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setNew(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
