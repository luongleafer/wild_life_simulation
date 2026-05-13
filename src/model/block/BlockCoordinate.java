package model.block;

import java.util.Objects;

public class BlockCoordinate {
    private int x;
    private int y;

    public BlockCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters and Setters
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    } 
    // Thêm setter nếu cần thay đổi tọa độ

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";       
    }

    @Override
    // Thêm cái này để bảo vệ hàm equals 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockCoordinate)) return false;
        BlockCoordinate that = (BlockCoordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        // Tạo mã định danh duy nhất dựa trên cặp x và y
        return Objects.hash(x, y);
    }
}
