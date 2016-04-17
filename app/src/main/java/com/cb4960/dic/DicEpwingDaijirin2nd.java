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
 * Represents 『大辞林 第2版』 (Daijirin 2nd Edition). J-J. Japanese-only example sentences.
 * 
 * Ported from JGlossator.
 */
public class DicEpwingDaijirin2nd extends DicEpwing
{
  /** Constructor. */
  public DicEpwingDaijirin2nd(String catalogsFile, int subBookIdx)
  {
    super(catalogsFile, subBookIdx);

    this.Name = "大辞林 第2版";
    this.NameEng = "Daijirin 2nd Edition";
    this.ShortName = "大辞林2";
    this.ExamplesNotes = "Japanese only";
    this.DicType = DicTypeEnum.JJ;
    this.ExamplesType = DicExamplesEnum.J_ONLY;
  }


  /** Lookup the given word. */
  @Override
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    // The list of entries in the dictionary for this word
    List<Entry> dicEntryList = new ArrayList<Entry>();
    List<String> rawEntryList = this.searchEpwingDic(word, EPLKUP_TAG_FLAG_LINK | EPLKUP_TAG_FLAG_KEYWORD);

    for (String rawEntry : rawEntryList)
    {
      if (rawEntry.length() > 0)
      {
        if (this.isEntryTextFromDaijinJJ2nd(rawEntry))
        {
          Entry entry = new Entry();
          entry.SourceDic = this;

          List<String> lines = new LinkedList<String>(Arrays.asList(rawEntry.split("\n")));

          if (!this.parseFirstLine(lines.get(0), entry))
          {
            // Unexpected format
            continue;
          }

          // If user want to remove special characters from the reading, do it
          if (fineTune.JjRemoveSpecialReadingChars)
          {
            entry.Reading = entry.Reading.replaceAll("-", "");
            entry.Reading = entry.Reading.replaceAll("・", "");
          }

          // Remove the line that was already parsed
          lines.remove(0);

          this.parseBody(lines, entry, includeExamples, fineTune);

          dicEntryList.add(entry);
        }
      }
    }

    if (dicEntryList.size() == 0)
    {
      dicEntryList = null;
    }

