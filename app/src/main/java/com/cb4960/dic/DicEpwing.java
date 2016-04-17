/*******************************************************************************
 * Copyright 2013-2015 Christopher Brochtrup
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract EPWING dictionary base type.
 * 
 * Ported from JGlossator.
 * */
abstract public class DicEpwing extends Dic
{
  final public static int EPLKUP_TAG_FLAG_EMPHASIS = 1;
  final public static int EPLKUP_TAG_FLAG_KEYWORD = 2;
  final public static int EPLKUP_TAG_FLAG_LINK = 4;
  final public static int EPLKUP_TAG_FLAG_SUB = 8;
  final public static int EPLKUP_TAG_FLAG_SUP = 16;
  
  
  /** Path of the eplkup executable */
  private static String eplkup_path = "/data/data/com.cb4960.ocr/eplkup";
  private static String eplkup_path_non_pie = "/data/data/com.cb4960.ocr/eplkup_non_pie";

  /** Path of this dictionary's CATALOGS file. */
  private String catalogsFile = "";
  private int subBookIdx = 0;

  /** Constructor */
  public DicEpwing(String catalogsFile, int subBookIdx)
  {
    this.SourceType = DicSourceTypeEnum.EPWING;
    
    this.catalogsFile = catalogsFile;
    this.subBookIdx = subBookIdx;
  }
  
  
  public String getCatalogsFile()
  {
    return this.catalogsFile;
  }


  public void setCatalogsFile(String catalogsFile)
  {
    this.catalogsFile = catalogsFile;
  }

  
  public static void setEplkupExe(String eplkup)
  {
    eplkup_path = eplkup;
  }

  // eplkup version 1.4 (NDK build) by Christopher Brochtrup.
  //
  // Usage: 
  //   eplkup [--emphasis] [--gaiji] [--help] [--hit #] [--hit-num] [--html-sub] \
  //          [--html-sup] [--keyword] [--link] [--link-in] [--no-header] [--no-text] [--max-hits #] \
  //          [--show-count] [--subbook #] [--title] [--ver] <book-path> <word-to-lookup>
  //
  // Performs an exact search on the provided word in the provided EPWING book.
  //
  //  Required:
  //    book-path      - Directory that contains the EPWING "CATALOG" or "CATALOGS" file.
  //    word-to-lookup - The word to lookup (in UTF-8) or the link when --link-in is set.
  //
  //    Optional:
  //    --emphasis   - Place HTML <em></em> tags around bold/emphasized text.
  //    --gaiji      - 0 = Replace gaiji with no UTF-8 equivalents with a '?' (default).
  //                   1 = Replace gaiji with no UTF-8 equivalents with HTML image tags containing
  //                       embedded base64 image data.
  //    --help       - Show help.
  //    --hit        - Specify which hit to output (starting at 0). If not specified, all hits will be output.
  //    --hit-num    - Output the number of the hit above the hit output (if multiple hits). Ex: {ENTRY: 3}.
  //    --html-sub   - Put HTML <sub></sub> tags around subscript text.
  //    --html-sup   - Put HTML <sup></sup> tags around superscript text.
  //    --keyword    - Put <KEYWORD></KEYWORD> tags around the keyword.
  //    --link       - Put <LINK></LINK[page:offset]> tags around links/references.
  //    --link-in    - The input file contains a link in the format: 'hex_page<whitespace>hex_offset'.
  //    --max-hits   - Specify the number of hits to output when --hit is not specified. Default is 20.
  //    --no-header  - Don't print the headers.
  //    --no-text    - Don't print the text.
  //    --show-count - Output the number of lookup hits in the first line of the output file. Ex. {HITS: 6}
  //    --subbook    - The subbook to use in the EPWING book. Default is 0.
  //    --title      - Get the title of the subbook.
  //    --ver        - Show version.

