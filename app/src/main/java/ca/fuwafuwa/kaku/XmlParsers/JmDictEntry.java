package ca.fuwafuwa.kaku.XmlParsers;

import java.util.List;

/**
 * Created by Xyresic on 4/25/2016.
 */

/**
 * The kanji element, or in its absence, the reading element, is
 * the defining component of each entry.
 * The overwhelming majority of entries will have a single kanji
 * element associated with a word in Japanese. Where there are
 * multiple kanji elements within an entry, they will be orthographical
 * variants of the same word, either using variations in okurigana, or
 * alternative and equivalent kanji. Common "mis-spellings" may be
 * included, provided they are associated with appropriate information
 * fields. Synonyms are not included; they may be indicated in the
 * cross-reference field associated with the sense element.
 */
class JmKEle {

    /**
     * This element will contain a word or short phrase in Japanese
     * which is written using at least one non-kana character (usually kanji,
     * but can be other characters). The valid characters are
     * kanji, kana, related characters such as chouon and kurikaeshi, and
     * in exceptional cases, letters from other alphabets.
     */
    private String keb;

    /**
     * This is a coded information field related specifically to the
     * orthography of the keb, and will typically indicate some unusual
     * aspect, such as okurigana irregularity.
     */
    private List<String> ke_inf;

    /**
     * This and the equivalent re_pri field are provided to record
     * information about the relative priority of the entry,  and consist
     * of codes indicating the word appears in various references which
     * can be taken as an indication of the frequency with which the word
     * is used. This field is intended for use either by applications which
     * want to concentrate on entries of  a particular priority, or to
     * generate subset files.
     * The current values in this field are:
     * - news1/2: appears in the "wordfreq" file compiled by Alexandre Girardi
     * from the Mainichi Shimbun. (See the Monash ftp archive for a copy.)
     * Words in the first 12,000 in that file are marked "news1" and words
     * in the second 12,000 are marked "news2".
     * - ichi1/2: appears in the "Ichimango goi bunruishuu", Senmon Kyouiku
     * Publishing, Tokyo, 1998.  (The entries marked "ichi2" were
     * demoted from ichi1 because they were observed to have low
     * frequencies in the WWW and newspapers.)
     * - spec1 and spec2: a small number of words use this marker when they
     * are detected as being common, but are not included in other lists.
     * - gai1/2: common loanwords, based on the wordfreq file.
     * - nfxx: this is an indicator of frequency-of-use ranking in the
     * wordfreq file. "xx" is the number of the set of 500 words in which
     * the entry can be found, with "01" assigned to the first 500, "02"
     * to the second, and so on. (The entries with news1, ichi1, spec1 and
     * gai1 values are marked with a "(P)" in the EDICT and EDICT2
     * files.)
     * <p/>
     * The reason both the kanji and reading elements are tagged is because
     * on occasions a priority is only associated with a particular
     * kanji/reading pair.
     */
    private List<String> ke_pri;

    public JmKEle(String keb, List<String> ke_inf, List<String> ke_pri) {
        this.keb = keb;
        this.ke_inf = ke_inf;
        this.ke_pri = ke_pri;
    }
}

/**
 * The reading element typically contains the valid readings
 * of the word(s) in the kanji element using modern kanadzukai.
 * Where there are multiple reading elements, they will typically be
 * alternative readings of the kanji element. In the absence of a
 * kanji element, i.e. in the case of a word or phrase written
 * entirely in kana, these elements will define the entry.
 */
class JmREle {

    /**
     * this element content is restricted to kana and related
     * characters such as chouon and kurikaeshi. Kana usage will be
     * consistent between the keb and reb elements; e.g. if the keb
     * contains katakana, so too will the reb.
     */
    private String reb;

    /**
     * This element, which will usually have a null value, indicates
     * that the reb, while associated with the keb, cannot be regarded
     * as a true reading of the kanji. It is typically used for words
     * such as foreign place names, gairaigo which can be in kanji or
     * katakana, etc.
     */
    private String re_nokanji;

    /**
     * This element is used to indicate when the reading only applies
     * to a subset of the keb elements in the entry. In its absence, all
     * readings apply to all kanji elements. The contents of this element
     * must exactly match those of one of the keb elements.
     */
    private List<String> re_restr;

    /**
     * General coded information pertaining to the specific reading.
     * Typically it will be used to indicate some unusual aspect of
     * the reading.
     */
    private List<String> re_inf;

    /**
     * See the comment on ke_pri above.
     */
    private List<String> re_pri;

    public JmREle(String reb, String re_nokanji, List<String> re_restr, List<String> re_inf, List<String> re_pri) {
        this.reb = reb;
        this.re_nokanji = re_nokanji;
        this.re_restr = re_restr;
        this.re_inf = re_inf;
        this.re_pri = re_pri;
    }
}

/**
 * general coded information relating to the entry as a whole.
 */
class JmInfo {

    /**
     * This element holds details of linking information to
     * entries in other electronic repositories. The link_tag will be
     * coded to indicate the type of link (text, image, sound), the
     * link_desc will provided a textual label for the link, and the
     * link_uri contains the actual URI.
     */
    public class Links {

        private String link_tag;
        private String link_desc;
        private String link_uri;

