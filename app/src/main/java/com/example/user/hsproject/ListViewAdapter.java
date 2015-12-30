package com.example.user.hsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by user on 2015-12-24.
 *
 *  [물류정보팀 조준열], [온라인팀 손동현], [온라인팀 허용]
 *
 *  리스트뷰에 보여주는 데이터 바인딩 부분을 정의.
 */
public class ListViewAdapter extends BaseAdapter {

    private ListViewAdapter mAdapter = null;
    private Context mContext = null;
    private ArrayList<ListData> mListData = new ArrayList<ListData>();
    private int image_arr [] = {R.drawable.no_image1, R.drawable.no_image2, R.drawable.no_image3};

    public ListViewAdapter(Context applicationContext) {
        super();
        this.mContext = applicationContext;
    }
    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    // 제목을 가져오는 메서드
    public String getmTitle(int position) {
        return mListData.get(position).mTitle;
    }

    // 썸네일 사진을 가져오는 메서드
    public String getmIcon(int position){
        return mListData.get(position).mIcon;
    }

    // 조회수를 가져오는 메서드
    public String getmHit(int position){ return mListData.get(position).mCount;}

    // 등록날짜를 가져오는 메서드
    public String getmDate(int position){ return mListData.get(position).mDate;}

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 리스트 추가부분.
    public void addItem(String THUMB_URL, String SUBJECT, String REG_DT, String VIEW_COUNT){
        ListData addInfo = null;
        addInfo = new ListData();
        addInfo.mIcon = THUMB_URL;
        addInfo.mTitle = SUBJECT;
        addInfo.mDate = REG_DT;
        addInfo.mCount = VIEW_COUNT;

        mListData.add(addInfo);
    }

    public void remove(int position){
        mListData.remove(position);
        dataChange();
    }

    public void sort(){
        Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
        dataChange();
    }

    public void dataChange(){
        mAdapter.notifyDataSetChanged();
    }

    //
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, null);

            holder.mIcon = (CircularImageView) convertView.findViewById(R.id.mImage);
            holder.mText = (TextView) convertView.findViewById(R.id.mText);
            holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
            holder.mCount = (TextView) convertView.findViewById(R.id.mcount);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ListData mData = mListData.get(position);

        if (mData.mIcon.length()>1) {
            Glide.with(mContext).load(mData.mIcon).transform(new CircleTransform(mContext)).into(holder.mIcon);

        }else{
            // 이미지가 없는 데이터는 랜덤으로 no_image를 출력
            Random random= new Random();
            int random_icon = random.nextInt(3);
            holder.mIcon.setImageResource(image_arr[random_icon]);
        }
        holder.mText.setText(mData.mTitle);
        holder.mDate.setText(mData.mDate);
        holder.mCount.setText(" | 조회수 " + mData.mCount);

        return convertView;
    }

    private class ViewHolder {
        public ImageView mIcon;
        public TextView mText;
        public TextView mDate;
        public TextView mCount;
    }

    //글라이드 이미지 둥글게 처리 [온라인팀 허용]
    public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }
    }

}