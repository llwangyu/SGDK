package com.mwj.lhn.sgdk;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * Created by 13893 on 2017/12/6.
 */


public class Tab extends TabActivity {




    private int myMenuRes[] = { R.drawable.tab1, R.drawable.tab2,
            R.drawable.tab3, R.drawable.tab4

    };

    TabHost tabHost;
    TabSpec firstTabSpec;
    TabSpec secondTabSpec;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nbzltab);

        setTitle("内部资料");
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setBackgroundResource(R.drawable.background_login);
//fdsfsdfsdfafsd

        firstTabSpec = tabHost.newTabSpec("tid1");
        secondTabSpec = tabHost.newTabSpec("tid2");




        firstTabSpec.setIndicator("资料办理", getResources().getDrawable(
                myMenuRes[0]));
        secondTabSpec.setIndicator("资料列表", getResources().getDrawable(
                myMenuRes[1]));



        Bundle bundle = this.getIntent().getExtras();
        String tel = bundle.getString("tel");
        String plan_date = bundle.getString("plan_date");
        String msg = bundle.getString("msg");
        Bundle Nbundle = new Bundle();
        Nbundle.putString("tel", tel);
        Nbundle.putString("plan_date", plan_date);
        Nbundle.putString("msg", msg);


        Intent firstIntent = new Intent(this, com.mwj.lhn.sgdk.mwj.SgxxActivity.class);
        firstIntent.putExtras(Nbundle);
        Intent secondIntent = new Intent(this, com.mwj.lhn.sgdk.mwj.WtlbActivity.class);
        secondIntent.putExtras(Nbundle);

        firstTabSpec.setContent(firstIntent);
        secondTabSpec.setContent(secondIntent);

        tabHost.addTab(firstTabSpec);
        tabHost.addTab(secondTabSpec);
    }
}
