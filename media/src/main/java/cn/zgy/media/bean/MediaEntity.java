package cn.zgy.media.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 本地媒体资源文件 JavaBean
 *
 * @author a_liYa
 * @date 16/10/21 21:25.
 */
public class MediaEntity implements Parcelable {

    /**
     * 类型图片
     */
    public static final int MEDIA_TYPE_IMAGE = 1;
    /**
     * 类型视频
     */
    public static final int MEDIA_TYPE_VIDEO = 2;
    /**
     * 类型音频
     */
    public static final int MEDIA_TYPE_AUDIO = 3;

    private String mPath;
    private String mThumbnail;
    private Uri mUri;
    private String mName;
    private int mMediaType;
    private int mSize;
    private boolean isSelected;

    public MediaEntity() {
    }

    public MediaEntity(String path, String thumbnail, Uri uri, String name, int mediaType, int size) {
        mPath = path;
        mThumbnail = thumbnail;
        mUri = uri;
        mName = name;
        mMediaType = mediaType;
        mSize = size;
    }

    protected MediaEntity(Parcel in) {
        mPath = in.readString();
        mThumbnail = in.readString();
        mUri = in.readParcelable(Uri.class.getClassLoader());
        mName = in.readString();
        mMediaType = in.readInt();
        mSize = in.readInt();
    }

    public static final Creator<MediaEntity> CREATOR = new Creator<MediaEntity>() {
        @Override
        public MediaEntity createFromParcel(Parcel in) {
            return new MediaEntity(in);
        }

        @Override
        public MediaEntity[] newArray(int size) {
            return new MediaEntity[size];
        }
    };

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getMediaType() {
        return mMediaType;
    }

    public void setMediaType(int mediaType) {
        mMediaType = mediaType;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(mThumbnail);
        dest.writeParcelable(mUri, flags);
        dest.writeString(mName);
        dest.writeInt(mMediaType);
        dest.writeInt(mSize);
    }
}
