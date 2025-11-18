package org.openxava.actions;

/**
 * Releases list format for list tabs.
 * 
 * @since 7.6.3
 * @author Javier Paniza
 */
public class ReleaseListFormatAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().setOptimizeChunkSize(false);
	}
}
