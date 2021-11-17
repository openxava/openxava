package org.openxava.actions;

/**
 * 
 * @since 6.6.1
 * @author Javier Paniza
 */
public class SetOptimizeChunkSizeAction extends TabBaseAction {
	
	private boolean optimizeChunkSize;
	
	public void execute() throws Exception {
		getTab().setOptimizeChunkSize(optimizeChunkSize);
	}


	public boolean isOptimizeChunkSize() {
		return optimizeChunkSize;
	}


	public void setOptimizeChunkSize(boolean optimizeChunkSize) {
		this.optimizeChunkSize = optimizeChunkSize;
	}

}
