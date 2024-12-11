package com.sonar.vishal.emma.listener;

import com.sonar.vishal.emma.entity.CompanyNameData;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.grid.editor.Editor;

public class CompanyNameItemDoubleClickListener<T> implements ComponentEventListener<ItemDoubleClickEvent<T>> {

    private Editor<CompanyNameData> companyNameDataEditor;

    public CompanyNameItemDoubleClickListener(Editor<CompanyNameData> companyNameDataEditor) {
        this.companyNameDataEditor = companyNameDataEditor;
    }

    @Override
    public void onComponentEvent(ItemDoubleClickEvent<T> itemDoubleClickEvent) {
        companyNameDataEditor.editItem((CompanyNameData) itemDoubleClickEvent.getItem());
        Component editorComponent = itemDoubleClickEvent.getColumn().getEditorComponent();
        if (editorComponent instanceof Focusable<?> focusable) {
            focusable.focus();
        }
    }
}
