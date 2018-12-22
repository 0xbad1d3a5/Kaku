package ca.fuwafuwa.kaku.Deinflictor

/**
 * A RuleGroup is composed of multiple rules that share the same [DeinflectionRule.from] length
 *
 * @property rules List containing the rules in the RuleGroup
 * @property fromLength The [DeinflectionRule.from] length of the RuleGroup
 */
class DeinflectionRuleGroup
{
    val rules: ArrayList<DeinflectionRule> = ArrayList()
    var fromLength: Int = 0
}

/**
 * Data class to represent each rule in deinflect.dat
 *
 * @property from The inflected form
 * @property to The deinflected form
 * @property type Bitmask for determining whether to continue processing deinflection.
 * This is kinda Japanese black magic and I don't really understand it that well either.
 * @property reason Deinflection reason
 */
data class DeinflectionRule(
        val from: String,
        val to: String,
        val type: Int,
        val reason: String)

/**
 * Class to represent a deinflected word
 *
 * @property word The deinflected word
 * @property type Bitmask for determining whether to continue processing deinflection
 * @property reason Deinflection reason
 */
class DeinflectionInfo(
        var word: String,
        var type: Int,
        var reason: String)