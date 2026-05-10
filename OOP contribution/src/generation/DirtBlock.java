package generation;
import model.block.BlockModel;
public class DirtBlock extends BlockModel{
	public DirtBlock(int x, int y, int initialState, int sinkability) {
		super(x,y,initialState);
		this.blockType='sand';
		this.sinkability=2;
		this.totalState=1;
	}

}