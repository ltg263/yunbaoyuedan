package com.yunbao.dynamic.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.custom.CheckImageView;
import com.yunbao.common.utils.ImageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.im.bean.ChatChooseImageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.yunbao.common.Constants.MAX_PHOTO_LENGTH;

public class SelectPhotoAdapter extends BaseRecyclerAdapter<ChatChooseImageBean, BaseReclyViewHolder> {

    private Drawable[]drawables;
    private ArrayList<String>selectImagePathList;
    private DataChangeLisnter dataChangeLisnter;

    public SelectPhotoAdapter(List<ChatChooseImageBean> data, Context context) {
        super(data);
        init(context);
    }
    public SelectPhotoAdapter(List<ChatChooseImageBean> data, ArrayList<String>selectList,Context context) {
        super(data);
        if(selectList!=null){
          this.selectImagePathList=selectList;
          restoreCheck();
        }
        init(context);
    }

    /*复原之前选中的*/
    private void restoreCheck() {
        if(selectImagePathList==null||mData==null){
            return;
        }
        for(ChatChooseImageBean chatChooseImageBean:mData){
            File file=chatChooseImageBean.getImageFile();
            String path=file!=null?file.getAbsolutePath():"";
            if(selectImagePathList.contains(path)){
                chatChooseImageBean.setChecked(true);
            }
        }
    }

    private void init(Context context){
        this.context=context;
        drawables= ImageUtil.getDrawalesByResource(this.context,R.mipmap.icon_photo_default,R.mipmap.icon_photo_select);
        setOnItemChildClickListener(onItemChildListner);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_select_photo;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, ChatChooseImageBean item) {
        helper.setImageResouceFile(item.getImageFile(),R.id.img_cover);
        CheckImageView checkImageView=helper.getView(R.id.check_imageView);
        checkImageView.addImageResouce(drawables);
        helper.addOnClickListener(R.id.check_imageView);
        checkImageView.setChecked(item.isChecked());


    }

    private  OnItemChildClickListener onItemChildListner=new OnItemChildClickListener(){
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
               if(selectImagePathList==null){
                   selectImagePathList=new ArrayList<>();
               }
                boolean isChange= changeSelect(position);
                if(dataChangeLisnter!=null&&isChange){
                    dataChangeLisnter.change();
                }

        }
    };

    @Override
    public void setData(List<ChatChooseImageBean> data) {
        mData = data;
        restoreCheck();
        notifyDataSetChanged();
    }

    private boolean changeSelect(int position) {
        ChatChooseImageBean chatChooseImageBean=mData.get(position);
        boolean isChangeCheck= !chatChooseImageBean.isChecked();
        boolean isChangeSuccess;
        if(isChangeCheck){
            if(isLimit()){
                return false;
            }
            isChangeSuccess=selectImagePathList.add(chatChooseImageBean.getImageFile().getAbsolutePath());
        }else{
            isChangeSuccess=selectImagePathList.remove(chatChooseImageBean.getImageFile().getAbsolutePath());
        }
        if(isChangeSuccess){
            chatChooseImageBean.setChecked(isChangeCheck);
            notifyItemChanged(position+getHeaderLayoutCount());
        }
        return isChangeSuccess;
    }


    public boolean isLimit(){
        int size=selectImagePathList==null?0:selectImagePathList.size();
        if(size==MAX_PHOTO_LENGTH){
            ToastUtil.show(WordUtil.getString(R.string.max_select_photo_length,MAX_PHOTO_LENGTH));
            return true;
        }
        return false;
    }

    public void addData(ChatChooseImageBean chatChooseImageBean){

        if(mData!=null) {
            if(selectImagePathList==null){
               selectImagePathList=new ArrayList<>();
            }
        }

           mData.add(chatChooseImageBean);
           String path=chatChooseImageBean.getImageFile().getAbsolutePath();
           if(chatChooseImageBean.isChecked()&&!selectImagePathList.contains(path)){
              selectImagePathList.add(path);
           }
    }

    public void setDataChangeLisnter(DataChangeLisnter dataChangeLisnter) {
        this.dataChangeLisnter = dataChangeLisnter;
    }
    public ArrayList<String> getSelectImagePathList() {
        return selectImagePathList;
    }



    public interface DataChangeLisnter{
        public void change();
    }

}
