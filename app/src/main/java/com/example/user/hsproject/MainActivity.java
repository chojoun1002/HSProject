package com.example.user.hsproject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // View pager를 지칭할 변수
    ViewPager mViewPager;
    // View pager 이동을 담당하는 버튼
    Button page1Btn, page2Btn;
    // Detail page 전달 변수
    String title, icon ,date , hit = "";
    // Detail page detail_title
    TextView detail_title;
    TextView detail_date;
    TextView detail_hit;
    ImageView detail_image;
    CircularImageView myPic;

    View viewPager;
    ImageView menu_icon; // 각 리스트의 아이콘

    //핀치줌
    PhotoViewAttacher mAttacher;

    private OkHttpClient client =null;
    // 리스트뷰 정의.
    private ListView mListView;
    // 어뎁터 정의.
    private ListViewAdapter mAdapter01,mAdapter02 = null;

    /* Fragment numbering */
    private int NUM_PAGES = 3;        // 최대 페이지의 수
    public static int menu_num =  0; // 현재화면을 알려주는 int값  0 : 빠른해결, 1 : 매뉴얼, 2 : 상세화면
    public static int FRAGMENT_PAGE = 0; // 현재 페이지를 담음
    public final static int FRAGMENT_PAGE1 = 0; //
    public final static int FRAGMENT_PAGE2 = 1;
    public final static int FRAGMENT_PAGE3 = 2;

    // 접속가능 URL
    private String[] RequestUrl = {"http://asdev.hanssem.com/admin/board/doSearch.as?CONTEXT_TYPE=FAQ&PARAM_CONTEXT_TYPE=FAQ&VIEW_COUNT=50"
                                                   ,"http://asdev.hanssem.com/admin/board/doSearch.as?CONTEXT_TYPE=MAN&PARAM_CONTEXT_TYPE=FAQ&VIEW_COUNT=50"};

    // back 버튼 2번 종료
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 네트워크 상태 체크
        if(!NetworkCheck(this)){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("네트워크 연결 오류").setMessage("네트워크 상태 확인 후 다시 시도해 주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            finish();
                        }
                    }).show();
        }

        // 어뎁터 정의
        mAdapter01 = new ListViewAdapter(getApplicationContext());
        mAdapter02 = new ListViewAdapter(getApplicationContext());

        // 메인 아이콘 정의
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        // 아이콘의 투명도 여부 설정을 위해 정의.
        Drawable alpha = ((ImageView)findViewById(R.id.menu_icon)).getDrawable();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 나의 프로필 사진 터치시에 프로필 사진 다이얼로그로 출력
        myPic = (CircularImageView)findViewById(R.id.my_pic);
        myPic.setImageResource(R.drawable.sulhyun);
        myPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.my_pic_dialog);

                dialog.setTitle("나의 프로필 사진");
                ImageView my_pic = (ImageView) dialog.findViewById(R.id.dialog_img);
                my_pic.setImageResource(R.drawable.sulhyun);

                dialog.show();
            }
        });

        // 뷰페이저 정의.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
        //mViewPager.setCurrentItem(FRAGMENT_PAGE1);
        FRAGMENT_PAGE =  FRAGMENT_PAGE1;// 0

        // 화면이동을 위한 버튼 설정.
        page1Btn = (Button) findViewById(R.id.Page1Btn);
        page1Btn.setOnClickListener(this);
        page2Btn = (Button) findViewById(R.id.Page2Btn);
        page2Btn.setOnClickListener(this);
        menu_icon = (ImageView)findViewById(R.id.menu_icon);
        setMenuIcon(FRAGMENT_PAGE1);

        // 화면 최초 진입시 빠른조치 볼수 있도록.
        page1Btn.performClick();

        // 백버튼 2번 누르면 종료
        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    // PagerAdapterClass 정의.
    private  class PagerAdapterClass extends PagerAdapter {

        private LayoutInflater mInflater;

        private String[] RequestUrl = {"http://asdev.hanssem.com/admin/board/doSearch.as?CONTEXT_TYPE=FAQ&PARAM_CONTEXT_TYPE=FAQ&VIEW_COUNT=40"
                                                        ,"http://asdev.hanssem.com/admin/board/doSearch.as?CONTEXT_TYPE=MAN&PARAM_CONTEXT_TYPE=FAQ&VIEW_COUNT=40"};

        public PagerAdapterClass(Context context) {
            super();
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(final ViewGroup pager, int position) {

            View v = null;

            viewPager = mInflater.inflate(R.layout.content_page, null);

            if(position==0) {
                v=viewPager;
                mListView = (ListView) v.findViewById(R.id.listView1);
                mAdapter01 = new ListViewAdapter(getApplicationContext());
                mAdapter01.notifyDataSetChanged();
                AsyncTaskData(0);
                mListView.setAdapter(mAdapter01);
                title = icon = date= hit = "";
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        title = mAdapter01.getmTitle(position);
                        icon = mAdapter01.getmIcon(position);
                        date = mAdapter01.getmDate(position);
                        hit = mAdapter01.getmHit(position);
                        System.out.println("#########################################" + date);
                        mViewPager.setCurrentItem(FRAGMENT_PAGE3);
                        menu_num = FRAGMENT_PAGE3;
                        FRAGMENT_PAGE = FRAGMENT_PAGE1; // 0
                        setMenuIcon(menu_num);
                        menu_icon.setVisibility(View.GONE);
                        ((ImageView) findViewById(R.id.menu_icon)).getDrawable().setAlpha(0);
                    }
                });
            }
            else if(position==1)
            {
                v=viewPager;
                mListView = (ListView) v.findViewById(R.id.listView1);
                mAdapter02 = new ListViewAdapter(getApplicationContext());
                mAdapter02.notifyDataSetChanged();
                AsyncTaskData(1);
                mListView.setAdapter(mAdapter02);
                title = icon = date= hit = "";
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        title = mAdapter02.getmTitle(position);
                        icon = mAdapter02.getmIcon(position);
                        date = mAdapter02.getmDate(position);
                        hit = mAdapter02.getmHit(position);
                        mViewPager.setCurrentItem(0);
                        System.out.println("#########################################" + date);
                        mViewPager.setCurrentItem(FRAGMENT_PAGE3);
                        //menu_icon.setVisibility(View.INVISIBLE);
                        menu_num = FRAGMENT_PAGE3;
                        FRAGMENT_PAGE = FRAGMENT_PAGE2;// 1
                        setMenuIcon(menu_num);
                        menu_icon.setVisibility(View.GONE);
                        ((ImageView) findViewById(R.id.menu_icon)).getDrawable().setAlpha(0);

                    }
                });
            }
            else if(position==2)
            {
                viewPager = mInflater.inflate(R.layout.content_detail, null);
                detail_title = (TextView) viewPager.findViewById(R.id.detail_title);
                detail_image = (ImageView) viewPager.findViewById(R.id.detail_image);
                detail_date = (TextView) viewPager.findViewById(R.id.detail_date);
                detail_hit = (TextView) viewPager.findViewById(R.id.detail_hit);

                if(title.length()>1) {
                    detail_title.setText(title);
                } else  {
                    detail_title.setText("제목이 들어갈 공간입니다.");
                }

                if (icon.length()>1) {
                    Glide.with(getApplicationContext()).load(icon).into(detail_image);

                    detail_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog mdialog = new Dialog(MainActivity.this);
                            mdialog.setContentView(R.layout.my_pic_dialog);
                            mdialog.setTitle("상세사진");
                            ImageView detail_image = (ImageView) mdialog.findViewById(R.id.dialog_img);
                            //detail_image.setImageResource(R.drawable.sulhyun);
                            Glide.with(getApplicationContext()).load(icon).into(detail_image);
                            //이미지뷰 핀치줌 적용.
                            mAttacher = new PhotoViewAttacher(detail_image);
                            mAttacher.setScaleType(ImageView.ScaleType.FIT_XY);

                            //다이얼로그 임의 크기 지정.
                            WindowManager.LayoutParams params = mdialog.getWindow().getAttributes();
                            params.width = WindowManager.LayoutParams.MATCH_PARENT;
                            params.height = 800;
                            mdialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
                            mdialog.show();
                        }
                    });
                } else {
                    detail_image.setImageResource(R.drawable.image_not_found);
                }

                if(hit.length()>1) {
                    detail_hit.setText(hit);
                } else  {
                    detail_hit.setText("00");
                }

                if(date.length()>0){
                    detail_date.setText(date);
                }else{
                    detail_date.setText("0000/00/00");
                }

                v=viewPager;
                menu_num=position;  //2
            }
            pager.addView(v, 0);
            FRAGMENT_PAGE =  FRAGMENT_PAGE3;// 2
            return v;
        }

        @Override
        public void destroyItem(ViewGroup pager, int position, Object view) {
            pager.removeView((View) view);
        }

        // 생성 가능한 페이지 개수를 반환해준다.
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return NUM_PAGES;   //3
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    // 서버통신을 통한 데이터 수신.
    public void AsyncTaskData(final int k) {
        //AsyncTask
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    client = new OkHttpClient();
                    Request request = new Request.Builder().url(RequestUrl[k]).build();
                    Response response = client.newCall(request).execute();
                    return response.body().string();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    Gson gson = new Gson();
                    menu_num=k;
                    if(k== 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                VoData temp = gson.fromJson(jsonArray.get(i).toString(), VoData.class);
                                mAdapter01.addItem(temp.getTHUMB_URL(), temp.getSUBJECT(), temp.getREG_DT(), temp.getVIEW_COUNT());
                            }
                            mAdapter01.notifyDataSetChanged();

                    }else if(k == 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            VoData temp = gson.fromJson(jsonArray.get(i).toString(), VoData.class);
                            mAdapter02.addItem(temp.getTHUMB_URL(),temp.getSUBJECT(),temp.getREG_DT(),temp.getVIEW_COUNT());
                        }
                        mAdapter02.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Back 버튼이 눌리 었을때 작동 하는 이벤트 처리 모음.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            // 화면이 빠른조치/매뉴얼 이면 백버튼 2번누를 때 App종료
            if(menu_num == 0 || menu_num == 1){
                backPressCloseHandler.onBackBtnPressed();
            // 화면이 빠른조치의 상세화면에서 백버튼 누르면 빠른조치로
            }else if(menu_num == 2 && FRAGMENT_PAGE == 0){
                //mViewPager.setCurrentItem(FRAGMENT_PAGE1);
                page1Btn.performClick();
                menu_num=FRAGMENT_PAGE;
            // 화면이 매뉴얼의 상세화면에서 백버튼 누르면 매뉴얼로
            }else if(menu_num == 2 && FRAGMENT_PAGE == 1) {
                //mViewPager.setCurrentItem(FRAGMENT_PAGE2);
                page2Btn.performClick();
                menu_num=FRAGMENT_PAGE;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"[Settings] 메뉴가 선택 되었습니다.",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // 사이드메뉴 클릭시 발생하는 이벤트.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fast_sol) {
            Toast.makeText(getApplicationContext(),"[빠른해결] 메뉴가 선택 되었습니다.",Toast.LENGTH_SHORT).show();
            page1Btn.performClick();
        } else if (id == R.id.nav_menual) {
            Toast.makeText(getApplicationContext(),"[매뉴얼] 메뉴가 선택 되었습니다.",Toast.LENGTH_SHORT).show();
            page2Btn.performClick();
        } else if (id == R.id.nav_share) {
            Toast.makeText(getApplicationContext(),"[Share] 메뉴가 선택 되었습니다.",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(getApplicationContext(),"[Send] 메뉴가 선택 되었습니다.",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 화면 버튼 클릭시 이벤트 처리 모음.
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.Page1Btn:
                mViewPager.setCurrentItem(FRAGMENT_PAGE1);
                menu_num=0;
                page1Btn.setBackgroundColor(getResources().getColor(R.color.hint_foreground_material_light));
                page2Btn.setBackgroundColor(getResources().getColor(R.color.abc_background_cache_hint_selector_material_dark));
                setMenuIcon(menu_num);
                break;
            case R.id.Page2Btn:
                mViewPager.setCurrentItem(FRAGMENT_PAGE2);
                menu_num=1;
                page2Btn.setBackgroundColor(getResources().getColor(R.color.hint_foreground_material_light));
                page1Btn.setBackgroundColor(getResources().getColor(R.color.abc_background_cache_hint_selector_material_dark));
                setMenuIcon(menu_num);
                break;
        }
    }

    // 메뉴에 따른 메뉴 아이콘 변경 (페이드)
    private void setMenuIcon(int menu_num){
        if(menu_num == 0){
            AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
            fadeInAnimation.setDuration(500);
            fadeInAnimation.setFillAfter(true);
            menu_icon.setImageResource(R.drawable.hammer100);
            menu_icon.setAnimation(fadeInAnimation);
            menu_icon.setVisibility(View.VISIBLE);
            ((ImageView)findViewById(R.id.menu_icon)).getDrawable().setAlpha(500);
        }else if(menu_num == 1){
            AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
            fadeInAnimation.setDuration(500);
            fadeInAnimation.setFillAfter(true);
            menu_icon.setImageResource(R.drawable.document100);
            menu_icon.setAnimation(fadeInAnimation);
            menu_icon.setVisibility(View.VISIBLE);
            ((ImageView)findViewById(R.id.menu_icon)).getDrawable().setAlpha(500);
        }
    }

    // 인터넷 또는 WIFI가 연결되어 있는지 확인
    private boolean NetworkCheck(Context context){
        boolean isConnected = false;

        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mobile.isConnected() || wifi.isConnected()){
            isConnected = true;
        }else{
            isConnected = false;
        }
        return isConnected;
    }
}


