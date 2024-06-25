package com.sonar.vishal.emma.bus;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEventListener {

    public static final Logger LOG = LoggerFactory.getLogger(LogEventListener.class);

    @Subscribe
    public void error(LogErrorEvent logEvent) {
        LOG.error(logEvent.getMessage(), logEvent.getException());
    }
}
