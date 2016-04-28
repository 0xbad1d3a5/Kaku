package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * general coded information relating to the entry as a whole.
 */
@Root(name = "info")
public class JmInfo {

    @ElementList(entry = "links", inline = true, required = false)
    private List<Links> links;
    @ElementList(entry = "bibl", inline = true, required = false)
    private List<Bibl> bibl;
    @ElementList(entry = "etym", inline = true, required = false)
    private List<String> etym;
    @ElementList(entry = "audit", inline = true, required = false)
    private List<Audit> audit;

    /**
     * This field is used to hold information about the etymology
     * of the kanji or kana parts of the entry. For gairaigo,
     * etymological information may also be in the <lsource> element.
     */
    public List<String> getEtym(){
        return this.etym;
    }

    public List<Links> getLinks(){
        return this.links;
    }

    public List<Bibl> getBibl(){
        return this.bibl;
    }

    public List<Audit> getAudit(){
        return this.audit;
    }
}
