package ca.fuwafuwa.kaku.Deinflictor

class PosMap {

    companion object {

        fun GetPosMapVal(pos: String) : String {

            return when(pos){

                "*" -> "*"

                "一般" -> "general"

                "形容詞" -> "adjective"
                "形容詞・アウオ段" -> "adjective auo-end"
                "形容詞・イ段" -> "adjective i-end"
                "形容詞・イイ" -> "adjective ii"
                "副詞" -> "adverb"
                "助動詞" -> "auxiliary verb"
                "接続詞" -> "conjunction"
                "接続助詞" -> "conjunction particle"
                "連体詞" -> "pre-noun adjectival"
                "感動詞" -> "interjection"
                "名詞" -> "noun"
                "助詞" -> "particle"
                "格助詞" -> "case-marking particle"
                "終助詞" -> "sentence-ending particle"
                "副助詞" -> "adverbial particle"
                "並立助詞" -> "parallel marker"
                "係助詞  " -> "binding particle"
                "副助詞／並立助詞／終助詞" -> "adverbial particle/parallel marker/binding particle"
                "接頭詞" -> "prefix"
                "助数詞" -> "counter suffix"
                "動詞" -> "verb"
                "代名詞" -> "pronoun"
                "固有名詞" -> "proper noun"

                "動詞非自立的" -> pos
                "形容動詞語幹" -> pos
                "副詞可能" -> pos
                "ナイ形容詞語幹" -> pos
                "助動詞語幹" -> pos
                "接続詞的" -> "conjunction-like"

                "自立" -> "independent"
                "非自立" -> "not independent"

                "基本形" -> "basic form"
                "文語基本形" -> "classical basic form"
                "仮定形" -> "hypothetical form"
                "未然形" -> "imperfective form (nai stem)"
                "連用形" -> "conjunctive form (masu stem)"
                "音便基本形" -> "euphonic change basic form"
                "現代基本形" -> "modern basic form"
                "基本形-促音便" -> "basic form - nasal sound change"

                "未然ヌ接続" -> "before -ta link"
                "未然ウ接続" -> "before -u link"
                "未然レル接続" -> "before -reru link"
                "連用タ接続" -> "continuous -ta link"
                "連用テ接続" -> "continuous -te link"
                "連用ニ接続" -> "continuous -ni link"
                "連用デ接続" -> "continuous -de link"
                "連用ゴザイ接続" -> "continuous -gozai link"
                "体言接続" -> "uninflected word link"
                "ガル接続" -> "-garu link"
                "助詞類接続" -> "particle link"
                "サ変接続" -> "irregular link"
                "名詞接続" -> "noun link"
                "形容詞接続" -> "adjective link"
                "数接続" -> "number link"
                "動詞接続" -> "verb link"
                "体言接続特殊" -> "special uninflected word link"
                "体言接続特殊２" -> "special uninflected word link 2"

                "仮定縮約１" -> "assumed contraction 1"
                "仮定縮約２" -> "assumed contraction 2"

                "接尾" -> "suffix"
                "不変化型" -> "Invariant type"
                "未然特殊" -> "before special"
                "連語" -> "compound word"
                "フィラー" -> pos

                "連体化" -> pos
                "副詞化" -> pos

                "特殊" -> "special"
                "特殊・マス" -> "masu"
                "特殊・デス" -> "desu"
                "特殊・ジャ" -> "jya"
                "特殊・タ" -> "ta"
                "特殊・タイ" -> "tai"
                "特殊・ヌ" -> "nu"
                "特殊・ヤ" -> "ya"
                "特殊・ナイ" -> "nai"
                "特殊・ダ" -> "da"

                "命令ｅ" -> "e-command"
                "命令ｉ" -> "i-command"
                "命令ｙｏ" -> "yo-command"
                "命令ｒｏ" -> "ro-command"

                "文語・ベシ" -> "classical beshi"
                "文語・マジ" -> "classical maji"
                "文語・キ" -> "classical ki"
                "文語・ナリ" -> "classical nari"
                "文語・ル" -> "classical ru"
                "文語・リ" -> "classical ri"
                "文語・ケリ" -> "classical keri"
                "文語・ゴトシ" -> "classical gotoshi"

                "一段" -> "ichidan verb"
                "一段・クレル" -> "ichidan verb kureru"
                "一段・得ル" -> "ichidan verb eru"

                "四段・ハ行" -> "yodan verb ha"
                "四段・タ行" -> "yodan verb ta"
                "四段・サ行" -> "yodan verb sa"
                "四段・バ行" -> "yodan verb ba"

                "五段・ラ行特殊" -> pos
                "五段・ラ行アル" -> pos
                "五段・マ行" -> pos
                "五段・サ行" -> pos
                "五段・ラ行" -> pos
                "五段・ワ行促音便" -> pos
                "五段・カ行イ音便" -> pos
                "五段・ガ行" -> pos
                "五段・バ行" -> pos
                "五段・タ行" -> pos
                "五段・ナ行" -> pos
                "五段・ワ行ウ音便" -> pos
                "五段・カ行促音便ユク" -> pos
                "五段・カ行促音便" -> pos

                "上二・ハ行" -> pos
                "上二・ダ行" -> pos

                "下二・タ行" -> pos
                "下二・ハ行" -> pos
                "下二・ガ行" -> pos
                "下二・カ行" -> pos
                "下二・得" -> pos
                "下二・マ行" -> pos
                "下二・ダ行" -> pos

                "サ変・−ズル" -> pos
                "サ変・−スル" -> pos
                "カ変・来ル" -> pos
                "ラ変" -> pos
                "サ変・スル" -> pos
                "カ変・クル" -> pos

                "縮約" -> pos
                "人名" -> pos
                "名" -> pos
                "姓" -> pos
                "数" -> pos
                "組織" -> pos
                "引用文字列" -> pos
                "地域" -> pos
                "国" -> pos
                "引用" -> pos
                "その他" -> pos
                "間投" -> pos
                "記号" -> pos
                "アルファベット" -> pos
                "括弧閉" -> pos
                "括弧開" -> pos
                "読点" -> pos
                "句点" -> pos
                "空白" -> pos

                else -> "UNMAPPED"
            };
        }
    }
}