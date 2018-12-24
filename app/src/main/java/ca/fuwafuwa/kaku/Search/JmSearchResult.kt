package ca.fuwafuwa.kaku.Search

import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized
import ca.fuwafuwa.kaku.Deinflictor.DeinflectionInfo

data class JmSearchResult(
        val entry: EntryOptimized,
        val deinfInfo: DeinflectionInfo?,
        val word: String
)