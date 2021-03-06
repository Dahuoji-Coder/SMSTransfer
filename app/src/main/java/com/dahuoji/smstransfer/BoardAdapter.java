package com.dahuoji.smstransfer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<CaseEntity> caseList;
    public static final int[] colors1 = new int[]{Color.parseColor("#F2CDCB"), Color.parseColor("#6C6C8C"), Color.parseColor("#62A8A1"), Color.parseColor("#81D1DD")};
    public static final int[] colors2 = new int[]{Color.parseColor("#EDC382"), Color.parseColor("#355B94"), Color.parseColor("#C3CBD2"), Color.parseColor("#AA3B45")};
    public static final int[] colors3 = new int[]{Color.parseColor("#44435F"), Color.parseColor("#B3B4B7"), Color.parseColor("#505F99"), Color.parseColor("#7396D1")};

    public BoardAdapter(Context context, List<CaseEntity> caseList) {
        this.context = context;
        this.caseList = caseList;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_board_layout, parent, false);
        return new BoardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        holder = (BoardViewHolder) holder;
        CaseEntity caseEntity = caseList.get(position);
        StringBuilder filtersStr = new StringBuilder();
        if (!TextUtils.isEmpty(caseEntity.getFiltersPhoneNumber())) {
            filtersStr.append(caseEntity.getFiltersPhoneNumber()).append(" | ");
        }
        if (!TextUtils.isEmpty(caseEntity.getFiltersKeyword1())) {
            filtersStr.append(caseEntity.getFiltersKeyword1()).append(" | ");
        }
        if (!TextUtils.isEmpty(caseEntity.getFiltersKeyword2())) {
            filtersStr.append(caseEntity.getFiltersKeyword2()).append(" | ");
        }
        if (filtersStr.length() > 0) {
            ((BoardViewHolder) holder).filtersTextView.setText(filtersStr.substring(0, filtersStr.length() - 3));
        } else {
            ((BoardViewHolder) holder).filtersTextView.setText("??????????????????");
        }
        StringBuilder forwardStr = new StringBuilder();
        if (caseEntity.getContact1() != null) {
            forwardStr.append(caseEntity.getContact1().getName() + " (" + caseEntity.getContact1().getPhoneNumber() + ")");
            if (caseEntity.getContact2() != null) {
                forwardStr.append("\n").append(caseEntity.getContact2().getName() + " (" + caseEntity.getContact2().getPhoneNumber() + ")");
            }
        }
        if (forwardStr.length() > 0) {
            ((BoardViewHolder) holder).transferTextView.setText(forwardStr);
        } else {
            ((BoardViewHolder) holder).transferTextView.setText("????????????");
        }
        if (caseEntity.isShowButtons()) {
            ((BoardViewHolder) holder).buttonsLayout.setVisibility(View.VISIBLE);
        } else {
            ((BoardViewHolder) holder).buttonsLayout.setVisibility(View.GONE);
        }
        ListActivity.setAlphaChange(((BoardViewHolder) holder).contentLayout, ((BoardViewHolder) holder).buttonEdit, ((BoardViewHolder) holder).buttonDelete);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((BoardViewHolder) holder).itemBoardRoot.setClipToOutline(true);
        }
        ((BoardViewHolder) holder).contentLayout.setBackgroundColor(caseEntity.getColor());
        RecyclerView.@NotNull ViewHolder finalHolder = holder;
        ((BoardViewHolder) holder).contentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(">>>BoardAdapter", "=== on long click root");
                if (((BoardViewHolder) finalHolder).buttonsLayout.getVisibility() == View.GONE) {
                    ((BoardViewHolder) finalHolder).buttonsLayout.setVisibility(View.VISIBLE);
                    caseEntity.setShowButtons(true);
                }
                return true;
            }
        });
        ((BoardViewHolder) holder).buttonsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });
        ((BoardViewHolder) holder).buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBoardEventListener.onButtonEditClicked(caseEntity);
            }
        });
        ((BoardViewHolder) holder).buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBoardEventListener.onButtonDeleteClicked(caseEntity);
                Log.d(">>>BoardAdapter", "=== on click delete button");
            }
        });
    }

    @Override
    public int getItemCount() {
        return caseList == null ? 0 : caseList.size();
    }

    static class BoardViewHolder extends RecyclerView.ViewHolder {
        public final View itemBoardRoot;
        public final RelativeLayout contentLayout;
        public final LinearLayout buttonsLayout;
        public final ImageView buttonEdit;
        public final ImageView buttonDelete;
        public final TextView filtersTextView;
        public final TextView transferTextView;

        public BoardViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemBoardRoot = itemView.findViewById(R.id.itemBoardRoot);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            filtersTextView = itemView.findViewById(R.id.filtersTextView);
            transferTextView = itemView.findViewById(R.id.transferTextView);
        }
    }

    private OnBoardEventListener onBoardEventListener;

    public void setOnBoardEventListener(OnBoardEventListener onBoardEventListener) {
        this.onBoardEventListener = onBoardEventListener;
    }

    public interface OnBoardEventListener {
        void onButtonEditClicked(CaseEntity caseEntity);

        void onButtonDeleteClicked(CaseEntity caseEntity);
    }
}
