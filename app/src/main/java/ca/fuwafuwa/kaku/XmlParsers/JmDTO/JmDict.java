package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="JMdict")
public class JmDict {

    @ElementList(entry = "entry", inline = true)
    List<JmEntry> entry;

    public List<JmEntry> getEntries(){
        return this.entry;
    }
}
