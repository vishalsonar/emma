package com.sonar.vishal.emma;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Emma", shortName = "Emma")
@Theme("emma-theme")
@Push(PushMode.AUTOMATIC)
public class EmmaAppShell implements AppShellConfigurator {

}
