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


/** Language-related utilities.
 *  
 *  Ported from JGlossator. */
public class UtilsLang
{
  /** Does this string contain any hiragana? */
  public static boolean containsHiragana(char c)
  {
    return (c >= '\u3040') && (c <= '\u309F');
  }
  
  
  /** Does this string contain any katakana? */
  public static boolean containsKatakana(char c)
  {
    return (c >= '\u30A0') && (c <= '\u30FF');
  }
  
  /** Does this string contain any kana? */
  public static boolean containsKana(char c)
  {
    return (UtilsLang.containsHiragana(c) || UtilsLang.containsKatakana(c));
  }
  
  
  /** Does this string contain an ideograph (like a kanji)? */
  public static boolean containsIdeograph(String expression)
  {
    boolean retVal = false;
    
    for(int i = 0; i < expression.length(); i++)
    {
      char c = expression.charAt(i);
      
      if((c >= '\u4E00') && (c <= '\u9FFF'))
      {
        retVal = true;
        break;
      }
    }
    
    return retVal;
  }
  
  public static boolean containsIdeograph(char c)
  {
    return (c >= '\u4E00') && (c <= '\u9FFF');
  }

  
  /** Does this string contain only katakana? */
  public static boolean containsOnlyKatakana(String str)
  {
    boolean retVal = true;
    
    for(int i = 0; i < str.length(); i++)
    {
      char c = str.charAt(i);
      
      if(!UtilsLang.containsKatakana(c))
      {
        retVal = false;
        break;
      }
    }
    
    return retVal;
  }
  
