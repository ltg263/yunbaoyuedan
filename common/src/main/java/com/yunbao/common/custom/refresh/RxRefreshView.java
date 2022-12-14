package com.yunbao.common.custom.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yunbao.common.R;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.custom.ScrollSpeedLinearLayoutManger;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import java.util.List;
import io.reactivex.Observable;

public class RxRefreshView<T> extends FrameLayout implements View.OnClickListener {

    public static  final int STATE_NO_DATA=1;
    public static  final int STATE_ERROR=2;
    public static  final int STATE_HAVE_DATA=3;


    private Context mContext;
    private int mLayoutRes;
    private View mContentView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private ClassicsHeader mHeader;
    private ClassicsFooter mFooter;
    private RecyclerView mRecyclerView;
    private FrameLayout mEmptyLayout;//???????????????View
    private View mLoadFailureView;//????????????View
    private boolean mRefreshEnable;//????????????????????????
    private boolean mLoadMoreEnable;//????????????????????????
    private int mPageCount;//??????
    private int mItemCount;//?????????Item??????

    private DataListner<T> dataListner;
    private DataAdapter<T> dataAdapter;


    public RxRefreshView(Context context) {
        this(context, null);

    }

    public RecyclerView getRecyclerView(){
      return mRecyclerView;
    }


