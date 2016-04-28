package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * This element holds details of linking information to
 * entries in other electronic repositories. The link_tag will be
 * coded to indicate the type of link (text, image, sound), the
 * link_desc will provided a textual label for the link, and the
 * link_uri contains the actual URI.
 */
@Root(name = "links")
public class Links {

    @Element(name = "link_tag")
    private String link_tag;
    @Element(name = "link_desc")
    private String link_desc;
    @Element(name = "link_uri")
    private String link_uri;

    public String getLinkTag(){
        return this.link_tag;
    }

    public String getLinkDesc(){
        return this.link_desc;
    }

    public String getLinkUri(){
        return this.link_uri;
    }
}
