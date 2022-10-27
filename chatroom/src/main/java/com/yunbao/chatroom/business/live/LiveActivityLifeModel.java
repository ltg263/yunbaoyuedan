package com.yunbao.chatroom.business.live;

import android.text.TextUtils;

import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.DataObsever;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/*数据逻辑要在model层,尽量不要在view层进行直接修改从而导致混乱*/
public class LiveActivityLifeModel<T extends SocketProxy> extends LifeObjectHolder {
    protected T mSocketProxy; //socket代理
    protected LiveBean mLiveBean; //聊天室的持有数据
    protected String skillId; //聊天室技能id
    protected int mLiveNum;   //聊天室实时人数
    protected float mLiveCharm; //聊天室魅力值
    protected List<LiveAnthorBean>mSeatList; //座位列表

    protected DataObsever<List<LiveAnthorBean>>mLiveSeatDataObsever; //观察座位的数据变化
    protected DataObsever<Boolean>mSpeakStateDataObsever;  //监听自己发言状态的变化
    protected DataObsever<Integer>mLiveOnlineNumObserver; //监听在线人数的变化

    protected boolean mIsBossMode;   //标记这个聊天室是否boss模式
    private int mLiveType;      //当前聊天室的直播类型

    /*必须设置在刚初始化之后传入*/
    public void init(boolean isBossMode){
        mIsBossMode=isBossMode;
    }

    public T getSocketProxy() {
        return mSocketProxy;
    }

    public void setSocketProxy(T t) {
        mSocketProxy = t;
    }
    public LiveBean getLiveBean() {
        return mLiveBean;
    }

    public void setLiveBean(LiveBean liveBean) {
        mLiveBean = liveBean;
        if(mLiveBean!=null){
          initSeatList(liveBean.getSits());
        }
    }

    /*合并livebean的数据*/
    public void margeLiveBean(LiveBean liveBean){
        if(mLiveBean==null){
           mLiveBean=liveBean;
        }
        mLiveBean.setVotestotal(liveBean.getVotestotal());
        mLiveBean.setChatserver(liveBean.getChatserver());
        mLiveBean.setIsattent(liveBean.getIsattent());
        mLiveBean.setSits(liveBean.getSits());
    }

    /*生成座位列表，并将接口获得占座情况赋值给座位*/
    public void initSeatList(List<UserBean>userBeanSitList) {
        if(ListUtil.haveData(mSeatList)){
            return;
        }
        if(mSeatList==null){
           mSeatList=new ArrayList<>();
        }
        for(int i=0;i<8;i++){
            LiveAnthorBean liveAnthorBean=new LiveAnthorBean();
            if(i==7){
                liveAnthorBean.setBoss(mIsBossMode);
            }
            UserBean userBean= ListUtil.safeGetData(userBeanSitList,i);
            if(userBean!=null&&!TextUtils.isEmpty(userBean.getId())){
                liveAnthorBean.setUserBean(userBean);
            }else{
                liveAnthorBean.setUserBean(null);
            }
          mSeatList.add(liveAnthorBean) ;
        }
    }

    /*移除座位上的用户*/
    public int downSeat(UserBean user,boolean isBoosDownAll){
        if(!ListUtil.haveData(mSeatList)||user==null){
            return -1;
        }
        int index=mSeatList.indexOf(user);  //这里通过重写equals()方法更改集合判定规则
        if(index==-1){
           return index;
        }
        LiveAnthorBean liveAnthorBean=mSeatList.get(index);
        if(liveAnthorBean.isBoss()&&isBoosDownAll){
            clearAllSeat();
        }else{
            clearSeat(liveAnthorBean);
        }
        getLiveSeatDataObsever().observer(mSeatList);
        return index;
    }
    public int downSeat(UserBean user){
        return downSeat(user,false);
    }



    /*清理所有座位*/
    public void clearAllSeat() {
        if(mSeatList==null){
            return;
        }
        for(LiveAnthorBean temp:mSeatList){
            clearSeat(temp);
        }
    }
    /*清理指定的座位*/
    private void clearSeat(LiveAnthorBean liveAnthorBean) {
        liveAnthorBean.setCurrentSpeak(false);
        liveAnthorBean.setUserBean(null);
        liveAnthorBean.setOpenWheat(false);
    }


    public List<LiveAnthorBean> getSeatList() {
        return mSeatList;
    }

    public DataObsever<List<LiveAnthorBean>> getLiveSeatDataObsever() {
        if(mLiveSeatDataObsever==null){
           mLiveSeatDataObsever=new DataObsever<>();
        }
        return mLiveSeatDataObsever;
    }

    /*是否在座位上*/
    public boolean isOnWheat(UserBean userBean) {
        if( mLiveBean==null||userBean==null||!ListUtil.haveData(mSeatList)){
           return false;
        }
        if(StringUtil.equals(userBean.getId(),mLiveBean.getUid())){
            return true;
        }
        return mSeatList.indexOf(userBean)!=-1;
    }

    //上普通麦 修改内存数据
    public int upNormalWheat(UserBean userBean,int positon) {
       return upWheat(userBean,positon);
    }

    //上老板麦 修改内存数据
    public int upBossWheat(UserBean userBean) {
      return  upWheat(userBean,7);
    }

