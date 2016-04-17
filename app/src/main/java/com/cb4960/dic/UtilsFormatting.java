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


/** Ported from JGlossator.  */
public class UtilsFormatting
{

  /** Add punctuation to the end of a some Japanese text (if it doesn't already have any). */
  public static String addPunctuationToJapText(String text)
  {
    String newText = text.trim();

    if (newText.length() > 0)
    {
      char lastChar = newText.charAt(newText.length() - 1);

      if (lastChar == '.')
      {
        // To be consistent, replace . with 。
        newText = newText.substring(0, newText.length() - 1) + "。";
      }
      else if ((lastChar != '。') && (lastChar != '！') && (lastChar != '？')
        && (lastChar != '!') && (lastChar != '?') && (lastChar != '…'))
      {
        newText += "。";
      }
    }

    return newText;
  }


/** Capitalize the first character of English text and add a period to the end. */
public static String addPunctuationToEngText(String text)
{
  String newText = text.trim();

  if (newText.length() > 0)
  {
    newText = newText.toUpperCase().charAt(0) + newText.substring(1);

    char lastChar = newText.charAt(newText.length() - 1);

    if ((lastChar != '.') && (lastChar != '!') 
      && (lastChar != '?') && (lastChar != '…'))
    {
      newText += ".";
    }
  }

  return newText;
}


/** Remove special characters from an expression.
  * ×猪突   --->   猪突 */
public static String removeSpecialCharsFromExpression(String expression)
{
  expression = expression.replaceAll("<.*?>", "");
  expression = expression.replaceAll("\\(.*?\\)", "");
  expression = expression.replaceAll("\\（.*?\\）", "");
  expression = expression.replaceAll("[\\-‐・▽▼△×《》〈〉（）]", "");

  return expression;
}


/** Remove special characters from a reading.
  * す‐てき    --->   すてき  */
public static String removeSpecialCharsFromReading(String reading)
{
  return reading.replaceAll("[\\-‐・]", "");
}


/// <summary>
/// Remove ruby from string.
/// 足《あし》 -->  足.
/// </summary>
//public static string removeRuby(string text)
//{
//  return Regex.Replace(text, "《.*?》", "", RegexOptions.None)
//    .Replace("《", "").Replace("》", "");
//}



}

