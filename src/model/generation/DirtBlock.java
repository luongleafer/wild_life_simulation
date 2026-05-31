package model.generation;

import model.block.BlockModel;

import java.util.List;
import java.util.Random;

public class DirtBlock extends BlockModel {

    public DirtBlock(int x, int y, int initialState) {
        super(x, y, initialState, 2);
        this.blockType = "dirt";
        this.totalStates = 1;
        this.sinkability = 2;
    }


    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {
        long waterBlockCount = surroundingBlocks.stream().filter(blockModel -> blockModel.getBlockType().equals("water")).count();
        if(new Random().nextDouble() < waterBlockCount * 0.001){
            return new MudBlock(position.x, position.y, 0);
        }
        long grassBlocks = surroundingBlocks.stream().filter(blockModel -> blockModel.getBlockType().equals("grass")).count();
        if(new Random().nextDouble() < grassBlocks * 0.005){
            return new GrassBlock(position.x,position.y,0);
        }
        return this;
    }
}