        public Links(String link_tag, String link_desc, String link_uri) {
            this.link_tag = link_tag;
            this.link_desc = link_desc;
            this.link_uri = link_uri;
        }
    }

    /**
     * Bibliographic information about the entry. The bib_tag will a
     * coded reference to an entry in an external bibliographic database.
     * The bib_txt field may be used for brief (local) descriptions.
     */
    public class Bibl {

        private String bib_tag;
        private String bib_txt;

        public Bibl(String bib_tag, String bib_txt) {
            this.bib_tag = bib_tag;
            this.bib_txt = bib_txt;
        }
    }

    /**
     * The audit element will contain the date and other information
     * about updates to the entry. Can be used to record the source of
     * the material.
     */
    public class Audit {

        private String upd_date;
        private String upd_detl;

        public Audit(String upd_date, String upd_detl) {
            this.upd_date = upd_date;
            this.upd_detl = upd_detl;
        }
    }

    /**
     * This field is used to hold information about the etymology
     * of the kanji or kana parts of the entry. For gairaigo,
     * etymological information may also be in the <lsource> element.
     */
    private List<String> etym;

    private List<Links> links;
    private List<Bibl> bibl;
    private List<Audit> audit;

    public JmInfo(List<String> etym, List<Links> links, List<Bibl> bibl, List<Audit> audit) {
        this.etym = etym;
        this.links = links;
        this.bibl = bibl;
        this.audit = audit;
    }
}

/**
 * The sense element will record the translational equivalent
 * of the Japanese word, plus other related information. Where there
 * are several distinctly different meanings of the word, multiple
 * sense elements will be employed.
 */
class JmSense {

    /**
     * These elements, if present, indicate that the sense is restricted
     * to the lexeme represented by the keb and/or reb.
     */
    private List<String> stagk;
    private List<String> stagr;

    /**
     * Part-of-speech information about the entry/sense. Should use
     * appropriate entity codes. In general where there are multiple senses
     * in an entry, the part-of-speech of an earlier sense will apply to
     * later senses unless there is a new part-of-speech indicated.
     */
    private List<String> pos;

    /**
     * This element is used to indicate a cross-reference to another
     * entry with a similar or related meaning or sense. The content of
     * this element is typically a keb or reb element in another entry. In some
     * cases a keb will be followed by a reb and/or a sense number to provide
     * a precise target for the cross-reference. Where this happens, a JIS
     * "centre-dot" (0x2126) is placed between the components of the
     * cross-reference.
     */
    private List<String> xref;

    /**
     * This element is used to indicate another entry which is an
     * antonym of the current entry/sense. The content of this element
     * must exactly match that of a keb or reb element in another entry.
     */
    private List<String> ant;

    /**
     * Information about the field of application of the entry/sense.
     * When absent, general application is implied. Entity coding for
     * specific fields of application.
     */
    private List<String> field;

    /**
     * This element is used for other relevant information about
     * the entry/sense. As with part-of-speech, information will usually
     * apply to several senses.
     */
    private List<String> misc;

    /**
     * The sense-information elements provided for additional
     * information to be recorded about a sense. Typical usage would
     * be to indicate such things as level of currency of a sense, the
     * regional variations, etc.
     */
    private List<String> s_inf;

    /**
     * This element records the information about the source
     * language(s) of a loan-word/gairaigo. If the source language is other
     * than English, the language is indicated by the xml:lang attribute.
     * The element value (if any) is the source word or phrase.
     */
    private List<String> lsource;

    /**
     * For words specifically associated with regional dialects in
     * Japanese, the entity code for that dialect, e.g. ksb for Kansaiben.
     */
    private List<String> dial;

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
    private List<String> gloss;

    /**
     * The example elements provide for pairs of short Japanese and
     * target-language phrases or sentences which exemplify the usage of the
     * Japanese head-word and the target-language gloss. Words in example
     * fields would typically not be indexed by a dictionary application.
     */
    private List<String> example;

    public JmSense(List<String> stagk, List<String> stagr, List<String> pos, List<String> xref, List<String> ant, List<String> field, List<String> misc, List<String> s_inf, List<String> lsource, List<String> dial, List<String> gloss, List<String> example) {
        this.stagk = stagk;
        this.stagr = stagr;
        this.pos = pos;
        this.xref = xref;
        this.ant = ant;
        this.field = field;
        this.misc = misc;
        this.s_inf = s_inf;
        this.lsource = lsource;
        this.dial = dial;
        this.gloss = gloss;
        this.example = example;
    }
}

class JmEntry {

    /**
     * A unique numeric sequence number for each entry
     */
    private String ent_seq;

    private List<JmKEle> jmK_ele;
    private List<JmREle> jmR_ele;
    private JmInfo jmInfo;
    private List<JmSense> jmSense;

    public JmEntry(String ent_seq, List<JmKEle> jmK_ele, List<JmREle> jmR_ele, JmInfo jmInfo, List<JmSense> jmSense) {
        this.ent_seq = ent_seq;
        this.jmK_ele = jmK_ele;
        this.jmR_ele = jmR_ele;
        this.jmInfo = jmInfo;
        this.jmSense = jmSense;
    }
}
