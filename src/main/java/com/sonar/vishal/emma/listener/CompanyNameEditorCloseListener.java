package com.sonar.vishal.emma.listener;

import com.sonar.vishal.emma.entity.CompanyNameData;
import com.sonar.vishal.emma.service.FireBaseService;
import com.vaadin.flow.component.grid.editor.EditorCloseEvent;
import com.vaadin.flow.component.grid.editor.EditorCloseListener;

import java.util.Map;

public class CompanyNameEditorCloseListener<T> implements EditorCloseListener<T> {

    private FireBaseService fireBaseService;

    public CompanyNameEditorCloseListener(FireBaseService fireBaseService) {
        this.fireBaseService = fireBaseService;
    }

    @Override
    public void onEditorClose(EditorCloseEvent<T> editorCloseEvent) {
        CompanyNameData companyNameData = (CompanyNameData) editorCloseEvent.getItem();
        Map<String, Object> companyNameDataMap = fireBaseService.getCompanyNameData();
        companyNameDataMap.put(companyNameData.getEconomicTimesName(), companyNameData.getZerodhaName());
        fireBaseService.setCompanyNameData(companyNameDataMap);
    }
}
