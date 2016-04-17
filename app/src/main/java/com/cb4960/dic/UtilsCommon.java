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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UtilsCommon
{
  /** Get the first line of a multi-lined text. */
  static public String getFirstLine(String text)
  {
    String firstLine = "";

    if (text.trim().contains("\n"))
    {
      firstLine = text.substring(0, text.indexOf('\n', 0));
    }
    else
    {
      firstLine = text;
    }

    return firstLine;
  }
  
  
  /** Call the provided exe and get it's stdout. */
  public static String callExe(String[] exeArgs)
  {
    String outText = "";
    
    try
    {
      Process process = Runtime.getRuntime().exec(exeArgs);

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      int read;
      char[] buffer = new char[60000];
      StringBuffer output = new StringBuffer();
      
      while ((read = reader.read(buffer)) > 0)
      {
        output.append(buffer, 0, read);
      }
      
      reader.close();

      process.waitFor();
      
      outText = output.toString();
    }
    catch (IOException e)
    {
      // throw new RuntimeException(e);
    }
    catch (InterruptedException e)
    {
      // throw new RuntimeException(e);
    }
    
    return outText;
  }
  
  /**
  /// Separate the expressions and readings and create a list of combinations.
  /// Examples:
  /// 
  /// Given: 夕飯   ゆうはん、 ゆうめし
  /// Return list:
  ///   夕飯   ゆうはん
  ///   夕飯   ゆうめし
  ///   
  /// Given: 足･脚   あし
  /// Return list:
  ///   足     あし
  ///   脚     あし
  ///   
  ///*/
  public static List<Entry> getExpressionReadingCombinations(String expression, String reading)
  {
    List<Entry> comboList = new ArrayList<Entry>();
    String[] readings = reading.split("[,、]");

    String expressionSplitBy = "[、（";

    // Don't split with "," if expression contains img
    if (!expression.contains("<img"))
    {
      expressionSplitBy += ",";
    }
  
    // If not something like "ア・ラ・カルト", treat the dot (・) as an expression separator too
    if (!UtilsLang.containsOnlyKatakana(expression.replace("・", "")))
    {
      expressionSplitBy += "・";
    }

    expressionSplitBy += "]";

    String[] expressions = expression.split(expressionSplitBy);

    if ((expression == "") && (reading == ""))
    {

    }
    else if (expression == "")
    {
      for (String readingField : readings)
      {
        Entry entry = new Entry();
        entry.Reading = UtilsFormatting.removeSpecialCharsFromReading(readingField).trim();

        comboList.add(entry);
      }
    }
    else if (reading == "")
    {
      for (String expressionField : expressions)
      {
        Entry entry = new Entry();
        entry.Expression = UtilsFormatting.removeSpecialCharsFromExpression(expressionField).trim();

        comboList.add(entry);
      }
    }
    else
    {
      for (String expressionField : expressions)
      {
        for (String readingField : readings)
        {
          Entry entry = new Entry();
          entry.Expression = UtilsFormatting.removeSpecialCharsFromExpression(expressionField).trim();
          entry.Reading = UtilsFormatting.removeSpecialCharsFromReading(readingField).trim();

          // If the expression contains only special chars, prevent a blank expression
          if (entry.Expression == "")
          {
            entry.Expression = expressionField;
          }

          // Don't include bad combos. Example: for 押し込む, 押し込める   おしこむ、 おしこめる, 
          // don't include 押し込む  おしこめる or 押し込める  おしこむ
          char lastCharExpression = entry.Expression.charAt(entry.Expression.length() - 1);
          char lastCharReading = entry.Reading.charAt(entry.Reading.length() - 1);

          if (!UtilsLang.containsKana(lastCharExpression) || (lastCharExpression == lastCharReading))
          {
            comboList.add(entry);
          }
        }
      }
    }

    return comboList;
  }
}
