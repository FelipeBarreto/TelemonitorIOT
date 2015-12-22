package br.ufc.great.iot.telemonitoriot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Provider;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IReaction;

public class MainActivity extends AppCompatActivity {

    private SyssuManager mSyssu;
    private Object subscribeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Log.d("sensorTagMonitor", value);
            }
        };

        subscribeId = mSyssu.subscribe(reaction, Provider.LOCAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSyssu.unsubscribe(subscribeId);
    }
}
