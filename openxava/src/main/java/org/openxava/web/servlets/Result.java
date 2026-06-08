package org.openxava.web.servlets;

import java.io.Serializable;
import java.util.Map;

/**
 * Result bean used to return AJAX response data, now part of servlets.
 * 
 * @since 8.0
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    private String error;
    private boolean reload;
    private String forwardURL;
    private boolean forwardInNewWindow;
    private String[] forwardURLs;
    private String nextModule;
    private String application;
    private String module;
    private boolean showDialog;
    private boolean hideDialog;
    private int dialogLevel;
    private String dialogTitle;
    private boolean resizeDialog;
    private String focusPropertyId;
    private String viewMember;
    private int currentRow;
    private Map<String, int[]> selectedRows;
    private String urlParam;
    private boolean viewSimple;
    private boolean dataChanged;
    private boolean hasPostJS;
    private String[] editorsWithError;
    private String[] editorsWithoutError;
    private String[] propertiesUsedInCalculations;
    private Map<String, StrokeAction> strokeActions;
    private Map<String, Object> changedParts;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public String getForwardURL() {
        return forwardURL;
    }

    public void setForwardURL(String forwardURL) {
        this.forwardURL = forwardURL;
    }

    public boolean isForwardInNewWindow() {
        return forwardInNewWindow;
    }

    public void setForwardInNewWindow(boolean forwardInNewWindow) {
        this.forwardInNewWindow = forwardInNewWindow;
    }

    public String[] getForwardURLs() {
        return forwardURLs;
    }

    public void setForwardURLs(String[] forwardURLs) {
        this.forwardURLs = forwardURLs;
    }

    public String getNextModule() {
        return nextModule;
    }

    public void setNextModule(String nextModule) {
        this.nextModule = nextModule;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public boolean isHideDialog() {
        return hideDialog;
    }

    public void setHideDialog(boolean hideDialog) {
        this.hideDialog = hideDialog;
    }

    public int getDialogLevel() {
        return dialogLevel;
    }

    public void setDialogLevel(int dialogLevel) {
        this.dialogLevel = dialogLevel;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public boolean isResizeDialog() {
        return resizeDialog;
    }

    public void setResizeDialog(boolean resizeDialog) {
        this.resizeDialog = resizeDialog;
    }

    public String getFocusPropertyId() {
        return focusPropertyId;
    }

    public void setFocusPropertyId(String focusPropertyId) {
        this.focusPropertyId = focusPropertyId;
    }

    public String getViewMember() {
        return viewMember;
    }

    public void setViewMember(String viewMember) {
        this.viewMember = viewMember;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public Map<String, int[]> getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(Map<String, int[]> selectedRows) {
        this.selectedRows = selectedRows;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public boolean isViewSimple() {
        return viewSimple;
    }

    public void setViewSimple(boolean viewSimple) {
        this.viewSimple = viewSimple;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    public boolean isHasPostJS() {
        return hasPostJS;
    }

    public void setHasPostJS(boolean hasPostJS) {
        this.hasPostJS = hasPostJS;
    }

    public String[] getEditorsWithError() {
        return editorsWithError;
    }

    public void setEditorsWithError(String[] editorsWithError) {
        this.editorsWithError = editorsWithError;
    }

    public String[] getEditorsWithoutError() {
        return editorsWithoutError;
    }

    public void setEditorsWithoutError(String[] editorsWithoutError) {
        this.editorsWithoutError = editorsWithoutError;
    }

    public String[] getPropertiesUsedInCalculations() {
        return propertiesUsedInCalculations;
    }

    public void setPropertiesUsedInCalculations(String[] propertiesUsedInCalculations) {
        this.propertiesUsedInCalculations = propertiesUsedInCalculations;
    }

    public Map<String, StrokeAction> getStrokeActions() {
        return strokeActions;
    }

    public void setStrokeActions(Map<String, StrokeAction> strokeActions) {
        this.strokeActions = strokeActions;
    }

    public Map<String, Object> getChangedParts() {
        return changedParts;
    }

    public void setChangedParts(Map<String, Object> changedParts) {
        this.changedParts = changedParts;
    }
}
