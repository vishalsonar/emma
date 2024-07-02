package com.sonar.vishal.emma.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
public class RouteNotFoundView extends Component implements HasErrorParameter<NotFoundException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        getElement().setText("Access Denied");
        getStyle().setTextAlign(Style.TextAlign.CENTER);
        getStyle().setFontSize("40px");
        getStyle().setPaddingTop("20%");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