  /** Run the eplkup tool with the provided inputs. */
  private static List<String> runEplkup(List<String> argsList)
  {
    String lookupText = "";

    String[] epwingArgs = argsList.toArray(new String[argsList.size()]);

    lookupText = UtilsCommon.callExe(epwingArgs);

    lookupText = lookupText.replaceAll("\r\n", "\n");

    List<String> rawEntryList = new ArrayList<String>();

    // Separate out each entry
    if (lookupText.contains("{ENTRY: 0}"))
    {
      String[] entrySplit = lookupText.split("\\{ENTRY: \\d*?\\}");

      for (String entry : entrySplit)
      {
        String entryToAdd = entry.trim();

        if (entryToAdd.length() > 0)
        {
          boolean duplicate = false;

          for (String rawEntry : rawEntryList)
          {
            if (rawEntry == entryToAdd)
            {
              duplicate = true;
              break;
            }
          }

          // Sometimes eplkup returns duplicates, don't add them
          if (!duplicate)
          {
            rawEntryList.add(entryToAdd);
          }
        }
      }
    }
    else
    {
      if (lookupText.trim().length() > 0)
      {
        rawEntryList.add(lookupText);
      }
    }

    return rawEntryList;
  }


  /**
   * Given a word, get list containing the raw text for each entry in the EPWING dictionary. List
   * will not contains duplicate entries or blank lines.
   */
  protected List<String> searchEpwingDic(String word, int tagFlags)
  {
    File file = new File(this.catalogsFile);
    String epwingDir = file.getParent();
    List<String> tagOptions = this.formatTagFlagStr(tagFlags);
    
    if(word.length() > 127)
    {
      word = word.substring(0, 127);
    }

    List<String> eplkupArgs = new ArrayList<String>();
    eplkupArgs.add(eplkup_path);
    eplkupArgs.add("--no-header");
    eplkupArgs.add("--gaiji");
    eplkupArgs.add("1");
    eplkupArgs.add("--hit-num");
    eplkupArgs.addAll(tagOptions);
    eplkupArgs.add("--subbook");
    eplkupArgs.add(Integer.toString(this.subBookIdx));
    eplkupArgs.add(epwingDir);
    eplkupArgs.add(word);

    return runEplkup(eplkupArgs);
  }


  /* Search the EPWING dic with a page and offset. */
  protected List<String> searchEpwingDic(int page, int offset, int tagFlags)
  {
    File file = new File(this.catalogsFile);
    String epwingDir = file.getParent();
    List<String> tagOptions = this.formatTagFlagStr(tagFlags);
    String pageAndOffset = String.format("0x%X 0x%X", page, offset);

    List<String> eplkupArgs = new ArrayList<String>();
    eplkupArgs.add(eplkup_path);
    eplkupArgs.add("--gaiji");
    eplkupArgs.add("1");
    eplkupArgs.add("--link-in");
    eplkupArgs.addAll(tagOptions);
    eplkupArgs.add("--subbook");
    eplkupArgs.add(Integer.toString(this.subBookIdx));
    eplkupArgs.add(epwingDir);
    eplkupArgs.add(pageAndOffset);

    return runEplkup(eplkupArgs);
  }


  /** Format the EPWING tag flags into arguments that can be used by eplkup. */
  private List<String> formatTagFlagStr(int tagFlags)
  {
    List<String> tagList = new ArrayList<String>();

    if ((tagFlags & EPLKUP_TAG_FLAG_EMPHASIS) == EPLKUP_TAG_FLAG_EMPHASIS)
    {
      tagList.add("--emphasis");
    }

    if ((tagFlags & EPLKUP_TAG_FLAG_KEYWORD) == EPLKUP_TAG_FLAG_KEYWORD)
    {
      tagList.add("--keyword");
    }

    if ((tagFlags & EPLKUP_TAG_FLAG_LINK) == EPLKUP_TAG_FLAG_LINK)
    {
      tagList.add("--link");
    }

    if ((tagFlags & EPLKUP_TAG_FLAG_SUB) == EPLKUP_TAG_FLAG_SUB)
    {
      tagList.add("--html-sub");
    }

    if ((tagFlags & EPLKUP_TAG_FLAG_SUP) == EPLKUP_TAG_FLAG_SUP)
    {
      tagList.add("--html-sup");
    }

    return tagList;
  }


