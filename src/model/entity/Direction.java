package model.entity;

public class Direction {

    public static Direction NORTH(){
        return new Direction(0,-1);
    }
    public static Direction EAST() { return new Direction(1,0); }
    public static Direction SOUTH() { return new Direction(0,1); }
    public static Direction WEST() { return new Direction(-1,0); }
    public static Direction STAY() { return new Direction(0,0); }

    Direction(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction(Direction direction) {
        this.dx = direction.dx;
        this.dy = direction.dy;
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
