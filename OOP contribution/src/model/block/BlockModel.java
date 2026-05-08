package model.block;

public abstract class BlockModel {
	protected int totalState;
	protected int currentState;// tình trạng hiện tại của block(selected, placed,..)
	protected String blockType; //các loại của block (grass, dirt,...)
	protected int sinkability;//tính lún của block (càng gần 0 càng cứng)
	public int getCurrentState() {
		return currentState;
	}
	public BlockModel(int x, int y, int initialState, int sinkability ) {
		
		this.position = new BlockCoordinate(x,y);
		this.currentState = initialState;
		this.sinkability=sinkability;
	}
	public void setState(int newState) {
		this.curentSate=newState;
	}
	public BlockCoordinate getPosition() {
		return position;
	}
	public String getBlocktype() {
		return blockType;
	}
}
