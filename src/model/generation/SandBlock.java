package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;

import java.util.List;
import java.util.Random;

public class SandBlock extends BlockModel {

    static {
        BlockFactory.register("sand", SandBlock::new);
    }

    public SandBlock(int x, int y) {
        super(x, y, 4);
        this.blockType = "sand";
        this.sinkability = 4;
    }

    @Override
    public boolean isAffectedByGravity() {
        return true;
    }

    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        long waterBlockCount = surroundingBlocks.stream().filter(blockModel -> blockModel.getBlockType().equals("water")).count();
        if(new Random().nextDouble() < waterBlockCount * 0.005){
            return new WaterBlock(position.x, position.y);
        }
        return this;
    }

}