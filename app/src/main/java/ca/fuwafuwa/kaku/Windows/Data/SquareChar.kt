package ca.fuwafuwa.kaku.Windows.Data

interface ISquareChar
{
    var index: Int

    var char: String

    var text: String?

    var userTouched: Boolean

    val displayData: DisplayData

    fun clone() : ISquareChar
}

open class SquareChar(override val displayData: DisplayData,
                      override var char: String) : ISquareChar
{
    override var index: Int = -1

    override var userTouched: Boolean = false

    override var text : String? = null
    get()
    {
        val returnText = field
        field = null
        return returnText
    }

    override fun clone(): ISquareChar
    {
        return SquareChar(displayData, char)
    }
}

class SquareCharOcr(override val displayData: DisplayDataOcr,
                    val allChoices : MutableList<Pair<String, Double>>,
                    val bitmapPos: IntArray) : SquareChar(displayData, "")
{
    override var char : String = ""
    set(value)
    {
        assert(allChoices.any { x -> x.first == char })
        field = value
    }

    init
    {
        sortChoices()
        char = allChoices[0].first
    }

    fun addChoice(char: String, certainty: ChoiceCertainty)
    {
        if (certainty == ChoiceCertainty.CERTAIN)
        {
            allChoices.add(0, Pair(char, 100.0))
        }
        else {
            allChoices.add(Pair(char, 0.0))
        }
    }

    override fun clone(): ISquareChar
    {
        return SquareCharOcr(displayData, allChoices.toMutableList(), bitmapPos)
    }

    private fun sortChoices()
    {
        allChoices.sortByDescending { x -> x.second }
    }
}

enum class ChoiceCertainty
{
    CERTAIN,
    UNCERTAIN
}
