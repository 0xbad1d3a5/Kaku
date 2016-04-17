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
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/** Represents the ENAMDICT names dictionary.
 *  
 *  Ported from JGlossator.
 *  */
public class DicNames extends Dic
{
  private SQLiteDatabase sqliteConnection = null;
   
  /** Constructor. */
  public DicNames()
  {
    this.Name = "ENAMDICT";
    this.NameEng = "ENAMDICT";
    this.ShortName = "Name";
    this.SourceType = DicSourceTypeEnum.DEFAULT;
    this.DicType = DicTypeEnum.JE;
    this.ExamplesType = DicExamplesEnum.NO;
  }
   
  
  /** Is the database loaded? */
  public boolean isDatabaseLoaded()
  {
    return (sqliteConnection != null);
  }
  
  
  /** Load the ENAMDICT database. */
  public boolean openDatabase(String dbFile)
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
        
    return status;
  }
  
  
  /** Close the ENAMDICT database. */
//  private void closeDatabase()
//  {
//    sqliteConnection.close();
//  }
  
  
  /** Lookup the given name. */
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    // The list of entries in the dictionary for this word
    List<Entry> dicEntryList = new ArrayList<Entry>();

    if (sqliteConnection == null)
    {
      return null;
    }
    
    String query = "SELECT * FROM dict WHERE name='" + word + "'";
    Cursor cursor = sqliteConnection.rawQuery(query, null);
    
    if (cursor.moveToNext())
    {
      Entry entry = new Entry();
      entry.SourceDic = this;
      entry.Expression = word;
      entry.Inflected = word;
      entry.Definition = cursor.getString(cursor.getColumnIndex("romaji"));
     
      dicEntryList.add(entry);
    }
    
    cursor.close();

    if ((dicEntryList.size() == 0) || (dicEntryList.get(0).Definition == ""))
    {
      dicEntryList = null;
    }

    return dicEntryList;
  }
  

  /**
   * Lookup names found at the start of the provided text. Returned list will be sorted
   * from longest match to smallest match.
   * */
  public List<Entry> searchWord(String text, int maxNameLength)
  {
    if((text.trim().length() == 0) || (maxNameLength <= 0))
    {
      return null;
    }
    
    List<Entry> nameList = new ArrayList<Entry>();
           
    int lastIndex = Math.min(maxNameLength, text.length());
        
    for(int i = lastIndex; i > 0; i--)
    {
      String searchText = text.substring(0, i);
      
      List<Entry> lookupList = lookup(searchText, false, null);
      
      if((lookupList == null) || (lookupList.size() == 0))
      {
        continue;
      }
      
      nameList.add(lookupList.get(0));
    }
    
    return nameList;
  }
}
