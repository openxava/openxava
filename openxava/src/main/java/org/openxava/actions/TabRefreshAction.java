package org.openxava.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TabRefreshAction extends ViewBaseAction implements ICustomViewAction {

    String tabObject;

    String customView;

    @Override
    public void execute() throws Exception {
        org.openxava.tab.Tab tab;
        try {
            tab = (org.openxava.tab.Tab) getContext().get(getRequest(), tabObject);
        } catch (Exception e) {
            String [] id = tabObject.split("_+");
            String application = id[1];
            String module = id[2];
            StringBuffer collectionSB = new StringBuffer();
            for (int i=3; i<id.length; i++) { // To work with collections inside @AsEmbedded references
                if (i > 3) collectionSB.append("_");
                collectionSB.append(id[i]);
            }
            String collection = collectionSB.toString();
            String tabObject = "list".equals(collection)?"xava_tab":"xava_collectionTab_" + collection;
            tab = (org.openxava.tab.Tab) getContext().get(application, module, tabObject);
        }
        if (tab.getCollectionView() != null) {
            tab.getCollectionView().setMustRefreshCollection(true);
        }else {
            customView = ICustomViewAction.DEFAULT_VIEW;
        }
    }

    @Override
    public String getCustomView() throws Exception {
        return customView;
    }
}
