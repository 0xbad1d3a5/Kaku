package ca.fuwafuwa.kaku.Deinflictor

import ca.fuwafuwa.kaku.LangUtils

class Deinflector(rulesFile: String) {

    private val _ruleReasons: List<String> = ArrayList()
    private val _ruleGroups: List<DeinflectionRuleGroup> = ArrayList()

    init {

    }

    fun getPotentialDeinflections(text: String) : List<DeinflectedWord> {

        var text: String = LangUtils.ConvertKanatanaToHiragana(text)

        // Map of possible deinflections so far
        var possibleWords = HashMap<String, Int>()
        possibleWords.put(text, 0)

        // List of inflections encountered
        var deList = ArrayList<DeinflectedWord>()
        deList.add(DeinflectedWord(text, 0xFF, ""));

        return deList;
    }
}