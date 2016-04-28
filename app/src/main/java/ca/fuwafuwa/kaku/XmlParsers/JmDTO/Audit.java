package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * The audit element will contain the date and other information
 * about updates to the entry. Can be used to record the source of
 * the material.
 */
@Root(name = "audit")
public class Audit {

    @Element(name = "upd_date")
    private String upd_date;
    @Element(name = "upd_detl")
    private String upd_detl;

    public String getUpdDate(){
        return this.upd_date;
    }

    public String getUpdDetl(){
        return this.upd_detl;
    }
}
