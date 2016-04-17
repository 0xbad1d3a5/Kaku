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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents 『研究社　新英和・和英中辞典』 (Kenkyusha Shin Eiwa-Waei Chujiten). J-E. Has example sentences.
 * 
 * Ported from JGlossator.
 * */
class DicEpwingChujiten extends DicEpwing
{
  /** Constructor. */
  public DicEpwingChujiten(String catalogsFile, int subBookIdx)
  {
    super(catalogsFile, subBookIdx);

    this.Name = "研究社　新英和・和英中辞典";
    this.NameEng = "Kenkyusha Shin Eiwa-Waei Chujiten";
    this.ShortName = "中辞典";
    this.DicType = DicTypeEnum.JE;
    this.ExamplesType = DicExamplesEnum.YES;
  }


  /** Lookup the given word. */
  @Override
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    // The list of entries in the dictionary for this word
    List<Entry> dicEntryList = new ArrayList<Entry>();
    List<String> rawEntryList = this.searchEpwingDic(word, EPLKUP_TAG_FLAG_LINK | EPLKUP_TAG_FLAG_SUB
        | EPLKUP_TAG_FLAG_SUP);

    for (String rawEntry : rawEntryList)
    {
      Entry entry = new Entry();
      entry.SourceDic = this;

      List<String> lines = new LinkedList<String>(Arrays.asList(rawEntry.split("\n")));

      if (!this.parseFirstLine(lines.get(0), entry))
      {
        continue;
      }

      // Remove the line that was already parsed
      lines.remove(0);

      this.parseBody(lines, entry, includeExamples);

      // Don't use this entry for definition purposes if it doesn't contain alpha characters.
      if (fineTune.JeNoAlphaFallback && !this.containsAlpha(entry.Definition))
      {
        entry.SecondaryDefinition = entry.Definition;
        entry.Definition = "";
      }

      dicEntryList.add(entry);
    }

    if (dicEntryList.size() == 0)
    {
      dicEntryList = null;
    }

