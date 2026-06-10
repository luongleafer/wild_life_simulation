package model.block;

@FunctionalInterface
public interface BlockCreation {
    BlockModel create(int x, int y, int initialState);
}
