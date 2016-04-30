package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

/**
 * The audit element will contain the date and other information
 * about updates to the entry. Can be used to record the source of
 * the material.
 */
public class JmAudit {

    private String upd_date;
    private String upd_detl;

    public JmAudit(String upd_date, String upd_detl) {
        this.upd_date = upd_date;
        this.upd_detl = upd_detl;
    }

    public String getUpdDate(){
        return this.upd_date;
    }

    public String getUpdDetl(){
        return this.upd_detl;
    }
}
