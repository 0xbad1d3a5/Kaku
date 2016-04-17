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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Used to find all possible inflections for a word.
 * 
 *  The algorithms found in this class are based on code from Rikaichan.
 *  http://www.polarcloud.com/rikaichan/ 
 *  
 *  Ported from JGlossator.
 *  */
public class Deinflector
{
  /** Textual list of inflection types (passive, position, past, etc.) */
  private List<String> reasons = null;
  
  /** List of inflection endings grouped by length */
  private List<InfoRuleGroup> ruleGroupList = null;
  
  
  /** Constructor. */
  public Deinflector()
  {

  }
 
  
  /** Read the file that contains the deinflection rules and store its contents. */
  public boolean loadRulesFile(String deinflectionRulesFile)
  {    
    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(deinflectionRulesFile), "UTF8"));
  
      InfoRuleGroup ruleGroup = new InfoRuleGroup();
      ruleGroup.FromLen = -1;
  
      reasons = new ArrayList<String>();
      ruleGroupList = new ArrayList<InfoRuleGroup>();
  
      // Skip header line
      reader.readLine();
  
      String line = "";
  
      while ((line = reader.readLine()) != null)
      {
        String[] fields = line.split("\t");
  
        // Reason or Rule?
        if (fields.length == 1)
        {
          reasons.add(fields[0].trim());
        }
        else if (fields.length == 4)
        {
          InfoRule rule = new InfoRule(
            fields[0].trim(),
            fields[1].trim(),
            Integer.parseInt(fields[2].trim()),
            reasons.get(Integer.parseInt(fields[3].trim())));
            
          // Store rules of common character length in separate groups
          if (ruleGroup.FromLen != rule.From.length())
          {
            ruleGroup = new InfoRuleGroup();
            ruleGroup.FromLen = rule.From.length();
            ruleGroupList.add(ruleGroup);
          }
  
          ruleGroup.Rules.add(rule);
        }
      }
  
      reader.close();
    }
    catch(Exception e)
    {
      return false;
    }
    
    return true;
  }
  
  
  
  /** Give list of possible deinflections for the provided word.
   *  It does NOT guarantee that each deinflection is an actual word though.
   *  In fact, most will probably NOT be actual words.
   * 
   *  For example, searching for 盛り上�?��?��?� would result in:
   *    1) 盛り上�?��?��?�                                                Original word
   *    2) 盛り上�?��?�      -te          Not an actual word in the dictionary
   *    3) 盛り上�?��??      -te          Not an actual word in the dictionary    
   *    4) 盛り上�?��?�      -te          Not an actual word in the dictionary
   *    5) 盛り上�?�る      -te          Actual word (this is the one that the caller should use)
   *    6) 盛り上�?��?��?�    imperative   Not an actual word in the dictionary
   *    7) 盛り上�?��?��?�る  masu stem    Not an actual word in the dictionary
   *    8 )盛り上�?��?�る    -te          Not an actual word in the dictionary */
  public List<InfoDeinflection> getPossibleDeinflections(String inWord)
  {
    // Convert the word to hiragana to facilitate lookups
    inWord = UtilsLang.convertKatakanaToHiragana(inWord);

    // List of possible deinflections so far
    Map<String, Integer> possibleWords = new HashMap<String, Integer>();
    possibleWords.put(inWord, 0);

    // List of each inflection encountered
    List<InfoDeinflection> deList = new ArrayList<InfoDeinflection>();
    deList.add(new InfoDeinflection(inWord, 0xFF, ""));

    int i = 0;
    
    // If loadRulesFile() was not called
    if((reasons == null) || (ruleGroupList == null))
    {
      return deList;
    }

    // Loop through deinflections
    do
    {
      String word = deList.get(i).Word;
      
      for (InfoRuleGroup ruleGroup : ruleGroupList)
      {
        // Make sure that the rule group isn't bigger than the word itself.
        if (ruleGroup.FromLen <= word.length())
        {
          // Store the end of word so it can be compared to deinflection rules
          String endWord = word.substring(word.length() - ruleGroup.FromLen);
    
          for (InfoRule rule : ruleGroup.Rules)
          {
            // Does the inflection match the end of the word?
            if (((deList.get(i).Type & rule.Type) != 0) && (endWord.equals(rule.From)))
            {
              // Apply the rule to the word
              String newWord = word.substring(0, word.length() - rule.From.length()) + rule.To;

              // Inflected words must be 2 or more characters in length
              if (newWord.length() <= 1)
              {
                continue;
              }

              if (possibleWords.containsKey(newWord))
              {
                deList.get(possibleWords.get(newWord)).Type |= (rule.Type >> 8);
                continue;
              }

              // Add word to list of possible deinflections
              possibleWords.put(newWord, deList.size());
                
              
              InfoDeinflection infoDein = new InfoDeinflection("", -1, "");

              if (deList.get(i).Reason.length() != 0)
              {
                infoDein.Reason = rule.Reason + " < " + deList.get(i).Reason;
              }
              else
              {
                infoDein.Reason = rule.Reason;
              }

              infoDein.Type = (rule.Type >> 8);
              infoDein.Word = newWord;

              deList.add(infoDein);
            }
          }
        }
      }
    }
    while (++i < deList.size());

    return deList;
  }
  
  
  
  
  
}
