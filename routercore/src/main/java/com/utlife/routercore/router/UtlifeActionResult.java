package com.utlife.routercore.router;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wanglei on 16/6/14.
 */
public class UtlifeActionResult<T> implements Parcelable {
    public static final int CODE_SUCCESS = 0x0000;
    public static final int CODE_ERROR = 0x0001;
    public static final int CODE_NOT_FOUND = 0X0002;
    public static final int CODE_INVALID = 0X0003;
    public static final int CODE_ROUTER_NOT_REGISTER = 0X0004;
    public static final int CODE_CANNOT_BIND_LOCAL = 0X0005;
    public static final int CODE_REMOTE_EXCEPTION = 0X0006;
    public static final int CODE_CANNOT_BIND_WIDE = 0X0007;
    public static final int CODE_TARGET_IS_WIDE = 0X0008;
    public static final int CODE_WIDE_STOPPING = 0X0009;

    private int code;
    private String msg;
    private String data;
    private T result;

    UtlifeActionResult(){

    }

    private UtlifeActionResult(Builder builder) {
        code = builder.code;
        msg = builder.msg;
        data = builder.data;
        result = (T) builder.result;
    }

    protected UtlifeActionResult(Parcel in) {
        code = in.readInt();
        msg = in.readString();
        data = in.readString();
        result = (T) in.readParcelable(this.getClass().getClassLoader());
    }

    public static final Creator<UtlifeActionResult> CREATOR = new Creator<UtlifeActionResult>() {
        @Override
        public UtlifeActionResult createFromParcel(Parcel in) {
            return new UtlifeActionResult(in);
        }

        @Override
        public UtlifeActionResult[] newArray(int size) {
            return new UtlifeActionResult[size];
        }
    };

    public Object getResult() {
        return result;
    }

    public String getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "MaActionResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                ", object=" + result +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(msg);
        dest.writeString(data);
        dest.writeParcelable((Parcelable) result, flags);
    }


    public static final class Builder<T> {
        private int code;
        private String msg;
        private String data;
        private T result;

        public Builder() {
        }

        public Builder code(int val) {
            code = val;
            return this;
        }

        public Builder msg(String val) {
            msg = val;
            return this;
        }

        public Builder data(String val) {
            data = val;
            return this;
        }

        public Builder result(T val) {
            result = val;
            return this;
        }

        public UtlifeActionResult build() {
            return new UtlifeActionResult(this);
        }
    }
}
