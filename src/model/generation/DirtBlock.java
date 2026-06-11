package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;

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
        long waterBlockCount = surroundingBlocks.stream().filter(blockModel -> blockModel.getBlockType().equals("water")).count();
        if(new Random().nextDouble() < waterBlockCount * 0.001){
            return new MudBlock(position.x, position.y);
        }
        long grassBlocks = surroundingBlocks.stream().filter(blockModel -> blockModel.getBlockType().equals("grass")).count();
        if(new Random().nextDouble() < grassBlocks * 0.01){
            return new GrassBlock(position.x,position.y);
        }
        return this;
    }
}