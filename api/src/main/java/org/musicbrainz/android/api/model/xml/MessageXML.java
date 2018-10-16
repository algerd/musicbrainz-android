package org.musicbrainz.android.api.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Alex on 12.12.2017.
 */

@Root(name="message")
public class MessageXML {

    @Element(name="text")
    private String text;

    public MessageXML() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
