package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import java.util.List;

public class JmDict {

    private List<JmEntry> entry;

    public JmDict(List<JmEntry> entry) {
        this.entry = entry;
    }

    public List<JmEntry> getEntries(){
        return this.entry;
    }
}
