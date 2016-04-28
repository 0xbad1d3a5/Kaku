package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * The sense element will record the translational equivalent
 * of the Japanese word, plus other related information. Where there
 * are several distinctly different meanings of the word, multiple
 * sense elements will be employed.
 */
@Root(name = "sense")
public class JmSense {

    @ElementList(entry = "stagk", inline = true, required = false)
    private List<String> stagk;
    @ElementList(entry = "stagr", inline = true, required = false)
    private List<String> stagr;
    @ElementList(entry = "pos", inline = true, required = false)
    private List<String> pos;
    @ElementList(entry = "xref", inline = true, required = false)
    private List<String> xref;
    @ElementList(entry = "ant", inline = true, required = false)
    private List<String> ant;
    @ElementList(entry = "field", inline = true, required = false)
    private List<String> field;
    @ElementList(entry = "misc", inline = true, required = false)
    private List<String> misc;
    @ElementList(entry = "s_inf", inline = true, required = false)
    private List<String> s_inf;
    @ElementList(entry = "lsource", inline = true, required = false)
    private List<String> lsource;
    @ElementList(entry = "dial", inline = true, required = false)
    private List<String> dial;
    @ElementList(entry = "gloss", inline = true, required = false)
    private List<String> gloss;
    @ElementList(entry = "example", inline = true, required = false)
    private List<String> example;

    /**
     * These elements, if present, indicate that the sense is restricted
     * to the lexeme represented by the keb and/or reb.
     */
    public List<String> getStagk(){
        return this.stagk;
    }
    public List<String> getStagr(){
        return this.stagr;
    }

    /**
     * Part-of-speech information about the entry/sense. Should use
     * appropriate entity codes. In general where there are multiple senses
     * in an entry, the part-of-speech of an earlier sense will apply to
     * later senses unless there is a new part-of-speech indicated.
     */
    public List<String> getPos(){
        return this.pos;
    }

    /**
     * This element is used to indicate a cross-reference to another
     * entry with a similar or related meaning or sense. The content of
     * this element is typically a keb or reb element in another entry. In some
     * cases a keb will be followed by a reb and/or a sense number to provide
     * a precise target for the cross-reference. Where this happens, a JIS
     * "centre-dot" (0x2126) is placed between the components of the
     * cross-reference.
     */
    public List<String> getXRef() {
        return this.xref;
    }

    /**
     * This element is used to indicate another entry which is an
     * antonym of the current entry/sense. The content of this element
     * must exactly match that of a keb or reb element in another entry.
     */
    public List<String> getAnt(){
        return this.ant;
    }

    /**
     * Information about the field of application of the entry/sense.
     * When absent, general application is implied. Entity coding for
     * specific fields of application.
     */
    public List<String> getField(){
        return this.field;
    }

    /**
     * This element is used for other relevant information about
     * the entry/sense. As with part-of-speech, information will usually
     * apply to several senses.
     */
    public List<String> getMisc(){
        return this.misc;
    }

    /**
     * The sense-information elements provided for additional
     * information to be recorded about a sense. Typical usage would
     * be to indicate such things as level of currency of a sense, the
     * regional variations, etc.
     */
    public List<String> getSInf(){
        return this.s_inf;
    }

    /**
     * This element records the information about the source
     * language(s) of a loan-word/gairaigo. If the source language is other
     * than English, the language is indicated by the xml:lang attribute.
     * The element value (if any) is the source word or phrase.
     */
    public List<String> getLSource(){
        return this.lsource;
    }

    /**
     * For words specifically associated with regional dialects in
     * Japanese, the entity code for that dialect, e.g. ksb for Kansaiben.
     */
    public List<String> getDial(){
        return this.dial;
    }

    /**
     * Within each sense will be one or more "glosses", i.e.
     * target-language words or phrases which are equivalents to the
     * Japanese word. This element would normally be present, however it
     * may be omitted in entries which are purely for a cross-reference.
     *
     * Note: <!ELEMENT gloss (#PCDATA | pri)*>
     *       <!ELEMENT pri (#PCDATA)>
     *       These elements highlight particular target-language words which
     *       are strongly associated with the Japanese word. The purpose is to
     *       establish a set of target-language words which can effectively be
     *       used as head-words in a reverse target-language/Japanese relationship.
     */
    public List<String> getGloss(){
        return this.gloss;
    }

    /**
     * The example elements provide for pairs of short Japanese and
     * target-language phrases or sentences which exemplify the usage of the
     * Japanese head-word and the target-language gloss. Words in example
     * fields would typically not be indexed by a dictionary application.
     */
    public List<String> getExample(){
        return this.example;
    }
}
