package view.entity;

import model.entity.EntityCoordinate;
import model.entity.EntityModel;

public class EntityRenderer {
    private final EntityModel entity;
    private String entityType;
    private String spritePath;
    private char consoleSymbol;
    private int screenX;
    private int screenY;
    private int width;
    private int height;
    private boolean visible;

    public EntityRenderer(EntityModel entity, String spritePath, char consoleSymbol, int width, int height) {
        this.entity = entity;
        this.entityType = entity.getClass().getSimpleName();
        this.spritePath = spritePath;
        this.consoleSymbol = consoleSymbol;
        this.width = width;
        this.height = height;
        this.visible = true;
        updateScreenPosition(1, 0, 0);
    }

    public void updateScreenPosition(int tileSize, int cameraX, int cameraY) {
        EntityCoordinate position = entity.getPosition();
        this.screenX = (int) (position.posX * tileSize) - cameraX;
        this.screenY = (int) (position.posY * tileSize) - cameraY;
    }

    public EntityModel getEntity() {
        return entity;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
    }

    public char getConsoleSymbol() {
        return consoleSymbol;
    }

    public void setConsoleSymbol(char consoleSymbol) {
        this.consoleSymbol = consoleSymbol;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getCurrentState() {
        return entity.getCurrentState();
    }
}
