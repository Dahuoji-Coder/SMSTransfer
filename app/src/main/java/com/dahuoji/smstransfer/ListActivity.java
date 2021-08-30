package com.dahuoji.smstransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        findViewById(R.id.floatingActionButton).setOnClickListener(this);
        RecyclerView boardRecyclerview = findViewById(R.id.boardRecyclerView);
        boardRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        List<CaseEntity> caseList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            caseList.add(new CaseEntity());
        }
        BoardAdapter boardAdapter = new BoardAdapter(this, caseList);
        boardRecyclerview.setAdapter(boardAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingActionButton) {
            startActivity(new Intent(this, MainActivity.class));
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