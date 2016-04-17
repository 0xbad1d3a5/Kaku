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
 * Represents 『研究社　新和英大辞典　第５版』 (Kenkyusha New Japanese-English Dictionary 5th Edition). J-E. Has
 * example sentences.
 * 
 * Ported from JGlossator.
 */
public class DicEpwingKen5th extends DicEpwing
{
  /** Constructor. */
  public DicEpwingKen5th(String catalogsFile, int subBookIdx)
  {
    super(catalogsFile, subBookIdx);

    this.Name = "研究社　新和英大辞典　第５版";
    this.NameEng = "Kenkyusha New Japanese-English Dictionary 5th Edition";
    this.ShortName = "研究社５";
    this.DicType = DicTypeEnum.JE;
    this.ExamplesType = DicExamplesEnum.YES;
  }


  /** Lookup the given word. */
  @Override
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    // The list of entries in the dictionary for this word
    List<Entry> dicEntryList = new ArrayList<Entry>();
    List<String> rawEntryList = this.searchEpwingDic(word, EPLKUP_TAG_FLAG_SUB | EPLKUP_TAG_FLAG_SUP);

    for (String rawEntry : rawEntryList)
    {
      Entry entry = new Entry();
      entry.SourceDic = this;

      List<String> lines = new LinkedList<String>(Arrays.asList(rawEntry.split("\n")));
      
      if (!this.parseFirstLine(lines.get(0), entry))
      {
        // Unexpected format or something like 悪そう which has no def or reading
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
    boolean subDefNumFound = false;
    int subDef = 1;
    boolean defFound = false;
    List<String> subDefList = new ArrayList<String>();

    // .Net: @"^((?<Tilde>〜)|(?<Example>[▲・])|(?<Def>[0-9]+))?(?<Text>.*)"
    Pattern pattern = Pattern.compile("^((〜)|([▲・])|([0-9]+))?(.*)");

    for (String line : lines)
    {
      Matcher matcher = pattern.matcher(line);

      if (matcher.find())
      {
        String prefixTilde = matcher.group(2);
        String prefixExample = matcher.group(3);
        String prefixDef = matcher.group(4);
        String text = matcher.group(5);

        if (prefixTilde == null)
        {
          prefixTilde = "";
        }

        if (prefixExample == null)
        {
          prefixExample = "";
        }

        if (prefixDef == null)
        {
          prefixDef = "";
        }

        if (text == null)
        {
          text = "";
        }

        prefixTilde = prefixTilde.trim();
        prefixExample = prefixExample.trim();
        prefixDef = prefixDef.trim();
        text = text.trim();

        if (prefixTilde.length() > 0)
        {
          subDefList.add(line.trim());
        }
        else if (prefixExample.length() > 0)
        {
          if (includeExamples)
          {
            Example example = new Example();
            example.DicName = this.Name;
            example.Text = line.substring(1).trim();
            example.SubDefNumber = subDef;
            example.HasTranslation = true;

            // Make sure that the sentence contains a translation
            if (UtilsLang.containsAlpha(example.Text))
            {
              // Replace Japanese space with a tab to separate the Japanese Example and Translation
              example.Text = example.Text.replaceAll("　", "\t");

              entry.ExampleList.add(example);
            }
          }
        }
        else
        {
          if (prefixDef.length() > 0)
          {
            subDefNumFound = true;
            subDef = Integer.parseInt(prefixDef);
          }
          else if (defFound)
          {
            // Stop if sub word is encountered (see 悪い or 美しい for example)
            break;
          }

          // Case where entry has no definition, but does have example sentences
          if ((entry.ExampleList.size() >= 1) && !defFound)
          {
          }
          else
          {
            defFound = true;

            if (subDefNumFound)
            {
              String circledNum = UtilsLang.convertIntegerToCircledNumStr(subDef);
              subDefList.add(circledNum + " " + text);
            }
            else
            {
              // Add to the definition if doesn't start with ◨ or ◧ to prevent
              // cases like "公" returning "◧公沙汰(ざた) ..." for the definition.
              if (!text.startsWith("◨") && !text.startsWith("◧"))
              {
                subDefList.add(prefixDef + text);
              }
            }
          }
        }
      }
    }

    int count = 1;

    // Create the definition text
    for (String def : subDefList)
    {
      if (count == subDefList.size())
      {
        entry.Definition += def;
      }
      else
      {
        entry.Definition += def + "<br />";
      }

      count++;
    }

    // Replace ugly tilde with nicer tilde
    entry.Definition = entry.Definition.replaceAll("〜", "～");

    // If the definition is blank, the example sentences cannot have a sub definition
    if (entry.Definition.length() == 0)
    {
      for (Example example : entry.ExampleList)
      {
        example.SubDefNumber = Example.NO_SUB_DEF;
      }
    }
  }


  /**
   * Parse the first line of an entry. Sets the reading, expression, and maybe definition of the
   * entry. Returns false on error.
   */
  private boolean parseFirstLine(String line, Entry entry)
  {
    boolean success = true;

    // Sometimes eblib returns a bogus hit, ignore it
    if (line.startsWith("・") || line.startsWith("▲"))
    {
      return false;
    }

    // Remove sup tag
    line = line.replaceAll("<sup>.*?</sup>", "");

    // Case 1: "うつくしい【美しい】 ﾛｰﾏ(utsukushii)"
    // .Net: ^(?<Reading>.*?)【(?<Expression>.*?)】 ﾛｰﾏ\(.*?\)$
    Pattern patternC1 = Pattern.compile("^(.*?)【(.*?)】 ﾛｰﾏ\\(.*?\\)$");
    Matcher matcherC1 = patternC1.matcher(line);

    if (matcherC1.find())
    {
      entry.Reading = matcherC1.group(1);
      entry.Expression = matcherC1.group(2);

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
    }
    else
    {
      // Case 2: "スキーマ ﾛｰﾏ(sukīma)"
      // .Net ^(?<Expression>.*?) ﾛｰﾏ\(.*?\)$
      Pattern patternC2 = Pattern.compile("^(.*?) ﾛｰﾏ\\(.*?\\)$");
      Matcher matcherC2 = patternC2.matcher(line);

      if (matcherC2.find())
      {
        entry.Expression = matcherC2.group(1);

        if (entry.Expression == null)
        {
          entry.Expression = "";
        }

        entry.Expression = entry.Expression.trim();

        entry.Reading = entry.Expression;
      }
      else
      {
        // Case 3:
        // "隙間産業　a niche industry; a niche business."
        // "◧侵襲期　the stage of invasion."
        // .Net: ^[◨◧]?(?<Expression>.*?)　(?<Definition>.*)$
        Pattern patternC3 = Pattern.compile("^[◨◧]?(.*?)　(.*)$");
        Matcher matcherC3 = patternC3.matcher(line);

        if (matcherC3.find())
        {
          entry.Expression = matcherC3.group(1);
          entry.Definition = matcherC3.group(2);

          if (entry.Expression == null)
          {
            entry.Expression = "";
          }

          if (entry.Definition == null)
          {
            entry.Definition = "";
          }

          entry.Expression = entry.Expression.trim();
          entry.Definition = entry.Definition.trim();

          // If the expression is kana only
          if (!UtilsLang.containsIdeograph(entry.Expression))
          {
            entry.Reading = entry.Expression;
          }
          else
          {
            // This case has no reading
          }
        }
        else
        {
          success = false;
        }
      }
    }

    return success;
  }

}
