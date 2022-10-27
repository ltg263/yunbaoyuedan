package com.yunbao.im.business;


/*mvp模式的Presnter层实现Presnter和view层的分离,方便以后更换SDK*/
public interface ICallPresnter extends IRoom {
    /*是否开启前置摄像头*/
    public void isFront(boolean isFront);
    /*开启关闭摄像头*/
    public void openCamera(boolean isOpen);
    /*是否开启免提*/
    public void isHandsFree(boolean isHandsFree);
    /*是否开启静音*/
    public void isMute(boolean isMute);
    /*是视频模式还是音频模式*/
    public void isVideo(boolean isVideo);
    /*获取当前的state层*/
    public CallLivingState getCallState();
    /*开启本地预览*/
    public void startSDKLocalPreview(boolean isPreview);
    /*持有View*/
    public void setCallView(IVideoCallView callView);
    /*资源释放方法*/
    public void release();
}
