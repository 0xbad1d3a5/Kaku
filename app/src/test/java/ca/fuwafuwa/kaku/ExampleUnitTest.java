package ca.fuwafuwa.kaku;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import org.junit.Test;

import java.util.List;

import ca.fuwafuwa.kaku.Deinflictor.PosMap;

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
}