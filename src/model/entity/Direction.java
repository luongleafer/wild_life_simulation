package model.entity;

public class Direction {
    // Coords start from top left corner, not Cartesian
    static public Direction NORTH(){ return new Direction(0, -1);}
    static public Direction SOUTH() { return new Direction(0, 1); }
    static public Direction EAST() { return new Direction(1, 0); }
    static public Direction WEST() {return new Direction(-1, 0);}

    private double dx;
    private double dy;

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

    public void setDirection(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
