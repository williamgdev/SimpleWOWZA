package com.randmcnally.bb.poc.dto.openfire;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "broadcastPresenceRole", strict = false)
class BroadcastPresenceRole {
    @Text
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
