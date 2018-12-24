package ca.fuwafuwa.kaku;

import android.content.Context;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import ca.fuwafuwa.kaku.Deinflictor.PosMap;

import static org.mockito.Mockito.mock;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    private String GetTokenString(Token token){
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                PosMap.Companion.GetPosMapVal(token.getPartOfSpeechLevel1()),
                PosMap.Companion.GetPosMapVal(token.getPartOfSpeechLevel2()),
                PosMap.Companion.GetPosMapVal(token.getPartOfSpeechLevel3()),
                PosMap.Companion.GetPosMapVal(token.getPartOfSpeechLevel4()),
                PosMap.Companion.GetPosMapVal(token.getConjugationType()),
                PosMap.Companion.GetPosMapVal(token.getConjugationForm()),
                token.getBaseForm(),
                token.getReading(),
                token.getPronunciation());
    }

    @Test
    public void test(){
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize("ずっと側にいてくれてありがとう。");
        for (Token token : tokens) {
            System.out.println(token.getSurface() + "\t" + GetTokenString(token));
        }
    }

    @Test
    public void generateDic(){
        Context context = mock(Context.class);
        Mockito.when(context.getDatabasePath("")).thenReturn(new File(""));
    }

    @Test
    public void TestCircledNum(){
        for (int i = 1; i <= 100; i++){
            System.out.println(LangUtils.Companion.ConvertIntToCircledNum(i));
        }
    }
}