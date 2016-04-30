package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import java.util.List;

/**
 * general coded information relating to the entry as a whole.
 */
public class JmInfo {

    private List<JmLinks> links;
    private List<JmBibl> bibl;
    private List<String> etym;
    private List<JmAudit> audit;

    public JmInfo(List<JmLinks> links, List<JmBibl> bibl, List<String> etym, List<JmAudit> audit) {
        this.links = links;
        this.bibl = bibl;
        this.etym = etym;
        this.audit = audit;
    }

    /**
     * This field is used to hold information about the etymology
     * of the kanji or kana parts of the entry. For gairaigo,
     * etymological information may also be in the <lsource> element.
     */
    public List<String> getEtym(){
        return this.etym;
    }

    public List<JmLinks> getLinks(){
        return this.links;
    }

    public List<JmBibl> getBibl(){
        return this.bibl;
    }

    public List<JmAudit> getAudit(){
        return this.audit;
    }
}