    return dicEntryList;
  }


  /**
   * Is the entry text from 『大辞林 第2版』 (Daijirin 2nd Edition)? Otherwise, it might be from
   * 『デイリーコンサイス英和辞典 第5版』 (Daily Concise Japanese-English Dictionary 5th Edition).
   */
  private boolean isEntryTextFromDaijinJJ2nd(String entryText)
  {
    boolean isJJ = true;

    if (entryText.length() == 0)
    {
      return false;
    }

    //
    // Try quick tests first and than move toward more lengthy tests.
    //

    // Many 『デイリーコンサイス英和辞典 第5版』contain this link
    if (entryText.contains("→英和"))
    {
      isJJ = false;
    }
    else
    {
      String firstLine = UtilsCommon.getFirstLine(entryText);
      String firstLineNoKeyword = firstLine.replaceAll("<KEYWORD>.*?</KEYWORD>", "").trim();

      // If the first lines is blank after removing the keyword and doesn't contain '['
      // Example: "<KEYWORD>あじわう【味わう】</KEYWORD>"
      if ((firstLineNoKeyword.length() == 0) && !firstLine.contains("["))
      {
        // Is this point, for most words it is probably a J-E.
        // Some exceptions like one of the J-J "に" entries will also get to this point though
        isJJ = false;

        // The following tests will try to disprove that the entry is J-E.

        // 『デイリーコンサイス英和辞典 第5版』 will not contain an '-' except as maybe the first character
        if ((firstLine.charAt(0) != '-') && firstLine.contains("-"))
        {
          isJJ = true;
        }
        else
        {
          String body = entryText.substring(firstLine.length()).trim();
          body = body.replaceAll("<LINK>", "");
          body = body.replaceAll("</LINK.*?>", "");

          int alphaCount = 0;
          int nonAlphaCount = 0;

          for (int i = 0; i < body.length(); i++)
          {
            char c = body.charAt(i);

            if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || (c == ' ') || (c == '.') || (c == '\''))
            {
              alphaCount++;
            }
            else
            {
              nonAlphaCount++;
            }
          }

          // If there are more non-alpha than alpha characters, is is probably a J-J.
          if (nonAlphaCount > alphaCount)
          {
            isJJ = true;
          }
        }
      }
    }

    return isJJ;
  }


  /** Parse the body of the entry. */
  private void parseBody(List<String> lines, Entry entry, boolean includeExamples, FineTune fineTune)
  {
    int subDef = 1;

    for (String line : lines)
    {
      String defLine = line;

      Pattern pattern = Pattern.compile("^（(\\d.*?)）");
      Matcher matcher = pattern.matcher(defLine);

      if (matcher.find())
      {
        try
        {
          String subDefStr = matcher.group(1);

          if (subDefStr == null)
          {
            subDefStr = "";
          }

          subDefStr = subDefStr.trim();

          subDef = UtilsLang.convertJapNumToInteger(subDefStr);

          // Convert subdef number in definition to circled number
          defLine = defLine.replaceAll("^（(\\d.*?)）", "");
          defLine = UtilsLang.convertIntegerToCircledNumStr(subDef) + " " + defLine;
        }
        catch (Exception e1)
        {
          // Don't care
        }
      }
      else
      {
        subDef = Example.NO_SUB_DEF;
      }

      // Remove the links from a line if the line doesn't contain other text and is not the only
      // line.
      defLine = removeLinks(defLine, (lines.size() == 1));

      // Get the examples sentences
      List<String> exampleList = new ArrayList<String>();
      String defNoExamples = this.parseExamples(defLine, exampleList, entry.Expression,
          fineTune.JjFillInExampleBlanksWithWord);

      // Remove example sentences from definition unless user says otherwise
      if (!fineTune.JjKeepExamplesInDef)
      {
        defLine = defNoExamples;
      }

      for (String exampleText : exampleList)
      {
        Example example = new Example();
        example.Text = exampleText.trim();

        if (example.Text.length() > 0)
        {
          example.DicName = this.Name;
          example.SubDefNumber = subDef;
          example.HasTranslation = false;
          entry.ExampleList.add(example);
        }
      }

      // Add English space after fullstop to allow wrapping of long lines.
      defLine = defLine.replaceAll("。", "。 ");

      // After removing the link, make sure that the line is not blank.
      if (defLine.trim().length() > 0)
      {
        defLine += "<br />";
        entry.Definition += defLine.trim();
      }
    }

    // Remove trailing <br />
    if (entry.Definition.endsWith("<br />"))
    {
      entry.Definition = entry.Definition.substring(0, entry.Definition.length() - 6);
    }
  }


  /** Remove the links from a line if the line doesn't contain other text and is not the only line. */
  private String removeLinks(String line, boolean onlyLine)
  {
    String defLine = line;
    String noLinkDef = defLine.replaceAll("<LINK>.*?</LINK.*?>", "").trim();

    // If the line would be blank after removing the links
    // and this was not the only line in the entry, remove the link text
    if ((noLinkDef.length() == 0) && !onlyLine)
    {
      // Remove the links
      defLine = noLinkDef;
    }
    else
    {
      // Remove the link tags, but keep the link text
      defLine = defLine.replaceAll("<LINK>", "");
      defLine = defLine.replaceAll("</LINK.*?>", "");
    }

    return defLine;
  }


  /**
   * Parse the examples from a line. Added to exampleList and returns the original line without the
   * examples.
   */
  public String parseExamples(String line, List<String> exampleList, String expression, boolean fillInBlanks)
  {
    // Match match;
    String noExamplesLine = line;
    int failsafe = 0;

    // Samples:
    // 1) 「not_ex」def。def。source1「ex1」「ex2」「ex3」
    // 2) 「not_ex」def。def。source1「ex1」「ex2」「ex3」→trailing。
    // .Net: @"(?<Example>(?<Source>[^。」]*?)「[^」]*?」。?)(?<Trailing>[↔→].*?)?$"
    Pattern pattern = Pattern.compile("(([^。」]*?)「[^」]*?」。?)([↔→].*?)?$");

    // Keep applying regex to get last example in line until no more examples are present.
    while (true)
    {
      Matcher matcher = pattern.matcher(noExamplesLine);

      if (matcher.find())
      {
        failsafe++;

        String example = matcher.group(1);
        String source = matcher.group(2);
        String trailing = matcher.group(3);

        if (example == null)
        {
          example = "";
        }

        if (source == null)
        {
          source = "";
        }

        if (trailing == null)
        {
          trailing = "";
        }

        example = example.trim();
        source = source.trim();
        trailing = trailing.trim();

        // Just in case
        if ((example.length() == 0) || (failsafe > 200))
        {
          break;
        }

        // Remove the example sentence, but leave in the trailing text
        noExamplesLine = noExamplesLine.substring(0, noExamplesLine.length() - example.length() - trailing.length())
            + noExamplesLine.substring(noExamplesLine.length() - trailing.length());
        noExamplesLine = noExamplesLine.trim();

        // Remove the source text
        if (source.length() > 0)
        {
          example = example.substring(source.length());
        }

        // Remove '「' and '」'
        example = example.replaceAll("「", "").replaceAll("」", "");

        // Handle blank part of example
        example = this.processExampleBlanks(example, expression, fillInBlanks);

        exampleList.add(example);
      }
      else
      {
        break;
      }
    }

    return noExamplesLine;
  }


  /**
   * If user has "Fill in example sentence blanks with expression" checked, 無罪を―する ---> ▲無罪を確信する
   * Otherwise, 無罪を―する ---> ▲無罪を___する
   */
  private String processExampleBlanks(String example, String expression, boolean fillInBlanks)
  {
    String newExample = example;

    // If user wants to fill in the blanks with the expression
    if (fillInBlanks)
    {
      String unformattedExpression = expression.replaceAll("・", "|");
      unformattedExpression = UtilsFormatting.removeSpecialCharsFromExpression(unformattedExpression);

      // Only use the first expression (零す・溢す ---> 零す)
      if (unformattedExpression.contains("|"))
      {
        try
        {
          unformattedExpression = unformattedExpression.substring(0, unformattedExpression.indexOf("|"));
        }
        catch (Exception e1)
        {
          // Don't care
        }
      }

      newExample = newExample.replaceAll("―", unformattedExpression);
    }
    else
    // Make expression placeholder more obvious
    {
      newExample = newExample.replaceAll("―", "___");
    }

    return newExample;
  }


  /**
   * Parse the first line of an entry. Sets the reading and expression of the entry. Returns false
   * on error.
   */
  private boolean parseFirstLine(String line, Entry entry)
  {
    boolean success = true;

    // Three cases to handle:
    // 1) <KEYWORD>きょう-はく</KEYWORD> キヤウ― [0] 【強迫】 （名）スル
    // 2) <KEYWORD>バー</KEYWORD> [1] 〖bar〗
    // 3) <KEYWORD>ぱあ</KEYWORD> [1]
    // .Net: ^<KEYWORD>(?<Reading>.*?)</KEYWORD>
    Pattern pattern = Pattern.compile("^<KEYWORD>(.*?)</KEYWORD>");
    Matcher matcher = pattern.matcher(line);

    if (matcher.find())
    {
      entry.Reading = matcher.group(1);

      if (entry.Reading == null)
      {
        entry.Reading = "";
      }

      entry.Reading = entry.Reading.trim();

      // .Net: 【(?<Expression>.*?)】
      Pattern pattern2 = Pattern.compile("【(.*?)】");
      Matcher matcher2 = pattern2.matcher(line);

      if (matcher2.find())
      {
        entry.Expression = matcher2.group(1);

        if (entry.Expression == null)
        {
          entry.Expression = "";
        }

        entry.Expression = entry.Expression.trim();
      }

      // If this entry does not have an expression
      if (entry.Expression.length() == 0)
      {
        entry.Expression = entry.Reading.replaceAll("-", "");
      }
    }
    else
    {
      success = false;
    }

    return success;
  }

}