    public RxRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RxRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonRefreshView);
        mRefreshEnable = ta.getBoolean(R.styleable.CommonRefreshView_crv_refreshEnable, true);
        mLoadMoreEnable = ta.getBoolean(R.styleable.CommonRefreshView_crv_loadMoreEnable, true);
        mLayoutRes = ta.getResourceId(R.styleable.CommonRefreshView_crv_layout, R.layout.view_refresh_default);
        mItemCount = ta.getInteger(R.styleable.CommonRefreshView_crv_itemCount, 10);
        ta.recycle();
        init();
    }

    public void scrollPosition(int position){
        if(mRecyclerView!=null){
           mRecyclerView.scrollToPosition(position);
        }
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mLayoutRes, this, false);
        mContentView = view;
        addView(view);
        mSmartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//?????????????????????????????????????????????????????????
        mSmartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);//?????????????????????????????????Footer????????????
        mSmartRefreshLayout.setEnableOverScrollBounce(false);//?????????????????????????????????????????????true???
        mEmptyLayout = (FrameLayout) view.findViewById(R.id.no_data_container);
        mLoadFailureView = view.findViewById(R.id.load_failure);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });

        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                loadMore();
            }
        });
        mSmartRefreshLayout.setEnableRefresh(mRefreshEnable);
        mSmartRefreshLayout.setEnableLoadMore(mLoadMoreEnable);
        View btnReload = view.findViewById(R.id.btn_reload);
        if (btnReload != null) {
            btnReload.setOnClickListener(this);
        }
        int textColor = ContextCompat.getColor(mContext, R.color.textColor);
        mHeader = findViewById(R.id.header);
        mHeader.setAccentColor(textColor);
        mFooter = findViewById(R.id.footer);
        mFooter.setAccentColor(textColor);
        mFooter.setTextSizeTitle(14);
    }

    public void setRecycledViewPool(RecyclerView.RecycledViewPool recycledViewPool){
        mRecyclerView.setRecycledViewPool(recycledViewPool);
    }


    public void autoRefresh(){
        mSmartRefreshLayout.autoRefresh();
    }
    public void autoLoadMore(){
        mSmartRefreshLayout.autoLoadMore();
    }
    public boolean isRefreshing(){
        return mSmartRefreshLayout.getState()== RefreshState.Refreshing;
    }

    public interface DataListner<T>{
        public Observable<List<T>> loadData(int p);
        public void compelete(List<T> data);
        public void error(Throwable e);
    }

    private RefreshDataLisnter<T> mRefreshDataLisnter;

    public void setRefreshDataLisnter(RefreshDataLisnter refreshDataLisnter) {
        mRefreshDataLisnter = refreshDataLisnter;
    }

    public interface RefreshDataLisnter<T>{
        public void refreshData(List<T> t);
    }


    private DefaultObserver freshObserver= new DefaultObserver<List<T>>() {
        private  int mDataCount;
        @Override
        public void onNext(List<T> data) {

            RecyclerView.Adapter recyclerViewAdapter = mRecyclerView.getAdapter();
            if (recyclerViewAdapter == null) {
                return;
            }
            mDataCount = data==null?0:data.size();
            if(mRefreshDataLisnter!=null){
               mRefreshDataLisnter.refreshData(data);
            }

            if(dataAdapter!=null){
              dataAdapter.setData(data);
            }

            if(mDataCount==0){
              statusChange(STATE_NO_DATA);
            }else{
                statusChange(STATE_HAVE_DATA);
            }
            if(dataListner!=null){
                dataListner.compelete(dataAdapter.getArray());
            }


        }
        @Override
        public void onError(Throwable e) {
            super.onError(e);
            statusChange(STATE_ERROR);
            if(dataListner!=null){
                dataListner.error(e);
            }
        }
        @Override
        public void onComplete() {
            super.onComplete();
            if (mSmartRefreshLayout != null) {
                mSmartRefreshLayout.finishRefresh(true);
                if (mDataCount < mItemCount) {
                    mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                }
            }
        }
    };

    public void statusChange(int state) {
        if(state==STATE_ERROR){
            if (mLoadFailureView != null) {
                if (mLoadFailureView.getVisibility() != View.VISIBLE) {
                    if (mRecyclerView != null) {
                        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                        if (adapter != null && adapter.getItemCount() > 0) {
                            ToastUtil.show(R.string.load_failure);
                        } else {
                            mLoadFailureView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mLoadFailureView.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtil.show(R.string.load_failure);
                }
            }

            if (mEmptyLayout != null){
                mEmptyLayout.setVisibility(View.INVISIBLE);
            }
        }else if(state==STATE_NO_DATA){
            if (mLoadFailureView != null) {
                mLoadFailureView.setVisibility(View.INVISIBLE);
            }
            if (mEmptyLayout != null){
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
        }else{
            if (mLoadFailureView != null) {
                mLoadFailureView.setVisibility(View.INVISIBLE);
            }
            if (mEmptyLayout != null){
                mEmptyLayout.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void requestData(DefaultObserver observer) {
        if(dataListner!=null&& dataListner.loadData(mPageCount)!=null){
            dataListner.loadData(mPageCount).subscribe(observer);
        }
    }



    /*???????????????????????????adapter??????????????????*/
    public interface DataAdapter<E>{
        public void setData(List<E> data);
        public void appendData(List<E> data);
        public List<E> getArray();
        public RecyclerView.Adapter returnRecyclerAdapter();
        public void notifyReclyDataChange();
    }



    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setItemDecoration(ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }


    public void setNoDataTip(String text){
        TextView textView=mEmptyLayout.findViewById(R.id.tv_no_data);
        if(textView!=null){
            textView.setText(text);
        }
    }

    public void setNoDataTextColor(int color){
        TextView textView=mEmptyLayout.findViewById(R.id.tv_no_data);
        if(textView!=null){
            textView.setTextColor(getResources().getColor(color));
        }
    }

    public void setNoDataIcon(Drawable drawable){
        ImageView img =mEmptyLayout.findViewById(R.id.img_no_data);
        if(img!=null){
            img.setImageDrawable(drawable);
        }
    }


    public void setNoDataTip(int text){
        setNoDataTip(WordUtil.getString(text));
    }

    public void showLoading() {
        mPageCount = 1;
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.autoRefreshAnimationOnly();
        }
        if (mEmptyLayout != null && mEmptyLayout.getVisibility() == VISIBLE) {
            mEmptyLayout.setVisibility(INVISIBLE);
        }
        if (mLoadFailureView != null && mLoadFailureView.getVisibility() == VISIBLE) {
            mLoadFailureView.setVisibility(INVISIBLE);
        }
    }

    public void showEmpty() {
        if (mEmptyLayout != null && mEmptyLayout.getVisibility() != VISIBLE) {
            mEmptyLayout.setVisibility(VISIBLE);
        }
    }

    public void hideEmpty() {
        if (mEmptyLayout != null && mEmptyLayout.getVisibility() == VISIBLE) {
            mEmptyLayout.setVisibility(INVISIBLE);
        }
    }

    public void hideLoadFailure() {
        if (mLoadFailureView != null && mLoadFailureView.getVisibility() == VISIBLE) {
            mLoadFailureView.setVisibility(INVISIBLE);
        }
    }

    public void setAdapter(DataAdapter<T> adapter){
        dataAdapter=(DataAdapter<T>) adapter;
        mRecyclerView.setAdapter(dataAdapter.returnRecyclerAdapter());
    }

    public void initData() {
        refresh();
    }

    private void refresh() {
        if (dataListner != null) {
            mPageCount = 1;
            dataListner.loadData(mPageCount).subscribe(freshObserver);
        }
    }

    DefaultObserver loadMoreObserver=new DefaultObserver<List<T>>() {
        private int mDataCount;

        @Override
        public void onNext(List<T> data) {
            if (dataAdapter == null) {
                mPageCount--;
                return;
            }
            mDataCount = data==null?0:data.size();
            if(dataAdapter!=null){
              dataAdapter.appendData(data);
              int size= dataAdapter.getArray().size();
              if(size>0){
                  statusChange(STATE_HAVE_DATA);
              }else{
                  statusChange(STATE_NO_DATA);
              }
            }else{
             statusChange(STATE_NO_DATA);
            }
            if(dataListner!=null){
                dataListner.compelete(dataAdapter.getArray());
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            statusChange(STATE_ERROR);
            if(dataListner!=null){
                dataListner.error(e);
            }
        }

        @Override
        public void onComplete() {
            super.onComplete();
            if (mSmartRefreshLayout != null) {
                if (mDataCount < mItemCount) {
                    mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    mSmartRefreshLayout.finishLoadMore(true);
                }
            }
        }
    };


    private void loadMore() {
        if (dataListner != null) {
            mPageCount++;
            dataListner.loadData(mPageCount).subscribe(loadMoreObserver);
        }
    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int pageCount) {
        mPageCount = pageCount;
    }


    public int getItemCount() {
        return mItemCount;
    }

    public void setItemCount(int itemCount) {
        mItemCount = itemCount;
    }

    public void setRefreshEnable(boolean enable) {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.setEnableRefresh(enable);
        }
    }

    public void setLoadMoreEnable(boolean enable) {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.setEnableLoadMore(enable);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reload) {
            refresh();
        }
    }


    public interface DataHelper<T> {
        RefreshAdapter<T> getAdapter();

        void loadData(int p, HttpCallback callback);

        List<T> processData(String[] info);

        /**
         * ??????????????????
         *
         * @param list      Adapter??????????????????List
         * @param listCount Adapter????????????????????????
         */
        void onRefreshSuccess(List<T> list, int listCount);

        /**
         * ??????????????????
         */
        void onRefreshFailure();

        /**
         * ??????????????????
         *
         * @param loadItemList  ????????????????????????
         * @param loadItemCount ????????????????????????
         */
        void onLoadMoreSuccess(List<T> loadItemList, int loadItemCount);

        /**
         * ????????????
         */
        void onLoadMoreFailure();
    }

    /**
     * ??????????????????
     */
    public void setEmptyLayoutId(int noDataLayoutId) {
        if (mEmptyLayout != null) {
            mEmptyLayout.removeAllViews();
            if (noDataLayoutId > 0) {
                View v = LayoutInflater.from(mContext).inflate(noDataLayoutId, mEmptyLayout, false);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                params.gravity = Gravity.CENTER;
                v.setLayoutParams(params);
                mEmptyLayout.addView(v);
            }
        }
    }


    public void setDataListner(DataListner<T> dataListner) {
        this.dataListner = dataListner;
    }


    public View getContentView() {
        return mContentView;
    }


    @Deprecated
    public void setRecyclerViewAdapter(RefreshAdapter adapter) {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }






    public void setReclyViewSetting(ReclyViewSetting reclyViewSetting){
        mRecyclerView.setLayoutManager(reclyViewSetting.layoutManager);
        if(mRecyclerView.getItemDecorationCount()==0&&reclyViewSetting.itemDecoration!=null){
           mRecyclerView.addItemDecoration(reclyViewSetting.itemDecoration);
        }
        mRecyclerView.setHasFixedSize(reclyViewSetting.hasFixedSize);
    }

    public static class ReclyViewSetting{
        private RecyclerView.LayoutManager layoutManager;
        private ItemDecoration itemDecoration;
        private  boolean hasFixedSize;

        public ReclyViewSetting(RecyclerView.LayoutManager layoutManager, ItemDecoration itemDecoration,boolean hasFixedSize) {
            this.layoutManager = layoutManager;
            this.itemDecoration = itemDecoration;
            this.hasFixedSize=hasFixedSize;
        }



        public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        public static ReclyViewSetting createLinearSetting(Context context){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            ItemDecoration decoration = new ItemDecoration(context, 0xffdd00, 5, 5);
            ReclyViewSetting reclyViewSetting=new ReclyViewSetting(linearLayoutManager,decoration,true);
            return reclyViewSetting;
        }


        public static ReclyViewSetting createLinearSetting(Context context,int span){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            ItemDecoration decoration = new ItemDecoration(context, 0xffdd00, span, span);
            ReclyViewSetting reclyViewSetting=new ReclyViewSetting(linearLayoutManager,decoration,true);
            return reclyViewSetting;
        }

        public static ReclyViewSetting createGridSetting(Context context,int spanCount){
            return createGridSetting(context,spanCount,5);
        }

        public static ReclyViewSetting createGridSetting(Context context,int spanCount,int divider){
            GridLayoutManager gridLayoutManager= new GridLayoutManager(context,spanCount);
            ItemDecoration decoration = new ItemDecoration(context, 0xffdd00, divider, divider);
            ReclyViewSetting reclyViewSetting=new ReclyViewSetting(gridLayoutManager,decoration,true);
            return reclyViewSetting;
        }

        public static ReclyViewSetting creatStaggeredGridSetting(Context context,int spanCount){
            StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL);
            ReclyViewSetting reclyViewSetting=new ReclyViewSetting(gridLayoutManager,null,true);
            return reclyViewSetting;
        }

        public static ReclyViewSetting creatScrollSpeedSetting(Context context,int spanCount){
            ScrollSpeedLinearLayoutManger gridLayoutManager=new ScrollSpeedLinearLayoutManger(context,StaggeredGridLayoutManager.VERTICAL,false);
            ReclyViewSetting reclyViewSetting=new ReclyViewSetting(gridLayoutManager,null,true);
            return reclyViewSetting;
        }


        public void settingRecyclerView(RecyclerView recyclerView){
            if(recyclerView.getLayoutManager()!=null){
                return;
            }
            recyclerView.setLayoutManager(layoutManager);
            if(recyclerView.getItemDecorationCount()==0&&itemDecoration!=null){
                recyclerView.addItemDecoration(itemDecoration);
            }
            recyclerView.setHasFixedSize(hasFixedSize);
        }
    }





}
