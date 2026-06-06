package model;

public class Vector2 {
    private double x;
    private double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x;}
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public Vector2 normalize(){
        double length = Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
        return this;
    }

    public Vector2 add(Vector2 other){
        x += other.x;
        y += other.y;
        return this;
    }

    public Vector2 multiply(double scalar){
        x *= scalar;
        y *= scalar;
        return this;
    }

    public double distance(Vector2 other){
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
}