    return dicEntryList;
  }


  /** Does the string contains alpha characters after the HTML has been stripped. */
  private boolean containsAlpha(String text)
  {
    String noHtml = text.replaceAll("<.*?>", "");
    return UtilsLang.containsAlpha(noHtml);
  }


  /** Parse the body of the entry. */
  private void parseBody(List<String> lines, Entry entry, boolean includeExamples)
  {
    int subDef = 1;

    String expressionNoSpecialChars = UtilsFormatting.removeSpecialCharsFromExpression(entry.Expression);
    String readingNoSpecialChars = UtilsFormatting.removeSpecialCharsFromExpression(entry.Reading);

    for (String line : lines)
    {
      String defLine = line;

      // If example sentence
      if (defLine.startsWith("◆"))
      {
        SeparateExampleOutput sepExOutput = new SeparateExampleOutput();
        boolean status = this.separateExample(defLine, sepExOutput);

        if (status)
        {
          // Check for examples that are more like definitions (in the tradition of ken5).
          // Example: 年上の in the 年上 entry.
          // .Net: ^(?:{0}|{1})(?<TrailingText>で|する|と|な|に|の)$
          Pattern pattern = Pattern.compile(String.format("^(?:%s|%s)(で|する|と|な|に|の)$", expressionNoSpecialChars,
              readingNoSpecialChars));
          Matcher matcher = pattern.matcher(sepExOutput.jap);

          // If this is a really a definition.
          if (matcher.find())
          {
            String trailingText = matcher.group(1);

            if (trailingText == null)
            {
              trailingText = "";
            }

            trailingText = trailingText.trim();

            entry.Definition += String.format("～%s %s<br />", trailingText, sepExOutput.eng);
          }
          else
          // It's a real example
          {
            Example example = new Example();
            example.DicName = this.Name;
            example.SubDefNumber = subDef;
            example.HasTranslation = true;
            example.Text = String.format("%s\t%s", sepExOutput.jap, sepExOutput.eng);
            example.Text = example.Text.replaceAll("<LINK>", "");
            example.Text = example.Text.replaceAll("</LINK.*?>", "");

            entry.ExampleList.add(example);
          }
        }
      }
      // Else if this is an example sentence link
      else if (defLine.startsWith("<LINK>→✎</LINK"))
      {
        this.getExamplesBehindLink(defLine, entry, subDef);
      }
      // Else if this line is entirely a link. skip it
      else if (defLine.startsWith("<LINK>→</LINK"))
      {
        continue;
      }
      else
      // It is either a definition or a subword
      {
        // .Net: ^(?:(?<SubDefNum>\d*) )?(?<Def>.*)
        Pattern pattern2 = Pattern.compile("^(?:(\\d*) )?(.*)");
        Matcher matcher2 = pattern2.matcher(defLine);

        if (matcher2.find())
        {
          String subDefNumStr = matcher2.group(1);
          String def = matcher2.group(2);

          if (subDefNumStr == null)
          {
            subDefNumStr = "";
          }

          if (def == null)
          {
            def = "";
          }

          subDefNumStr = subDefNumStr.trim();
          def = def.trim();

          // If the definition is a sub definition
          if (subDefNumStr.length() > 0)
          {
            subDef = Integer.parseInt(subDefNumStr);
            entry.Definition += String.format("%s %s<br />", UtilsLang.convertIntegerToCircledNumStr(subDef), def);
          }
          // Else if the definition is not a subword (like 独唱会 in the 独唱 entry);
          else if ((def.length() > 0) && !UtilsLang.containsIdeograph(def.charAt(0)))
          {
            entry.Definition += def + "<br />";
          }
        }
      }
    }

    // Remove the link tags, but keep the link text
    entry.Definition = entry.Definition.replaceAll("<LINK>", "");
    entry.Definition = entry.Definition.replaceAll("</LINK.*?>", "");

    // Remove trailing <br /> from definition
    if (entry.Definition.endsWith("<br />"))
    {
      entry.Definition = entry.Definition.substring(0, entry.Definition.length() - 6);
    }
  }

  private class SeparateExampleOutput
  {
    public String jap = "";
    public String eng = "";
  }


  /**
   * Return the Japanese and English portions of the example sentence. All this because numbers,
   * letters, and spaces can exist in the Japanese part of the definition.
   */
  private boolean separateExample(String example, SeparateExampleOutput output)
  {
    // Example: ◆年を取る grow older; age; 《fml》 get on in years; 《口語》 get on; 〈老人になる〉 get [grow, 《fml》

    output.jap = "";
    output.eng = "";

    // Delete ◆
    example = example.substring(1);

    boolean status = true;
    int lastJapDefIdx = 0;

    boolean inBracket1 = false; // 《...》
    boolean inBracket2 = false; // 〈...〉
    boolean inBracket3 = false; // (...)

    for (int i = example.length() - 1; i >= 0; i--)
    {
      char c = example.charAt(i);

      if (c == '》')
      {
        inBracket1 = true;
      }
      else if (c == '《')
      {
        inBracket1 = false;
      }
      else if (c == '〉')
      {
        inBracket2 = true;
      }
      else if (c == '〈')
      {
        inBracket2 = false;
      }
      else if (c == ')')
      {
        inBracket3 = true;
      }
      else if (c == '(')
      {
        inBracket3 = false;
      }
      else if (!inBracket1 && !inBracket2 && !inBracket3
          && (UtilsLang.containsIdeograph(c) || UtilsLang.containsHiragana(c) || UtilsLang.containsKatakana(c)))
      {
        lastJapDefIdx = i;
        break;
      }
    }

    try
    {
      // If didn't end in a Japanese character as in "◆主要産業 ☛<LINK>→</LINK[28879:1FC]>さんぎょう"
      if (lastJapDefIdx != example.length() - 1)
      {
        output.jap = example.substring(0, lastJapDefIdx + 1);
        output.eng = example.substring(lastJapDefIdx + 2);
      }
    }
    catch (Exception e1)
    {
      // Don't care
    }

    if ((output.jap.length() == 0) || (output.eng.length() == 0))
    {
      status = false;
    }

    return status;
  }


  /** Get the example sentences that are behind a link. */
  private void getExamplesBehindLink(String defLine, Entry entry, int subDef)
  {
    // Example: <LINK>→✎</LINK[2C40F:5CA]>

    // .Net: \[(?<Page>[0-9A-F]*):(?<Offset>[0-9A-F]*)\]
    Pattern pattern = Pattern.compile("\\[([0-9A-F]*):([0-9A-F]*)\\]");
    Matcher matcher = pattern.matcher(defLine);

    if (matcher.find())
    {
      String pageStr = matcher.group(1);
      String offsetStr = matcher.group(2);

      if (pageStr == null)
      {
        return;
      }

      if (offsetStr == null)
      {
        return;
      }

      pageStr = pageStr.trim();
      offsetStr = offsetStr.trim();

      int page = Integer.parseInt(pageStr, 16);
      int offset = Integer.parseInt(offsetStr, 16);

      List<String> lines = this.searchEpwingDic(page, offset, EPLKUP_TAG_FLAG_SUB | EPLKUP_TAG_FLAG_SUP);

      if (lines.size() > 0)
      {
        List<String> splitLines = new LinkedList<String>(Arrays.asList(lines.get(0).split("\n")));

        for (String line : splitLines)
        {
          // .Net: ^(?<Jap>.*?\.) (?<Eng>.*)
          Pattern pattern2 = Pattern.compile("^(.*?\\.) (.*)");
          Matcher matcher2 = pattern2.matcher(line);

          if (matcher2.find())
          {
            String jap = matcher2.group(1);
            String eng = matcher2.group(2);

            if ((jap.length() > 0) && (eng.length() > 0))
            {
              Example example = new Example();
              example.DicName = this.Name;
              example.SubDefNumber = subDef;
              example.HasTranslation = true;
              example.Text = String.format("%s\t%s", jap, eng);
              example.Text = example.Text.replaceAll("<LINK>", "");
              example.Text = example.Text.replaceAll("</LINK.*?>", "");

              entry.ExampleList.add(example);
            }
          }
        }
      }
    }
  }


  /** Parse the first line of an entry.
    * Sets the reading, expression, and maybe definition of the entry.
    * Returns false on error. */
  private boolean parseFirstLine(String line, Entry entry)
  {
    boolean success = true;

    // Remove sup tag
    line = line.replaceAll("<sup>.*?</sup>", "");

    // Cases to handle:
    // 1) どくしょう 独唱
    // 2) 独唱会 a (vocal) recital
    // 3) バー
    // .Net: ^(?<Reading>.*?)(?:(?: (?<Expression>.*))|$)
    Pattern pattern = Pattern.compile("^(.*?)(?:(?: (.*))|$)");
    Matcher matcher = pattern.matcher(line);

    if (matcher.find())
    {
      entry.Reading = matcher.group(1);
      entry.Expression = matcher.group(2);

      if (entry.Reading == null)
      {
        entry.Reading = "";
      }

      if (entry.Expression == null)
      {
        entry.Expression = "";
      }

      entry.Reading = entry.Reading.trim();
      entry.Expression = entry.Expression.trim();

      // Case 3: "バー"
      if (entry.Expression.length() == 0)
      {
        entry.Expression = entry.Reading;
      }
      // Case 2: "独唱会 a (vocal) recital"
      else if (UtilsLang.containsAlpha(entry.Expression))
      {
        entry.Definition = entry.Expression;
        entry.Expression = entry.Reading;
        entry.Reading = "";
      }
    }
    else
    {
      success = false;
    }

    return success;
  }

}