  // Sanseido Super Daijirin
  // 『三省堂　スーパー大辞林』
  // Contains: 1) 『大辞林 第2版』 J-J. Contains Japanese-only examples.
  // 2) 『デイリーコンサイス英和辞典 第5版』 J-E. No examples.
  //
  // Dajirin 2nd Edition. J-J. Japanese-only examples.
  // 『大辞林 第2版』
  //
  // Daijisen. J-J. Japanese-only examples.
  // 『大辞泉』
  //
  // Genius EJ 3rd J-E 2nd. Contains example sentences.
  // 『ジーニアス英和〈第３版〉・和英〈第２版〉辞典』
  //
  // Genius EJ-JE. Contains example sentences.
  // 『ジーニアス英和・和英辞典』
  //
  // Kenkyusha 5th J-E. Contains example sentences.
  // 『研究社　新和英大辞典　第５版』
  //
  // Kenkyusha Shin Eiwa-Waei Chujiten J-E. Contains example sentences.
  // 『研究社　新英和・和英中辞典』
  //
  // Kojien 6th Edition. J-J. Japanese-only examples.
  // 『広辞苑第六版』
  //
  // Meikyo Kokugo Dictionary. J-J. Japanese-only examples.
  // 『明鏡国語辞典』

  /**
   * Return appropriate object for supported EPWING dictionaries. Otherwise return null.
   */
  public static DicEpwing createEpwingDic(String catalogsFile)
  {
    DicEpwing epwingDic = null;

    String title = getTitle(catalogsFile);

    if (title.equals("三省堂　スーパー大辞林"))
    {
      // Note: 『三省堂　スーパー大辞林』 also contains the 『デイリーコンサイス英和辞典 第5版』
      // J-E dictionary but it is not currently supported.
      epwingDic = new DicEpwingDaijirin2nd(catalogsFile, 0);
    }
    else if (title.equals("大辞林 第2版"))
    {
      epwingDic = new DicEpwingDaijirin2nd(catalogsFile, 0);
    }
    else if (title.equals("大辞泉"))
    {
      epwingDic = new DicEpwingDaijisen(catalogsFile, 0);
    }
    else if (title.equals("研究社　新和英大辞典　第５版"))
    {
      epwingDic = new DicEpwingKen5th(catalogsFile, 0);
    }
    else if (title.equals("研究社　新英和・和英中辞典"))
    {
      epwingDic = new DicEpwingChujiten(catalogsFile, 0);
    }
    else if (title.equals("広辞苑第六版"))
    {
      epwingDic = new DicEpwingKojien6th(catalogsFile, 0);
    }
    else if (title.equals("明鏡国語辞典"))
    {
      epwingDic = new DicEpwingMeikyo(catalogsFile, 0);
    }
    else
    {
      epwingDic = new DicEpwingRaw(catalogsFile, 0);
    }

    return epwingDic;
  }
  
  
  /** Get the title of the provided EPWING dictionary file */
  public static String getTitle(String catalogsFile)
  {
    String title = "";
    File file = new File(catalogsFile);
    String epwingDir = file.getParent();
    
    List<String> eplkupArgs = new ArrayList<String>();
    eplkupArgs.add(eplkup_path);
    eplkupArgs.add("--title");
    eplkupArgs.add(epwingDir);
    eplkupArgs.add("dummy");

    try
    {
      List<String> eplkupOutList = runEplkup(eplkupArgs);

      if(eplkupOutList.size() > 0)
      {
        title = eplkupOutList.get(0);
      }
    }
    catch(Exception e)
    {
      // Don't care
    }
    
    return title;
  }
  
}
