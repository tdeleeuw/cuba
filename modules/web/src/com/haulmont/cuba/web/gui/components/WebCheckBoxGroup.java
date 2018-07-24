package com.haulmont.cuba.web.gui.components;

import com.haulmont.cuba.gui.components.CaptionMode;
import com.haulmont.cuba.gui.components.CheckBoxGroup;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.data.OptionsSource;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class WebCheckBoxGroup<V, I> extends WebAbstractViewComponent<com.vaadin.ui.CheckBoxGroup<V>, V, V>
        implements CheckBoxGroup<V, I> {

    @Override
    public void focus() {
        // TODO: gg, implement
    }

    @Override
    public int getTabIndex() {
        // TODO: gg, implement
        return 0;
    }

    @Override
    public Orientation getOrientation() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setOrientation(Orientation orientation) {
        // TODO: gg, implement
    }

    @Override
    public void setLookupSelectHandler(Runnable selectHandler) {
        // TODO: gg, implement
    }

    @Override
    public Collection getLookupSelectedItems() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setOptionsSource(OptionsSource<I> optionsSource) {
        // TODO: gg, implement
    }

    @Override
    public OptionsSource<I> getOptionsSource() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setOptionCaptionProvider(Function<? super I, String> captionProvider) {
        // TODO: gg, implement
    }

    @Override
    public Function<? super I, String> getOptionCaptionProvider() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public CaptionMode getCaptionMode() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setCaptionMode(CaptionMode captionMode) {
        // TODO: gg, implement
    }

    @Override
    public String getCaptionProperty() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setCaptionProperty(String captionProperty) {
        // TODO: gg, implement
    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO: gg, implement
    }

    @Override
    public boolean isRequired() {
        // TODO: gg, implement
        return false;
    }

    @Override
    public void setRequired(boolean required) {
        // TODO: gg, implement
    }

    @Override
    public String getRequiredMessage() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setRequiredMessage(String msg) {
        // TODO: gg, implement
    }

    @Override
    public void addValidator(Validator validator) {
        // TODO: gg, implement
    }

    @Override
    public void removeValidator(Validator validator) {
        // TODO: gg, implement
    }

    @Override
    public Collection<Validator> getValidators() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public boolean isEditable() {
        // TODO: gg, implement
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
        // TODO: gg, implement
    }

    @Override
    protected void setValueToPresentation(V value) {
        // TODO: gg, implement
    }

    @Override
    public String getContextHelpText() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setContextHelpText(String contextHelpText) {
        // TODO: gg, implement
    }

    @Override
    public boolean isContextHelpTextHtmlEnabled() {
        // TODO: gg, implement
        return false;
    }

    @Override
    public void setContextHelpTextHtmlEnabled(boolean enabled) {
        // TODO: gg, implement
    }

    @Override
    public Consumer<ContextHelpIconClickEvent> getContextHelpIconClickHandler() {
        // TODO: gg, implement
        return null;
    }

    @Override
    public void setContextHelpIconClickHandler(Consumer<ContextHelpIconClickEvent> handler) {
        // TODO: gg, implement
    }

    @Override
    public boolean isValid() {
        // TODO: gg, implement
        return false;
    }

    @Override
    public void validate() throws ValidationException {
        // TODO: gg, implement
    }
}
