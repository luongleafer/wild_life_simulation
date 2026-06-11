package model.generation;

import model.block.BlockModel;

import java.util.List;
import java.util.Random;

public class DirtBlock extends BlockModel {

    static {
    }
    public DirtBlock(int x, int y) {
        super(x, y, 2);
        this.blockType = "dirt";
        this.sinkability = 2;
    }


    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        long grassBlocks = surroundingBlocks.stream().filter(blockModel -> blockModel.getBlockType().equals("grass")).count();
        double regrowChance = Math.min(0.9, 0.003 + grassBlocks * 0.08);
        if(new Random().nextDouble() < regrowChance){
            return new GrassBlock(position.x,position.y);
        }
        return this;
    }
}
