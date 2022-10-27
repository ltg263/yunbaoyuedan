package com.yunbao.chatroom.adapter;


import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;

import com.yunbao.common.CommonAppContext;
import com.yunbao.common.adapter.base.BaseMutiRecyclerAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.GiftUser;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.LiveBean;

import java.util.ArrayList;
import java.util.List;

public class GiftUserAdapter extends BaseMutiRecyclerAdapter<GiftUser, BaseReclyViewHolder> {
    private int selTextColor;
    private int normalTextColor;
    private OnItemClicker mOnItemClicker;

    public GiftUserAdapter(List<GiftUser> data) {
        super(data);
        selTextColor= CommonAppContext.sInstance.getResourceColor(R.color.white);
        normalTextColor= CommonAppContext.sInstance.getResourceColor(R.color.textColor);
        addItemType(GiftUser.GIFT_HEAD,R.layout.item_recly_gift_reward_user);
        addItemType(GiftUser.GIFT_NORMAL,R.layout.item_recly_gift_reward_user);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                GiftUser giftUser= mData.get(position);
                boolean isChecked=giftUser.isCheck();

                if(giftUser.getItemType()==GiftUser.GIFT_HEAD){
                    for(GiftUser user:mData){
                       user.setCheck(!isChecked);
                    }
                    notifyReclyDataChange();
                }else{
                    boolean checked=!isChecked;
                    int checkNum=0;
                    giftUser .setCheck(checked);
                    notifyItemChanged(position);
                    for(GiftUser user:mData){
                        if(user.getItemType()!=GiftUser.GIFT_HEAD&&user.isCheck()){
                            checkNum+=1;
                        }
                    }
                    GiftUser giftHeadUser=  ListUtil.safeGetData(mData,0);
                    if(giftHeadUser==null){
                        return;
                    }
                    boolean allChecked=false;
                    if(checkNum==size()-1){
                        allChecked=true;
                    }else{
                        allChecked=false;
                    }
                    if(giftHeadUser.isCheck()!=allChecked){
                        giftHeadUser.setCheck(allChecked);
                        notifyItemChanged(0);
                    }

                }

                if(mOnItemClicker!=null){
                    mOnItemClicker.click();
                }
            }
        });
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, GiftUser item) {
        switch (helper.getItemViewType()){
            case GiftUser.GIFT_HEAD:
                convertHead(helper,item);
                break;
            case GiftUser.GIFT_NORMAL:
                convertNormal(helper,item);
                break;
            default:
                break;
        }
    }

    public boolean haveSelect(){
        if (!ListUtil.haveData(mData)){
            return false;
        }
        boolean haveCheck=false;
        for(GiftUser giftUser:mData){
            if(!StringUtil.equals(giftUser.getUid(),GiftUser.ALL_UID)){
                if(giftUser.isCheck()){
                  haveCheck=true;
                  return haveCheck;
                }
            }
        }
        return haveCheck;
    }

    public void select(String uid){
     int size=size();
     if(size==0|| TextUtils.isEmpty(uid)){
         return;
     }
     for(int i=0;i<size;i++){
         GiftUser giftUser=mData.get(i);
         if(StringUtil.equals(uid,giftUser.getUid())){
            giftUser.setCheck(true);
            notifyItemChanged(i);
            return;
         }
     }

    }

    public static List<GiftUser>createData(LiveBean liveBean,List<LiveAnthorBean>seatList){
        List<LiveAnthorBean>list=seatList;
        List<GiftUser>array=new ArrayList<>();
        array.add(GiftUser.createHeadUser());
        UserBean hostUser=new UserBean();
        hostUser.setId(liveBean.getUid());
        hostUser.setAvatar(liveBean.getAvatar());
        array.add(GiftUser.createNormalUser(0,hostUser));

        if(seatList==null){
            return array;
        }
        int size=list.size();



        for(int i=0;i<size;i++){
            LiveAnthorBean liveAnthorBean=list.get(i);
            UserBean userBean=liveAnthorBean.getUserBean();
            if(userBean!=null){
               array.add(GiftUser.createNormalUser(i+1,userBean)) ;
            }
        }
        return array;
    }

    public String getUids(){
       if(!ListUtil.haveData(mData)) {
           return null;
       }
        StringBuilder builder=new StringBuilder();
       for(GiftUser giftUser:mData){
           String uid=giftUser.getUid();
           if(!TextUtils.isEmpty(uid)&&!StringUtil.equals(uid,GiftUser.ALL_UID)&&giftUser.isCheck()){
               builder.append(uid)
               .append(",");
           }
       }
       int length=builder.length();
       if(length>0){
          return builder.deleteCharAt(length-1).toString();
       }
       return null;
    }




    private void convertNormal(BaseReclyViewHolder helper, GiftUser item) {
        helper.setText(R.id.tv_tag,item.getMaiXu());
        helper.setImageUrl(item.getAvator(),R.id.img_avator);
        if(item.isCheck()){
            helper.setImageResouceId(item.getSelectBackGround(),R.id.img_bg);
            helper.setTextColor(R.id.tv_tag,selTextColor);
            helper.setBackgroundRes(R.id.tv_tag,R.mipmap.icon_reword_select);
        }else{
            helper.setImageResouceId(item.getNormalBackGround(),R.id.img_bg);
            helper.setBackgroundRes(R.id.tv_tag,R.mipmap.icon_reward_default);
            helper.setTextColor(R.id.tv_tag,normalTextColor);
        }
    }


    private void convertHead(BaseReclyViewHolder helper, GiftUser item) {
        helper.setText(R.id.tv_tag,item.getMaiXu());
        if(item.isCheck()){
            helper.setImageResouceId(item.getSelectBackGround(),R.id.img_bg);
            helper.setBackgroundRes(R.id.tv_tag,R.mipmap.icon_reword_select);
            helper.setTextColor(R.id.tv_tag,selTextColor);
        }else{
            helper.setImageResouceId(item.getNormalBackGround(),R.id.img_bg);
            helper.setBackgroundRes(R.id.tv_tag,R.mipmap.icon_reward_default);
            helper.setTextColor(R.id.tv_tag,normalTextColor);
        }
    }

    public void setOnItemClicker(OnItemClicker onItemClicker) {
        mOnItemClicker = onItemClicker;
    }

    public static interface  OnItemClicker{
        public void click();
    }
}
