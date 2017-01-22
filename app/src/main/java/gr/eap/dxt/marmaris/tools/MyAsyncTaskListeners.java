package gr.eap.dxt.marmaris.tools;

/**
 * Created by GEO on 22/1/2017.
 */

public interface MyAsyncTaskListeners {

    void myOnPreExecute();
    void myDoInBackground() throws Exception;
    void myOnPostExecute();

}
