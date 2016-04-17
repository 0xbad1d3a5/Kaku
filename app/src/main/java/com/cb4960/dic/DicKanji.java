package com.cb4960.dic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DicKanji extends Dic
{
  private static HashMap<String, InfoKanji> kanjiDic = null;
  private static String defaultDefFormat = "${Meanings}";
  private String defFormat = defaultDefFormat;
  
  public DicKanji()
  {
    this.Name = "Kanji";
    this.NameEng = "Kanji";
    this.ShortName = "Kanji";
    this.SourceType = DicSourceTypeEnum.DEFAULT;
    this.DicType = DicTypeEnum.JE;
    this.ExamplesType = DicExamplesEnum.NO;
  }
  
  
  /** Set the format of the definition */
  public void setDefFormat(String format)
  {
    this.defFormat = format;
  }
  
  
  /** Is the database loaded? */
  public boolean isDatabaseLoaded()
  {
    return (kanjiDic != null);
  }
  
  
  /** Load the kanji database. */
  public boolean openDatabase(String dbFile)
  {
    if(kanjiDic != null)
    {
      return true;
    }

    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dbFile), "UTF8"));
      
      kanjiDic = new HashMap<String, InfoKanji>();
      String line = "";
      
      while ((line = reader.readLine()) != null)
      {
        InfoKanji infoKanji = new InfoKanji();
        String[] fields = line.split("\\|", -1);
        
        infoKanji.Kanji = fields[0].trim();
        String codes = fields[1].trim();
        infoKanji.Readings = fields[2].trim().replace(" ", "、");
        infoKanji.ReadingsNanori = fields[3].trim().replace(" ", "、");
        infoKanji.ReadingsBushumei = fields[4].trim().replace(" ", "、");
        infoKanji.Meanings = fields[5].trim();

        String[] codeFields = codes.split(" ");

        for (String code : codeFields)
        {
          char firstChar = code.charAt(0);

          if (firstChar == 'B')
          {
            int mainRadicalIndex = Integer.parseInt(code.substring(1));
            infoKanji.Radicals.add(mainRadicalIndex);
          }
          else if (firstChar == 'G')
          {
            infoKanji.Grade = Integer.parseInt(code.substring(1));
          }
          else if (firstChar == 'S')
          {
            infoKanji.Strokes = Integer.parseInt(code.substring(1));
          }
          else if (firstChar == 'F')
          {
            infoKanji.Freq = Integer.parseInt(code.substring(1));
          }
          else if (firstChar == 'N')
          {
            infoKanji.Nelson = Integer.parseInt(code.substring(1));
          }
          else if (firstChar == 'V')
          {
            infoKanji.NewNelson = Integer.parseInt(code.substring(1));
          }
          else if (firstChar == 'H')
          {
            infoKanji.Halpern = Integer.parseInt(code.substring(1));
          }
          else if (code.startsWith("DK"))
          {
            infoKanji.KanjiLearnersDictionary = Integer.parseInt(code.substring(2));
          }
          else if (firstChar == 'L')
          {
            infoKanji.Heisig = Integer.parseInt(code.substring(1));
          }
          else if (code.startsWith("IN"))
          {
            infoKanji.TuttleKanjiAndKana = code.substring(2);
          }
          else if (firstChar == 'E')
          {
            infoKanji.Henshall = Integer.parseInt(code.substring(1));
          }
          else if (firstChar == 'P')
          {
            infoKanji.SkipPattern = code.substring(1);
          }
          else if (firstChar == 'I')
          {
            infoKanji.TuttleKanjiDic = code.substring(1);
          }
          else if (firstChar == 'Y')
          {
            infoKanji.PinYin = code.substring(1);
          }
        }

        infoKanji.Unicode = (int)infoKanji.Kanji.charAt(0);

        kanjiDic.put(infoKanji.Kanji, infoKanji);
      }
      
      reader.close();
    }
    catch(Exception e)
    {
      kanjiDic = null;
      return false;
    }
    
    return true;
  }
  
  
  /** Lookup the given name. */
  public List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune)
  {
    List<Entry> dicEntryList = new ArrayList<Entry>();
    HashMap<String, Boolean> usedKanji = new HashMap<String, Boolean>();
   
    if((kanjiDic != null) && (word.length() > 0))
    {
      for(int i = 0; i < word.length(); i++)
      {
        String kanji = Character.toString(word.charAt(i)); 
        
        // Prevent duplicates
        if(!usedKanji.containsKey(kanji))
        {
          InfoKanji infoKanji = kanjiDic.get(kanji);
          
          if(infoKanji != null)
          {
            Entry entry = new Entry();
            entry.Expression = kanji;
            entry.Reading = infoKanji.Readings;
            entry.SourceDic = this;
            entry.Inflected = kanji; 
            
            if(this.defFormat.trim().length() == 0)
            {
              this.defFormat = defaultDefFormat;
            }
            
            entry.Definition = this.defFormat
                .replace("${ReadingsNanori}",          infoKanji.ReadingsNanoriFormatted())
                .replace("${ReadingsBushumei}",        infoKanji.ReadingsBushumeiFormatted())
                .replace("${Meanings}",                infoKanji.MeaningsFormatted())
                .replace("${Freq}",                    infoKanji.FreqStr())
                .replace("${Grade}",                   infoKanji.GradeStr())
                .replace("${Halpern}",                 infoKanji.HalpernStr())
                .replace("${Heisig}",                  infoKanji.HeisigStr())
                .replace("${Henshall}",                infoKanji.HenshallStr())
                .replace("${KanjiLearnersDictionary}", infoKanji.KanjiLearnersDictionaryStr())
                .replace("${Nelson}",                  infoKanji.NelsonStr())
                .replace("${NewNelson}",               infoKanji.NewNelsonStr())
                .replace("${PinYin}",                  infoKanji.PinYinFormatted())
                .replace("${SkipPattern}",             infoKanji.SkipPatternFormatted())
                .replace("${Strokes}",                 infoKanji.StrokesStr())
                .replace("${TuttleKanjiAndKana}",      infoKanji.TuttleKanjiAndKanaFormatted())
                .replace("${TuttleKanjiDic}",          infoKanji.TuttleKanjiDicFormatted())
                .replace("${Unicode}",                 infoKanji.UnicodeStr());
                        
            dicEntryList.add(entry);
            usedKanji.put(kanji, true);
          }
        }
      }
    }
    
    return dicEntryList;
  }
  
  
}
