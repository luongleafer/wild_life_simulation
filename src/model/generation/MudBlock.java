package model.generation;

import model.block.BlockModel;
import model.block.BlockFactory;

import java.util.List;
import java.util.Random;

public class MudBlock extends BlockModel {

    static{
        BlockFactory.register("mud", MudBlock::new);
    }

    private boolean isWet;

    public MudBlock(int x, int y) {
        super(x, y, 4);
        this.blockType = "mud";
        this.sinkability = 4;
        this.isWet = true; // assume it starts as wet
    }

    public boolean isWet() {
        return isWet;
    }

    public void setWet(boolean wet) {
        isWet = wet;
    }

    /**
     * Dries out the mud block, turning it into a dirt block.
     * @return a new DirtBlock at the same position.
     */
    public BlockModel dryOut() {
        return new DirtBlock(this.position.getX(), this.position.getY());
    }


    @Override
    public BlockModel interact(List<BlockModel> surroundingBlocks) {

        long waterBlockCount = surroundingBlocks.stream().filter(blockModel -> blockModel.getBlockType().equals("water")).count();
        if(new Random().nextDouble() < (4 - waterBlockCount) * 0.0125){
            return dryOut();
        }
        else if(new Random().nextDouble() < waterBlockCount * 0.005){
            return new WaterBlock(position.x, position.y);
        }
        return this;
    }
}