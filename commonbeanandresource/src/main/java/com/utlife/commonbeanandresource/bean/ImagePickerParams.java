package com.utlife.commonbeanandresource.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 选择图片传递的参数
 * Created by xuqiang on 2017/3/30.
 */

public class ImagePickerParams implements Parcelable {

    public int requestCode; //请求码
    public boolean isMultiMode;//是否多选模式
    public int selectLimit; //最多选择数量
    public boolean crop;  //是否进行裁剪

    public ImagePickerParams() {
    }

    public static ImagePickerParams getDefault(int requestCode){
        ImagePickerParams params = new ImagePickerParams();
        params.requestCode = requestCode;
        params.selectLimit = 1;
        params.isMultiMode = false;
        params.crop = false;
        return params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.requestCode);
        dest.writeByte(this.isMultiMode ? (byte) 1 : (byte) 0);
        dest.writeInt(this.selectLimit);
        dest.writeByte(this.crop ? (byte) 1 : (byte) 0);
    }

    protected ImagePickerParams(Parcel in) {
        this.requestCode = in.readInt();
        this.isMultiMode = in.readByte() != 0;
        this.selectLimit = in.readInt();
        this.crop = in.readByte() != 0;
    }

    public static final Creator<ImagePickerParams> CREATOR = new Creator<ImagePickerParams>() {
        @Override
        public ImagePickerParams createFromParcel(Parcel source) {
            return new ImagePickerParams(source);
        }

        @Override
        public ImagePickerParams[] newArray(int size) {
            return new ImagePickerParams[size];
        }
    };
}
