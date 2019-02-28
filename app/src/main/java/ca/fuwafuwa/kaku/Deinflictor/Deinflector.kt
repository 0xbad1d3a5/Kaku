package ca.fuwafuwa.kaku.Deinflictor

import android.content.Context
import ca.fuwafuwa.kaku.LangUtils
import java.io.BufferedReader

class Deinflector(context: Context) {

    private val _ruleGroups: ArrayList<DeinflectionRuleGroup> = ArrayList()

    init
    {
        loadRules(context.assets.open("deinflect.dat").bufferedReader(Charsets.UTF_8))
    }

    private fun loadRules(reader: BufferedReader)
    {
        // Skip header
        reader.readLine()

        var ruleGroup = DeinflectionRuleGroup()
        ruleGroup.fromLength = -1

        var ruleReasons: ArrayList<String> = ArrayList()

        reader.forEachLine {

            var fields: List<String> = it.split("\t")

            // Parse reason from file
            if (fields.size == 1)
            {
                ruleReasons.add(fields[0].trim())
            }
            // Parse rule from file
            else if (fields.size == 4)
            {
                var rule = DeinflectionRule(fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim().toInt(),
                        ruleReasons.get(fields[3].trim().toInt()))

                // Group inflections of the same length together into the same RuleGroup
                if (ruleGroup.fromLength != rule.from.length){
                    ruleGroup = DeinflectionRuleGroup()
                    ruleGroup.fromLength = rule.from.length
                    _ruleGroups.add(ruleGroup)
                }

                ruleGroup.rules.add(rule)
            }
        }
    }

    /**
     * Gets potential deinflections of an inflected word.
     * Does not guarantee that each deinflection is a real word.
     *
     * Example:
     * かった	い	1152 (00000000 00000000 00000100 10000000)	14 (past)
     * ない	る	2308 (00000000 00000000 00001001 00000100)	15 (negative)
     *
     * Starting word: 食べなかった
     *
     * 食べなかった
     * - 食べない
     * - 00000000 00000000 00000000 00000100
     * - "< past"
     *
     * 食べない
     * - 食べる
     * - 00000000 00000000 00000000 00001001
     * - "< negative"
     *
     * Chain is: ["食べない", "食べる"]
     */
    fun getPotentialDeinflections(word: String): List<DeinflectionInfo> {

        var text: String = word //LangUtils.ConvertKanatanaToHiragana(word)

        // Chain of inflections encountered
        var deinfWordChain = ArrayList<DeinflectionInfo>()
        deinfWordChain.add(DeinflectionInfo(text, 0xFF, ""))

        // Map of possible deinflections to its deinfWordList index
        var prevSeenDeinfWords = HashMap<String, Int>()
        prevSeenDeinfWords[text] = 0

        var currWordChainIndex = 0

        do
        {
            var currDeinflectionInfo: DeinflectionInfo = deinfWordChain.get(currWordChainIndex)
            var currWord: String = currDeinflectionInfo.word

            for (ruleGroup in _ruleGroups)
            {
                // Only process RuleGroup if inflected word is longer than the group
                if (ruleGroup.fromLength > currWord.length) continue

                // Get the last X characters of word so that wordTail is the same length as the RuleGroup
                var wordTail: String = currWord.substring(currWord.length - ruleGroup.fromLength)

                for (rule in ruleGroup.rules)
                {
                    // Only process rule if wordTail matches a valid inflection rule
                    // Only process rule if previous word in deinflection chain allows for such a transformation (type does not mask out)
                    var shouldProcessRule = (currDeinflectionInfo.type and rule.type != 0) && (wordTail == rule.from)
                    if (!shouldProcessRule) continue

                    // Inflected words must be at least 2 characters in length
                    var newWord: String = currWord.substring(0, currWord.length - rule.from.length) + rule.to
                    if (newWord.length <= 1) continue

                    // We've seen this deinflection before under a different rule, update type
                    var prevSeenWordIndex: Int? = prevSeenDeinfWords.get(newWord)
                    if (prevSeenWordIndex != null)
                    {
                        var prevSeenWordType: Int = deinfWordChain[prevSeenWordIndex].type
                        deinfWordChain[prevSeenWordIndex].type = prevSeenWordType or (rule.type shr 8)
                        continue
                    }

                    // Add new deinflection to the deinflection chain
                    var newDeinflectedWord = DeinflectionInfo(
                            newWord,
                            rule.type shr 8,
                            if (currDeinflectionInfo.reason.isNotEmpty()) "< ${rule.reason} ${currDeinflectionInfo.reason}" else "< ${rule.reason}"
                    )
                    prevSeenDeinfWords[newWord] = deinfWordChain.size
                    deinfWordChain.add(newDeinflectedWord)
                }
            }
        } while (++currWordChainIndex < deinfWordChain.size)

        return deinfWordChain
    }
}