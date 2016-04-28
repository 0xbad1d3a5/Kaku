package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="entry")
public class JmEntry {

    @Element(name = "ent_seq")
    private String ent_seq;
    @ElementList(entry = "k_ele", inline = true, required = false)
    private List<JmKEle> k_ele;
    @ElementList(entry = "r_ele", inline = true)
    private List<JmREle> r_ele;
    @Element(name = "info", required = false)
    private JmInfo info;
    @ElementList(entry = "sense", inline = true)
    private List<JmSense> sense;

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