  /** Does this string contain an alpha character? */
  public static boolean containsAlpha(String str)
  {
    boolean retVal = false;
    
    for(int i = 0; i < str.length(); i++)
    {
      char c = str.charAt(i);
      
      if(((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')))
      {
        retVal = true;
        break;
      }
    }
    
    return retVal;
  }
  
  
  public static boolean containsAlpha(char c)
  {
    return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
  }
  

  private static int[] kanaHalf = 
  { 
    0x3092, 0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x3083, 0x3085, 
    0x3087, 0x3063, 0x30FC, 0x3042, 0x3044, 0x3046, 0x3048, 0x304A,
    0x304B, 0x304D, 0x304F, 0x3051, 0x3053, 0x3055, 0x3057, 0x3059,
    0x305B, 0x305D, 0x305F, 0x3061, 0x3064, 0x3066, 0x3068, 0x306A,
    0x306B, 0x306C, 0x306D, 0x306E, 0x306F, 0x3072, 0x3075, 0x3078,
    0x307B, 0x307E, 0x307F, 0x3080, 0x3081, 0x3082, 0x3084, 0x3086,
    0x3088, 0x3089, 0x308A, 0x308B, 0x308C, 0x308D, 0x308F, 0x3093
  };

  private static int[] kanaVoiced = 
  {
    0x30F4, 0xFF74, 0xFF75, 0x304C, 0x304E, 0x3050, 0x3052, 0x3054,
    0x3056, 0x3058, 0x305A, 0x305C, 0x305E, 0x3060, 0x3062, 0x3065,
    0x3067, 0x3069, 0xFF85, 0xFF86, 0xFF87, 0xFF88, 0xFF89, 0x3070,
    0x3073, 0x3076, 0x3079, 0x307C
  };
  
  private static int[] kanaSemiVoiced = { 0x3071, 0x3074, 0x3077, 0x307A, 0x307D };
  
  
  /** Convert half and full-width katakana to hiragana.
   * Note: Katakana 'vu' is never converted to hiragana.
   * This was routine adapted from Yomichan/Rikaichan. */
  public static String convertKatakanaToHiragana(String word)
  {
    StringBuilder result = new StringBuilder(35);

    int ordPrev = 0;

    for (int i = 0; i < word.length(); i++)
    {
      char theChar = word.charAt(i);
      int ordCurr = (int)theChar;

      //if (ordCurr <= 0x3000)
      //{
      //  // Break upon hitting non-japanese characters
      //  break;
      //}

      if ((ordCurr >= 0x30A1) && (ordCurr <= 0x30F3))
      {
        // Full-width katakana to hiragana
        ordCurr -= 0x60;
      }
      else if ((ordCurr >= 0xFF66) && (ordCurr <= 0xFF9D))
      {
        // Half-width katakana to hiragana
        ordCurr = kanaHalf[ordCurr - 0xFF66];
      }
      else if (ordCurr == 0xFF9E)
      {
        // Voiced (used in half-width katakana) to hiragana
        if ((ordPrev >= 0xFF73) && (ordPrev <= 0xFF8E))
        {
          result.setLength(result.length() - 1);
          ordCurr = kanaVoiced[ordPrev - 0xFF73];
        }
      }
      else if (ordCurr == 0xFF9F)
      {
        // Semi-voiced (used in half-width katakana) to hiragana
        if ((ordPrev >= 0xFF8A) && (ordPrev <= 0xFF8E))
        {
          result.setLength(result.length() - 1);
          ordCurr = kanaSemiVoiced[ordPrev - 0xFF8A];
        }
      }
      else if (ordCurr == 0xFF5E)
      {
        // Ignore Japanese ~
        ordPrev = 0;
        continue;
      }

      result.append((char)ordCurr);
      ordPrev = (int)theChar;
    }

    return result.toString();
  }
  

  /** Converts a Japanese number to an integer.
    * ５ --> 5, １２ --> 12, etc. */
  public static int convertJapNumToInteger(String japNum)
  {
    int num = 0;
    String numStr = "";

    for (int i = 0; i < japNum.length(); i++)
    {
      char c = japNum.charAt(i);

      if ((c >= '０') && (c <= '９'))
      {
        int convertedNum = (c - '０');
        numStr += String.valueOf(convertedNum);
      }
    }

    try
    {
      num = Integer.parseInt(numStr);
    }
    catch(Exception e1)
    {
      // Don't care
    }

    return num;
  }
  
  
  /** Convert an integer to a circled number string:
   * 1 --> ①, 2 --> ②.
   * Range: [0, 50].
   * If out of range, will return num surrounded by parens:
   * 51 --> (51) */
  public static String convertIntegerToCircledNumStr(int num)
  {
    String circledNumStr = "(" + num + ")";

    if (num == 0)
    {
      circledNumStr = "⓪";
    }
    else if ((num >= 1) && (num <= 20))
    {
      circledNumStr = Character.toString(((char)(('①' - 1) + num)));
    }
    // Note: The default Android font does not support over 20
    else if ((num >= 21) && (num <= 35))
    {
      circledNumStr = Character.toString(((char)(('㉑' - 21) + num)));
    }
    else if ((num >= 36) && (num <= 50))
    {
      circledNumStr = Character.toString(((char)(('㊱' - 36) + num)));
    }

    return circledNumStr;
  }
  
  
  /** Converter circled number to an integer.
   * ① --> 1, ② --> 2.
   * Range: [⓪,㊿]
   * If out of range, -1 will be returned;
   * */
  public static int convertCircledNumToInteger(char circledNum)
  {
    int num = -1;

    if (circledNum == '⓪')
    {
      num = 0;
    }
    else if ((circledNum >= '①') && (circledNum <= '⑳'))
    {
      num = circledNum - '①' + 1;
    }
    else if ((circledNum >= '㉑') && (circledNum <= '㉟'))
    {
      num = circledNum - '㉑' + 21;
    }
    else if ((circledNum >= '㊱') && (circledNum <= '㊿'))
    {
      num = circledNum - '㊱' + 36;
    }

    return num;
  }


  /** Convert a Japanese letter to it's ASCII equivalent.
    * Ａ --> A */
  public static char convertJapAlphaToAscii(char japAlpha)
  {
    char alpha = '0';

    if ((japAlpha >= 'Ａ') && (japAlpha <= 'Ｚ'))
    {
      alpha = (char)(japAlpha - 'Ａ' + 'A');
    }
    else if ((japAlpha >= 'ａ') && (japAlpha <= 'ｚ'))
    {
      alpha = (char)(japAlpha - 'ａ' + 'a');
    }

    return alpha;
  }





  
  
    
}
