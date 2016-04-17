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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/** Represents the EDICT J-E dictionary.
 *  http://ftp.monash.edu.au/pub/nihongo/00INDEX.html 
 *  
 *  Ported from JGlossator.
 *  */
public class DicEdict extends Dic
{
  private SQLiteDatabase sqliteConnection = null;
  
  private Deinflector deinflector = null;   
  
  /** Constructor. */
  public DicEdict()
  {
    this.Name = "EDICT";
    this.NameEng = "EDICT";
    this.ExamplesName = "Tatoeba";
    this.ShortName = "EDICT";
    this.ExamplesNotes = "Includes Tanaka Corpus";
    this.SourceType = DicSourceTypeEnum.DEFAULT;
    this.DicType = DicTypeEnum.JE;
    this.ExamplesType = DicExamplesEnum.YES;
    this.SeparateExampleDictionary = true;
  }
  
  
  /** Is the database loaded? */
  public boolean isDatabaseLoaded()
  {
    return (sqliteConnection != null);
  }
  
  
  /** Load the EDICT database. */
  public boolean openDatabase(String dbFile, String rulesFile)
  {
    boolean status = false;
    
    try
    {
      sqliteConnection = SQLiteDatabase.openDatabase(dbFile, null, 
          SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
      status = true;
    }
    catch (SQLiteException e)
    {
      status = false;
    }
    
    if(status)
    {
      deinflector = new Deinflector();
      status = deinflector.loadRulesFile(rulesFile);
    }
    
    return status;
  }
  
  
  /** Close the EDICT database. */
//  private void closeDatabase()
//  {
//    sqliteConnection.close();
//  }
  
  
  /** Lookup the given word. */
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    // The list of entries in the dictionary for this word
    List<Entry> dicEntryList = new ArrayList<Entry>();

    // Database search fields have been converted to hiragana so we need to convert the 
    // provided word to hiragana to facilitate the search.
    word = UtilsLang.convertKatakanaToHiragana(word);

    if (sqliteConnection == null)
    {
      return null;
    }
    
    String query = "SELECT * FROM dict WHERE kanji='" + word + "' OR kana='" + word + "' LIMIT 100";
    Cursor cursor =  sqliteConnection.rawQuery(query, null);
    
    while (cursor.moveToNext())
    {
      String expression = cursor.getString(cursor.getColumnIndex("kanji"));
      String reading = cursor.getString(cursor.getColumnIndex("kana"));
      String katakana = cursor.getString(cursor.getColumnIndex("katakana"));
      String definition = cursor.getString(cursor.getColumnIndex("def"));

      Entry entry = this.createEdictEntry(expression, reading, katakana, definition);

      dicEntryList.add(entry);
    }
    
    cursor.close();

    if (dicEntryList.size() == 0)
    {
      dicEntryList = null;
    }

    return dicEntryList;
  }
  

