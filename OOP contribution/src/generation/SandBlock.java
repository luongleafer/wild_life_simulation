package generation;
import model.block.BlockModel;
public class SandBlock extends BlockModel{
	public SandBlock(int x, int y, int initialState, int sinkability) {
		super(x,y,initialState);
		this.blockType='sand';
		this.sinkability=4;
		this.totalState=1;
	}

}