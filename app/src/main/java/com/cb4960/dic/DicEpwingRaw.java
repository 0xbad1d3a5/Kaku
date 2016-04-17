/*******************************************************************************
 * Copyright 2013-2015Christopher Brochtrup
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.cb4960.dic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an EWPING dictionary that that does not have a parser. 
 * 
 */
public class DicEpwingRaw extends DicEpwing
{
  /** Constructor. */
  public DicEpwingRaw(String catalogsFile, int subBookIdx)
  {
    super(catalogsFile, subBookIdx);

    String title = DicEpwing.getTitle(catalogsFile);
    int shortNameLen = 6;
    
    if(title.length() < shortNameLen)
    {
      shortNameLen = title.length();
    }
    
    this.Name = title;
    this.NameEng = "RAW";
    this.ShortName = title.substring(0, shortNameLen).trim();
    
    if(shortNameLen !=  title.length())
    {
      this.ShortName += "…";
    }
    
    this.ExamplesNotes = "";
    this.DicType = DicTypeEnum.UNKNOWN;
    this.ExamplesType = DicExamplesEnum.UNKNOWN;
  }


  /** Lookup the given word. */
  @Override
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    // The list of entries in the dictionary for this word
    List<Entry> dicEntryList = new ArrayList<Entry>();
    List<String> rawEntryList = this.searchEpwingDic(word, EPLKUP_TAG_FLAG_EMPHASIS | EPLKUP_TAG_FLAG_SUB | EPLKUP_TAG_FLAG_SUP);

    for (String rawEntry : rawEntryList)
    {
      if (rawEntry.length() > 0)
      {
        Entry entry = new Entry();
        entry.SourceDic = this;
        entry.Definition = rawEntry.replaceAll("\n", "<br />");
        
        dicEntryList.add(entry);
      }
    }

    if (dicEntryList.size() == 0)
    {
      dicEntryList = null;
    }

    return dicEntryList;
  }


}
