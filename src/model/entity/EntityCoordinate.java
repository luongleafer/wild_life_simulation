package model.entity;

public class EntityCoordinate {
    public double posX;
    public double posY;

    public EntityCoordinate(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public EntityCoordinate(EntityCoordinate other) {
        this.posX = other.posX;
        this.posY = other.posY;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double distance(EntityCoordinate target) {
        return Math.sqrt(
                (target.posX - posX) * (target.posX - posX) + (target.posY - posY) * (target.posY - posY)
        );
    }
}
