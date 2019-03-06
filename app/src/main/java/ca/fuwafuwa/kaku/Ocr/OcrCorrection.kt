package ca.fuwafuwa.kaku.Ocr

class OcrCorrection
{
    companion object
    {
        val CommonKanaLookalikes: List<List<String>> = listOf(

                // Hiragana
                listOf("あ", "ぁ", "お", "ぉ"),
                listOf("い", "ぃ"),
                listOf("う", "ぅ"),
                listOf("え", "ぇ"),
                listOf("お", "ぉ"),

                listOf("か", "が"),
                listOf("き", "ぎ"),
                listOf("く", "ぐ", "〈", "<", "＜"),
                listOf("け", "げ"),
                listOf("こ", "ご"),

                listOf("さ", "ざ"),
                listOf("し", "じ", "L", "Ｌ"),
                listOf("す", "ず"),
                listOf("せ", "ぜ"),
                listOf("そ", "ぞ"),

                listOf("た", "だ"),
                listOf("ち", "ぢ"),
                listOf("つ", "づ", "っ"),
                listOf("て", "で"),
                listOf("と", "ど"),

                listOf("な"),
                listOf("に"),
                listOf("ぬ"),
                listOf("ね"),
                listOf("の"),

                listOf("は", "ば", "ぱ"),
                listOf("ひ", "び", "ぴ"),
                listOf("ふ", "ぶ", "ぷ"),
                listOf("へ", "べ", "ぺ"),
                listOf("ほ", "ぼ", "ぽ"),

                listOf("ま"),
                listOf("み"),
                listOf("む"),
                listOf("め"),
                listOf("も"),

                listOf("や", "ゃ"),
                listOf("ゆ", "ゅ"),
                listOf("よ", "ょ"),

                listOf("ら"),
                listOf("り"),
                listOf("る"),
                listOf("れ"),
                listOf("ろ"),

                listOf("わ", "ゎ"),
                listOf("を"),
                listOf("ん"),

                // Katakana
                listOf("ア", "ァ"),
                listOf("イ", "ィ"),
                listOf("ウ", "ゥ"),
                listOf("エ", "ェ"),
                listOf("オ", "ォ"),

                listOf("カ", "ガ", "ヵ"),
                listOf("キ", "ギ"),
                listOf("ク", "グ", "ㇰ"),
                listOf("ケ", "ゲ", "ヶ"),
                listOf("コ", "ゴ"),

                listOf("サ", "ザ"),
                listOf("シ", "ジ", "ㇱ"),
                listOf("ス", "ズ", "ㇲ"),
                listOf("セ", "ゼ"),
                listOf("ソ", "ゾ"),

                listOf("タ", "ダ", "夕"),
                listOf("チ", "ヂ"),
                listOf("ツ", "ヅ", "ッ"),
                listOf("テ", "デ"),
                listOf("ト", "ド", "ㇳ"),

                listOf("ナ"),
                listOf("ニ"),
                listOf("ヌ", "ㇴ"),
                listOf("ネ"),
                listOf("ノ"),

                listOf("ハ", "バ", "パ", "ㇵ"),
                listOf("ヒ", "ビ", "ピ", "ㇶ"),
                listOf("フ", "ブ", "プ", "ㇷ", "\u31f7\u309a"),
                listOf("ヘ", "ベ", "ペ", "ㇸ"),
                listOf("ホ", "ボ", "ポ", "ㇹ"),

                listOf("マ"),
                listOf("ミ"),
                listOf("ム", "ㇺ"),
                listOf("メ"),
                listOf("モ"),

                listOf("ヤ", "ャ"),
                listOf("ユ", "ュ"),
                listOf("ヨ", "ョ"),

                listOf("ラ", "ㇻ"),
                listOf("リ", "ㇼ"),
                listOf("ル", "ㇽ"),
                listOf("レ", "ㇾ"),
                listOf("ロ", "ㇿ"),

                listOf("ワ", "ヮ"),
                listOf("ヲ"),
                listOf("ン"),

                // Other
                listOf("ー", "一", "―", "‐", "—", "－", "-", "_")
        )
    }
}