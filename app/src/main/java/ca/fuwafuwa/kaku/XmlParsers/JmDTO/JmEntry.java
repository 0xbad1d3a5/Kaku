package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import java.util.List;

public class JmEntry {

    private String ent_seq;
    private List<JmKEle> k_ele;
    private List<JmREle> r_ele;
    private JmInfo info;
    private List<JmSense> sense;

    public JmEntry(String ent_seq, List<JmKEle> k_ele, List<JmREle> r_ele, JmInfo info, List<JmSense> sense) {
        this.ent_seq = ent_seq;
        this.k_ele = k_ele;
        this.r_ele = r_ele;
        this.info = info;
        this.sense = sense;
    }

    /**
     * A unique numeric sequence number for each entry
     */
    public String getEntSeq(){
        return this.ent_seq;
    }

    public List<JmKEle> getKEle(){
        return this.k_ele;
    }

    public List<JmREle> getREle(){
        return this.r_ele;
    }

    public JmInfo getInfo(){
        return this.info;
    }

    public List<JmSense> getSense(){
        return this.sense;
    }
}