  /// <summary>
  /// Replace sub-definition numbers with a single character.
  /// So (1) -> ①, etc.
  /// </summary>
  private String replaceSubDefNumber(String def)
  {
    String newDef = def;
    
    for (int i = 1; i <= 30; i++)
    {
      String parenNum = "(" + i + ")";

      if (newDef.contains(parenNum))
      {
        newDef = newDef.replace(parenNum, UtilsLang.convertIntegerToCircledNumStr(i));
      }
      else
      {
        break;
      }
    }

    return newDef;
  }
  
 
  /** 
   * Get all possible deinflected dictionary entries of the provided word.
   *  
   *  For example, searching for 盛り上�?��?��?� would return this list:
   *    1) 盛り上�?�る
   *    2) 盛り
   *    3) 盛り
   *    4) 盛る
   *    5) 盛る
   *   
   *  Each element in the list will contain the dictionary form of the word (shown above),
   *  it's reading, the deinflection rule (ex: -te), and the original inflected word.
   *  
   *  Note: This routine was adapted from Rikaichan.
   *  http://www.polarcloud.com/rikaichan/
   *  
  /// maxEntries : The maximum size of the resulting list. Set to 0 to ignore. */
  public List<Entry> searchWord(String word, int maxEntries)
  {
    List<Entry> finalDictEntryList = new ArrayList<Entry>();

    String theWord = UtilsLang.convertKatakanaToHiragana(word);
    
    // Remove single quote to avoid query errors
    theWord = theWord.replaceAll("'", "");

    if (deinflector == null)
    {
      return finalDictEntryList;
    }

    while (theWord.length() > 0)
    {
      // Get list of possible deinflections for this word. The list will have a lot of entries
      // that are not actual words. Actual words will be determined later on in this routine.
      List<InfoDeinflection> resultsList = deinflector.getPossibleDeinflections(theWord);

      if ((maxEntries != 0) && (finalDictEntryList.size() >= maxEntries))
      {
        break;
      }

      Map<Entry, Boolean> possibleEntryList = new HashMap<Entry, Boolean>();
      int count = 0;

      for (InfoDeinflection result : resultsList)
      {
        List<Entry> entryList = this.lookup(result.Word, false, new FineTune());

        if ((maxEntries != 0) && (finalDictEntryList.size() >= maxEntries))
        {
          break;
        }

        if (entryList != null)
        {
          for (Entry entry : entryList)
          {
            if ((maxEntries != 0) && (finalDictEntryList.size() >= maxEntries))
            {
              break;
            }

            if (possibleEntryList.containsKey(entry))
            {
              continue;
            }

            boolean valid = true;

            if (count > 0)
            {
              if (
                (((result.Type & 1) != 0) && entry.Definition.contains("v1"))
                || (((result.Type & 2) != 0) && entry.Definition.contains("v5"))
                || (((result.Type & 4) != 0) && entry.Definition.contains("adj-i"))
                || (((result.Type & 8) != 0) && entry.Definition.contains("vk"))
                || (((result.Type & 16) != 0) && entry.Definition.contains("vs-")))
              {
                valid = true;
              }
              else
              {
                valid = false;
              }
            }

            if (valid)
            {
              if (!possibleEntryList.containsKey(entry))
              {
                possibleEntryList.put(entry, true);

                entry.DeinflectionRule = result.Reason;
                entry.Inflected = theWord;
                finalDictEntryList.add(entry);
              }
            }
          }
        }

        count++;
      }

      theWord = theWord.substring(0, theWord.length() - 1);
    }

    return finalDictEntryList;
  }
  
  
  /**  Create an Entry based on the provided information. */
  private Entry createEdictEntry(String expression, String reading, String katakana, String definition)
  {
    Entry entry = new Entry();

    // Does this word consist of once or more katakana characters?
    if (katakana.length() > 0)
    {
      // Replace hiragana fields with the actual katakana versions.
      String[] fields = katakana.split("/");

      if (fields.length == 1)
      {
        expression = "";
        reading = fields[0];
      }
      else
      {
        expression = fields[0];
        reading = fields[1];
      }
    }

    if (expression.length() == 0)
    {
      expression = reading;
    }

    entry.Expression = expression;
    entry.Reading = reading;
    entry.Definition = definition;

    entry.Definition = this.replaceSubDefNumber(entry.Definition);

    entry.Definition = entry.Definition.replace("/", "; ");
    

    // Replace final semicolon in subdef line with a period
    entry.Definition = entry.Definition.replaceAll("; <br />", ".<br />");

    int lastSemiColon = entry.Definition.lastIndexOf(";");

    if (lastSemiColon > 0)
    {
      StringBuilder builder = new StringBuilder(entry.Definition);
      builder.setCharAt(lastSemiColon, '.');
      entry.Definition = builder.toString();
    }

    entry.Definition = entry.Definition.replaceAll("[; ]+$", ""); // Remove trailing "; " and " "

    return entry;
  }
  
  
  /** Get the number of times that the provided reading appears in EDICT. */
  public int getReadingCount(String reading)
  {
    int count = 0;
    
    if (sqliteConnection == null)
    {
      return -1;
    }
    
    String query = "SELECT COUNT(*) FROM dict WHERE kana='" + reading + "' LIMIT 100";
    Cursor cursor =  sqliteConnection.rawQuery(query, null);
    
    cursor.moveToFirst();
    count = cursor.getInt(0);
    
    cursor.close();
    
    return count;
  }
}