    //上麦
    private int upWheat(UserBean userBean,int poistion) {
        if(userBean==null||!ListUtil.haveData(mSeatList)||isOnWheat(userBean)){
            return -1;
        }
        LiveAnthorBean bean=mSeatList.get(poistion);
        bean.setUserBean(userBean);
        getLiveSeatDataObsever().observer(mSeatList);
        return poistion;
    }


    /*判断列表上是否有大神吗*/
    public boolean hasNormalUser() {
        boolean haveNormalUser=false;
        List<LiveAnthorBean>list=mSeatList;
        if(!ListUtil.haveData(list)){
            return haveNormalUser;
        }
        for(LiveAnthorBean var:list){
            if(var.getUserBean()!=null&!var.isBoss()){
                haveNormalUser=true;
                return haveNormalUser;
            }
        }
        return haveNormalUser;
    }

    /*设置观众是否可以说话*/
    public void setAudienceCanSpeakState(boolean isOpenWheat){
        if(mLiveBean!=null){
           mLiveBean.setAudienceCanNotSpeak(isOpenWheat);
        }
    }

    public int setAudienceSpeakState(String uid,boolean speakState){
        int index=-1;
        if(!ListUtil.haveData(mSeatList)|| TextUtils.isEmpty(uid)){
            return index;
        }
        UserBean compareUser=UserBean.getCompareUserBean(uid);
        index=mSeatList.indexOf(compareUser);
        if(index==-1){
            return index;
        }
        LiveAnthorBean liveAnthorBean= mSeatList.get(index);
        liveAnthorBean.setCurrentSpeak(speakState);
        return index;
    }


    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }



    public void changeSpeakState(boolean isSpeak){
        getSpeakStateObsever().observer(isSpeak);
    }

    public DataObsever<Boolean> getSpeakStateObsever() {
        if(mSpeakStateDataObsever==null){
            mSpeakStateDataObsever=new DataObsever<>();
        }
        return mSpeakStateDataObsever;
    }


    public void setLiveNum(int num){
        mLiveNum=num;
        getLiveOnlineNumObserver().observer(mLiveNum);
    }

    private Disposable mDisposable;
    public DataObsever<Integer> getLiveOnlineNumObserver() {
        if(mLiveOnlineNumObserver==null){
            mLiveOnlineNumObserver=new DataObsever<>();
            startGetNewNum();
        }

        return mLiveOnlineNumObserver;
    }

    /*开始定时一分钟去调整聊天室在线人数*/
    private void startGetNewNum() {
        dispose();
        mDisposable=Observable.interval(1, TimeUnit.MINUTES).take(100000).observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Long, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Long aLong) throws Exception {
                        String liveUid=mLiveBean==null?null:mLiveBean.getUid();
                        String steam=mLiveBean==null?null:mLiveBean.getStream();
                        return ChatRoomHttpUtil.getUserNums(liveUid,steam);
                    }
                }
                ).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer num) throws Exception {
                        mLiveNum=num;
                        if(mLiveBean!=null){
                            mLiveBean.setNums(num);
                        }
                        getLiveOnlineNumObserver().observer(mLiveNum);
                    }
                });
    }


    private void dispose() {
       if(mDisposable!=null&&!mDisposable.isDisposed()) {
           mDisposable.dispose();
       }
    }

    /*修改聊天室在线人数*/
    public void changeOnLineNum(String uid, boolean isEnter) {
        if(mLiveBean==null){
           return;
        }
        int num=mLiveBean.getNums();
        if(isEnter){
           num=num+1;
        }else if(num>=1){
          num=num-1;
        }
        mLiveBean.setNums(num);
        getLiveOnlineNumObserver().observer(num);
    }

    public float getLiveCharm() {
        return mLiveCharm;
    }


    //直接设置魅力值
    public void setLiveCharm(float liveCharm) {
        mLiveCharm = liveCharm;
    }
    //直接设置魅力值
    public void setLiveCharm(String liveCharm) {
        mLiveCharm=Float.parseFloat(liveCharm);
    }
    //魅力值增加
    public void  addLiveCharm(String uid,float liveCharm) {
        String liveUid=mLiveBean==null?null:mLiveBean.getUid();
        if(StringUtil.equals(uid,liveUid)){
            mLiveCharm +=liveCharm;
        }
    }

    public int getLiveType() {
        if(mLiveType==0&&mLiveBean!=null){
            mLiveType=mLiveBean.getType();
        }
        return mLiveType;
    }

    public void setLiveType(int liveType) {
        mLiveType = liveType;
    }

    @Override
    public void release() {
        if(mSocketProxy!=null){
          mSocketProxy.release();
        }
        mSocketProxy=null;
        mLiveBean=null;
        if(mLiveSeatDataObsever!=null){
            mLiveSeatDataObsever.release();
            mLiveSeatDataObsever=null;
        }
        if(mSpeakStateDataObsever!=null){
            mSpeakStateDataObsever.release();
            mSpeakStateDataObsever=null;
        }
        if(mLiveOnlineNumObserver!=null){
            mLiveOnlineNumObserver.release();
            mLiveOnlineNumObserver=null;
        }
        dispose();
        L.e("release==");
    }

}
