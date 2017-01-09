package ca.fuwafuwa.kaku.Search;

import android.content.Context;
import android.os.AsyncTask;

import java.sql.SQLException;
import java.util.List;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized;
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Models.CharacterOptimized;

/**
 * Created by 0x1bad1d3a on 8/28/2016.
 */
public class Searcher implements JmTask.SearchJmTaskDone, Kd2Task.SearchKd2TaskDone {

    public interface SearchDictDone {
        void jmResultsCallback(List<EntryOptimized> results, SearchInfo search);
        void kd2ResultsCallback(List<CharacterOptimized> results, SearchInfo search);
    }

    private static final String TAG = Searcher.class.getName();

    private SearchDictDone mSearchDictDone;
    private Context mContext;

    public Searcher(Context context) throws SQLException {
        mContext = context;
    }

    public void registerCallback(SearchDictDone dictDone){
        this.mSearchDictDone = dictDone;
    }

    public void search(SearchInfo searchInfo){
        try {
            // Stick to serial execution for now until drawing ugliness with Kd2 usually drawing first on InfoWindow is figured out
            // Parallel doesn't make it that much faster anyways since Kd2 is usually super-fast, biggest delay on JmDict
            new JmTask(searchInfo, this, mContext).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            new Kd2Task(searchInfo, this, mContext).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void jmTaskCallback(List<EntryOptimized> results, SearchInfo searchInfo) {
        mSearchDictDone.jmResultsCallback(results, searchInfo);
    }

    @Override
    public void kd2TaskCallback(List<CharacterOptimized> results, SearchInfo searchInfo) {
        mSearchDictDone.kd2ResultsCallback(results, searchInfo);
    }
}
