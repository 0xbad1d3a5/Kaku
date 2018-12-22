package ca.fuwafuwa.kaku.Deinflictor

data class DeinflectionRuleGroup(val Rules: ArrayList<DeinflectionRule> = ArrayList())

data class DeinflectionRule(
        val from: String,
        val to: String,
        val type: Int,
        val reason: String)