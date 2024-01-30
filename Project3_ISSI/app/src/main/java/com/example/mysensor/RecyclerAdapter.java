package com.example.mysensor;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysensor.sensorData.Data;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private ArrayList<Data> mData = new ArrayList<Data>();
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();    //item의 클릭 상태를 저장할 array객체
    private int prePosition = -1;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //item 하나 하나 보여주는 (bind되는) 함수
        holder.onBind(mData.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void addItem(Data data){
        mData.add(data);
    }               //외부에서 item을 추가할 수 있음

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout detail;
        private TextView dateTime;
        private ImageButton chevron;
        private TextView distanceValue;
        private TextView ledValue;
        private ImageView ledColor;
        private Data data;
        private int position;

        ItemViewHolder(View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.date);
            chevron= itemView.findViewById(R.id.chevron);
            detail=itemView.findViewById(R.id.detail);
            ledColor=itemView.findViewById(R.id.ledColor);
            distanceValue=itemView.findViewById(R.id.distanceValue);
            ledValue=itemView.findViewById(R.id.ledValue);

        }

        void onBind(Data data,int position) {
            this.data = data;
            this.position=position;
            dateTime.setText(data.getDateTime());
            ledValue.setText(data.getLedValue());
            distanceValue.setText(data.getDistanceValue());
            ledColor.setImageResource(data.getLedColor());

            changeVisibility(selectedItems.get(position));

            chevron.setOnClickListener(this);
        }

        public void onClick(View v){
            //펼쳐진 아이팀 클릭시
            if(selectedItems.get(position)){
                selectedItems.delete(position);
            }else{
                //직전 클릭된 아이템을 지움
                selectedItems.delete(prePosition);
                //클릭한 아이템 position 저장
                selectedItems.put(position,true);
            }
            //포지션 변화를 알림
            if(prePosition!=-1) notifyItemChanged(prePosition);

            notifyItemChanged(position);
            //클릭된 포지션 저장
            prePosition=position;
        }

        private void changeVisibility(final boolean isExpanded) {
            int dpValue = 80;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(200);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    // 더보기란 높이 변경 및 펼치기,접기
                    detail.getLayoutParams().height = value;
                    detail.requestLayout();
                    detail.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            va.start();
        }
    }
}

