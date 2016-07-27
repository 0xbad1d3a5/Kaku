package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class MeaningLoanSource {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Meaning fkMeaning;

    @DatabaseField
    private String loanSource;

    @DatabaseField
    private String lang;

    @DatabaseField
    private String type;

    @DatabaseField
    private String waseieigo;

    public String getLoanSource() {
        return loanSource;
    }

    public void setLoanSource(String loanSource) {
        this.loanSource = loanSource;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWaseieigo() {
        return waseieigo;
    }

    public void setWaseieigo(String waseieigo) {
        this.waseieigo = waseieigo;
    }
}
