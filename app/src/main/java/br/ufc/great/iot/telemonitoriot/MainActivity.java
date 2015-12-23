package br.ufc.great.iot.telemonitoriot;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Provider;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleField;
import br.ufc.great.syssu.base.interfaces.IReaction;

public class MainActivity extends AppCompatActivity {

    private SyssuManager mSyssu;
    private Object subscribeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            createFileOnDevice(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSyssu = SyssuManager.getInstance(this);
        mSyssu.start();

        IReaction reaction = new IReaction() {

            private Object id;

            @Override
            public void setId(Object id) {
                this.id = id;
            }

            @Override
            public Object getId() {
                return id;
            }

            @Override
            public Pattern getPattern() {
                Pattern p = (Pattern) new Pattern().addField("id", "?");
                return p;
            }

            @Override
            public String getRestriction() {
                return null;
            }

            @Override
            public void react(Tuple tuple) {
                String value = tuple.getField(1).getValue().toString();

                Iterator<TupleField> tupleIterator = tuple.iterator();
                String log = "";
                while(tupleIterator.hasNext()){
                    TupleField field = tupleIterator.next();
                    log = log + "," + field.getName() + "=" + field.getValue();
                }
                log = log + "," + "timestamp" + "=" + System.currentTimeMillis();
                log = log.substring(1);
                Log.d("Telemonitor", log);
                log(log);
            }
        };

        subscribeId = mSyssu.subscribe(reaction, Provider.LOCAL);
    }

    private void log(final String s) {

                writeToFile(s);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSyssu.unsubscribe(subscribeId);
    }

    public static BufferedWriter out;

    private void createFileOnDevice(Boolean append) throws IOException {
                /*
                 * Function to initially create the log file and it also writes the time of creation to file.
                 */
        File Root = Environment.getExternalStorageDirectory();
        if(Root.canWrite()){
            File  LogFile = new File(Root, "Log.txt");
            FileWriter LogWriter = new FileWriter(LogFile, append);
            out = new BufferedWriter(LogWriter);
            Date date = new Date();
            out.write("Logged at" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n"));
            out.flush();

        }
    }

    public void writeToFile(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
