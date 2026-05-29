package view.entity;

import javafx.scene.image.Image;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.nio.file.Path;

/**
 * Quan ly thong tin hien thi cua mot {@link EntityModel}.
 *
 * <p>Lop nay khong thay doi logic song/hanh vi cua entity trong model. No chi
 * giu cac du lieu ma phan view can dung de ve entity len man hinh, vi du:
 * duong dan sprite, ky tu console, kich thuoc render, toa do tren man hinh va
 * trang thai an/hien.</p>
 */
public class EntityView {
    public static final int TILE_SIZE = 16;
    // Entity goc trong model. Renderer doc vi tri va state tu doi tuong nay.
    private final EntityModel entity;

    // Ten loai entity, mac dinh lay theo ten class de view co the phan loai.
    private String entityType;

    // Duong dan toi anh/sprite dung khi ve entity bang giao dien do hoa.
    private Path spritePath;

    private Image sprite;

    // Ky tu dai dien cho entity khi hien thi o che do console/text.
    private char consoleSymbol;

    // Toa do X/Y tren man hinh sau khi quy doi tu toa do trong world.
    private int screenX;
    private int screenY;

    // Kich thuoc ve entity tren man hinh, tinh theo pixel.
    private int width;
    private int height;

    // Cho biet renderer co nen ve entity hay bo qua entity nay.
    private boolean visible;

    /**
     * Tao renderer cho mot entity cu the.
     *
     * @param entity entity trong model can hien thi
     * @param sprite duong dan sprite mac dinh cua entity
     * @param consoleSymbol ky tu dai dien khi hien thi bang console
     * @param width chieu rong khi render, tinh theo pixel
     * @param height chieu cao khi render, tinh theo pixel
     */
    public EntityView(EntityModel entity, Image sprite, char consoleSymbol, int width, int height) {
        this.entity = entity;
        this.entityType = entity.getClass().getSimpleName();
//        this.spritePath = spritePath;
        this.consoleSymbol = consoleSymbol;
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        this.visible = true;
        updateScreenPosition(TILE_SIZE, 0, 0);
    }

    /**
     * Cap nhat toa do ve entity tren man hinh dua tren vi tri trong world.
     *
     * <p>Entity luu vi tri theo don vi o/tile trong {@link EntityCoordinate}.
     * Ham nay nhan kich thuoc moi tile de doi sang pixel, sau do tru toa do
     * camera de tinh vi tri thuc te trong viewport:</p>
     *
     * <pre>
     * screenX = entity.posX * tileSize - cameraX
     * screenY = entity.posY * tileSize - cameraY
     * </pre>
     *
     * @param tileSize kich thuoc mot tile tren man hinh, tinh theo pixel
     * @param cameraX do lech camera theo truc X, tinh theo pixel
     * @param cameraY do lech camera theo truc Y, tinh theo pixel
     */
    public void updateScreenPosition(int tileSize, int cameraX, int cameraY) {
        EntityCoordinate position = entity.getPosition();
        this.screenX = (int) (position.posX * tileSize) - cameraX;
        this.screenY = (int) (position.posY * tileSize) - cameraY;
    }

    /**
     * Lay entity goc ma renderer dang dai dien.
     *
     * @return entity trong model
     */
    public EntityModel getEntity() {
        return entity;
    }

    /**
     * Lay ten loai entity dang duoc dung cho viec phan loai/hien thi.
     *
     * @return ten loai entity
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Doi ten loai entity neu view can dung ten hien thi khac ten class.
     *
     * @param entityType ten loai entity moi
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * Lay duong dan sprite hien tai.
     *
     * @return duong dan den file sprite
     */
    public Path getSpritePath() {
        return spritePath;
    }

    /**
     * Doi sprite khi entity thay doi hinh dang, trang thai hoac giai doan.
     *
     * @param spritePath duong dan sprite moi
     */
    public void setSpritePath(Path spritePath) {
        this.spritePath = spritePath;
    }

    /**
     * Lay ky tu dai dien cho entity trong che do console/text.
     *
     * @return ky tu console hien tai
     */
    public char getConsoleSymbol() {
        return consoleSymbol;
    }

    /**
     * Doi ky tu dai dien cho entity trong che do console/text.
     *
     * @param consoleSymbol ky tu dai dien moi
     */
    public void setConsoleSymbol(char consoleSymbol) {
        this.consoleSymbol = consoleSymbol;
    }

    /**
     * Lay toa do X tren man hinh sau lan cap nhat gan nhat.
     *
     * @return toa do X tinh theo pixel
     */
    public int getScreenX() {
        return screenX;
    }

    /**
     * Lay toa do Y tren man hinh sau lan cap nhat gan nhat.
     *
     * @return toa do Y tinh theo pixel
     */
    public int getScreenY() {
        return screenY;
    }

    /**
     * Lay chieu rong render cua entity.
     *
     * @return chieu rong tinh theo pixel
     */
    public int getWidth() {
        return width;
    }

    /**
     * Doi chieu rong render cua entity.
     *
     * @param width chieu rong moi tinh theo pixel
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Lay chieu cao render cua entity.
     *
     * @return chieu cao tinh theo pixel
     */
    public int getHeight() {
        return height;
    }

    /**
     * Doi chieu cao render cua entity.
     *
     * @param height chieu cao moi tinh theo pixel
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Kiem tra entity co dang duoc phep hien thi hay khong.
     *
     * @return true neu renderer nen ve entity, false neu can an entity
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Bat/tat viec hien thi entity.
     *
     * @param visible true de hien entity, false de an entity
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Lay state hien tai tu entity goc.
     *
     * <p>Renderer khong luu ban sao cua state. Moi lan goi ham nay, gia tri
     * duoc doc truc tiep tu {@link EntityModel} de view luon thay trang thai
     * moi nhat cua entity.</p>
     *
     * @return state hien tai cua entity
     */
    public int getCurrentState() {
        return entity.getCurrentState();
    }

    public Image getSprite() {
        return sprite;
    }
}
