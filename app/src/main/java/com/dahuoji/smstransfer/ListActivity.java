package com.dahuoji.smstransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.dahuoji.smstransfer.database.DBUtil;
import com.gyf.cactus.Cactus;
import com.gyf.cactus.callback.CactusCallback;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private List<CaseEntity> caseList;
    private BoardAdapter caseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        findViewById(R.id.floatingActionButton).setOnClickListener(this);
        RecyclerView boardRecyclerview = findViewById(R.id.boardRecyclerView);
        boardRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        caseList = new ArrayList<>();
        caseAdapter = new BoardAdapter(this, caseList);
        caseAdapter.setOnBoardEventListener(new BoardAdapter.OnBoardEventListener() {
            @Override
            public void onButtonEditClicked(CaseEntity caseEntity) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                intent.putExtra("case_entity", caseEntity);
                startActivity(intent);
            }

            @Override
            public void onButtonDeleteClicked(CaseEntity caseEntity) {
                DBUtil.getInstance(ListActivity.this).deleteData(EditActivity.Table_Name, EditActivity.Column_ID + "=?", new String[]{caseEntity.getId()});
                caseList.remove(caseEntity);
                caseAdapter.notifyDataSetChanged();
            }
        });
        boardRecyclerview.setAdapter(caseAdapter);
        boardRecyclerview.addItemDecoration(new CaseItemDecoration());
        //注意:需要打开自启动
        Cactus.getInstance()
                .isDebug(true)
                .setTitle("短信转发")
                .setContent("请放心使用")
                .addCallback(new CactusCallback() {
                    @Override
                    public void doWork(int i) {

                    }

                    @Override
                    public void onStop() {

                    }
                })
                .register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        caseList.clear();
        caseList.addAll(getCaseList(this));
        caseAdapter.notifyDataSetChanged();
    }

    public static List<CaseEntity> getCaseList(Context context) {
        List<CaseEntity> caseList = new ArrayList<>();
        DBUtil dbUtil = DBUtil.getInstance(context);
        if (dbUtil.isExists(EditActivity.Table_Name)) {
            Cursor cursor = dbUtil.queryData(EditActivity.Table_Name, null, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String _id = cursor.getString(cursor.getColumnIndex(EditActivity.Column_ID));
                String filters_phone_number = cursor.getString(cursor.getColumnIndex(EditActivity.Column_Filters_Phone_Number));
                String filters_keyword_1 = cursor.getString(cursor.getColumnIndex(EditActivity.Column_Filters_Keyword_1));
                String filters_keyword_2 = cursor.getString(cursor.getColumnIndex(EditActivity.Column_Filters_Keyword_2));
                String forward_contact_1 = cursor.getString(cursor.getColumnIndex(EditActivity.Column_Forward_Contact_1));
                String forward_contact_2 = cursor.getString(cursor.getColumnIndex(EditActivity.Column_Forward_Contact_2));
                CaseEntity caseEntity = new CaseEntity();
                caseEntity.setId(_id);
                caseEntity.setFiltersPhoneNumber(filters_phone_number);
                caseEntity.setFiltersKeyword1(filters_keyword_1);
                caseEntity.setFiltersKeyword2(filters_keyword_2);
                if (!TextUtils.isEmpty(forward_contact_1) && forward_contact_1.contains(",")) {
                    caseEntity.setContact1(new Contact(forward_contact_1.split(",")[0], forward_contact_1.split(",")[1]));
                }
                if (!TextUtils.isEmpty(forward_contact_2) && forward_contact_2.contains(",")) {
                    caseEntity.setContact2(new Contact(forward_contact_2.split(",")[0], forward_contact_2.split(",")[1]));
                }
                caseEntity.setColor(BoardAdapter.colors3[caseList.size() % BoardAdapter.colors3.length]);
                caseList.add(caseEntity);
            }
        }
        return caseList;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingActionButton) {
            startActivity(new Intent(this, EditActivity.class));
        }
    }

    public static void setAlphaChange(final View... views) {
        for (View view : views) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setAlpha(0.8f);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.setAlpha(1.0f);
                            break;
                    }
                    return false;
                }
            });
        }
    }
}