package com.yunbao.dynamic.adapter;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.base.BaseMutiRecyclerAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.MyDynamicBean;
import com.yunbao.dynamic.bean.ResourseBean;
import com.yunbao.dynamic.ui.activity.DynamicDetailActivity;
import com.yunbao.dynamic.ui.activity.DynamicVideoActivity;
import com.yunbao.dynamic.ui.activity.GalleryActivity;
import com.yunbao.dynamic.widet.VoicePlayView;
import java.util.List;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;
import static com.yunbao.common.Constants.DYNAMIC_VIDEO;
import static com.yunbao.common.Constants.DYNAMIC_VOICE;

/*一个类型的adapter只有一种类型的数据*/
public class DynamicResourceAdapter  extends BaseMutiRecyclerAdapter<ResourseBean, BaseReclyViewHolder> {
    protected Activity activity;
    private View mLastView;

    private MyDynamicBean mDynamicBean;

    public DynamicResourceAdapter(List<ResourseBean> data, Activity activity) {
        super(data);
        this.activity=activity;
        addItemType(DYNAMIC_PHOTO, R.layout.item_recly_pub_image);
        addItemType(DYNAMIC_VIDEO,R.layout.item_recly_pub_video);
        addItemType(DYNAMIC_VOICE,R.layout.item_recly_pub_voice);
        setOnItemClickListener(onItemClickListener);
    }

    public void setDynamicBean(MyDynamicBean bean){
        mDynamicBean = bean;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, ResourseBean item) {
        mLastView=helper.itemView;
        switch (helper.getItemViewType()){
            case DYNAMIC_PHOTO:
                convertPhoto(helper,item);
                break;
            case DYNAMIC_VIDEO:
                convertVideo(helper,item);
                break;
            case DYNAMIC_VOICE:
                convertVoice(helper,item);
                break;
        }
    }

    protected void convertVoice(BaseReclyViewHolder helper, ResourseBean item) {
          VoicePlayView voicePlayView=helper.getView(R.id.voiceView);
        if(item.getObject()!=null&&item.getObject() instanceof Integer){
            int totalTime= (int) item.getObject();
            voicePlayView.setVoiceInfo(totalTime,item.getResouce());
        }else {
            voicePlayView.setVoiceInfo(0,item.getResouce());
        }
    }

    protected void convertVideo(BaseReclyViewHolder helper, ResourseBean item) {
        helper.setImageUrl((String)item.getObject(),R.id.image_thumb);
    }
    protected void convertPhoto(BaseReclyViewHolder helper, ResourseBean item) {
        String itemString=item.getResouce();
        helper.getView(R.id.image).setTransitionName(WordUtil.getString(R.string.transition_image)+helper.getLayoutPosition());
        if(TextUtils.isEmpty(itemString)||itemString.equals("-1")){
          helper.setImageResouceId(R.mipmap.icon_photo_add,R.id.image);
        }else{
          helper.setImageUrl(itemString,R.id.image);
        }
    }

    private  OnItemClickListener onItemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
             ResourseBean bean= ListUtil.safeGetData(mData,position);
             if(bean==null){
                return;
             }
             int type=bean.getItemType();
            switch (type){
                case DYNAMIC_PHOTO:
                    clickPhoto(bean.getResouce(),position,view);
                    break;
                case DYNAMIC_VIDEO:
                    if (CommonAppConfig.getInstance().isFloatButtonShowing()){
                        ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.can_not_do_this_in_opening_live_room));
                        return;
                    }
                    clickVideo(bean.getResouce(), (String) bean.getObject(),position);
                    break;
                case DYNAMIC_VOICE:
                    if (CommonAppConfig.getInstance().isFloatButtonShowing()){
                        ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.can_not_do_this_in_opening_live_room));
                        return;
                    }
                    clickVoice(bean.getResouce(),position);
                    break;
            }
        }
    };
    /*咱不可用哦*/
    protected void clickVoice(String resouce, int position) {
    }
    protected void clickVideo(String resouce,String cover, int position) {
        if (mDynamicBean != null){
            DynamicDetailActivity.forward(mContext,mDynamicBean);
        }
     //   DynamicVideoActivity.forword( mContext,DynamicVideoActivity.TYPE_WATCH,resouce,cover);
    }

    protected void clickPhoto(String resouce, int position, View view) {
        GalleryActivity.forward((Activity) mContext,view,ResourseBean.valuesTo(mData),position,GalleryActivity.TYPE_WATCH);
    }
    public  GridLayoutManager createDefaultGridMannger(){
        GridLayoutManager gridLayoutManager=new GridLayoutManager(mContext,6){
            @Override
            public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
                spanSizeLookup=new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int size=size();
                        if(size==1){
                            return 4;
                        }else if(size==2){
                            return 3;
                        }
                        return 2;
                    }
                };
                super.setSpanSizeLookup(spanSizeLookup);
            }
        };
        return gridLayoutManager;
    }


    public boolean contain(int hashCode){
       return mLastView!=null&&mLastView.hashCode()==hashCode;
    }
}
