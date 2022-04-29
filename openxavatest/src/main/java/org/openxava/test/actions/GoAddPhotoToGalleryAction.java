package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoAddPhotoToGalleryAction extends BaseAction implements ILoadFileAction {  

    public void execute() throws Exception {
    }
 
    public String[] getNextControllers() {
        return new String [] { "LoadPhotoIntoGallery" };
    }
 
    public String getCustomView() {
        return "xava/editors/chooseFile"; 
    }
 
    public boolean isLoadFile() {
        return true;
    }
 
}
