package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

/**
 * This element holds details of linking information to
 * entries in other electronic repositories. The link_tag will be
 * coded to indicate the type of link (text, image, sound), the
 * link_desc will provided a textual label for the link, and the
 * link_uri contains the actual URI.
 */
public class JmLinks {

    private String link_tag;
    private String link_desc;
    private String link_uri;

    public JmLinks(String link_tag, String link_desc, String link_uri) {
        this.link_tag = link_tag;
        this.link_desc = link_desc;
        this.link_uri = link_uri;
    }

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
