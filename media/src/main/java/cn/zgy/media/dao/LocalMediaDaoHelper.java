package cn.zgy.media.dao;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseArray;


import java.util.ArrayList;
import java.util.List;

import cn.zgy.base.utils.UIUtils;
import cn.zgy.media.bean.MediaEntity;

/**
 * 媒体资源 DaoHelper
 *
 * @author a_liYa
 * @date 16/10/21 21:38.
 */
public class LocalMediaDaoHelper {
    /**
     * _id=2691
     * _data=/storage/sdcard0/dcim/100MEDIA/IMAG0109.jpg #相片的路径
     * _size=1503648
     * _display_name=IMAG0109.jpg #图片的文件名
     * mime_type=image/jpeg
     * title=IMAG0109 不含扩展名
     * date_added=1378981123
     * date_modified=1372592538
     * description=null
     * picasa_id=null
     * isprivate=null
     * latitude=11.3686  #这里的坐标
     * longitude=123.895 #这里也是坐标
     * datetaken=1372592531000
     * orientation=0
     * mini_thumb_magic=null
     * bucket_id=113065532 #特别注意这里，这个是对应另一个表中的ID
     * bucket_display_name=100MEDIA #还有这里，这里是上一个表中的名称，即文件夹的名称
     * volid=409208784
     * alive=1
     * v_folder=-0968832562;-0968832562;+
     * favorite=null
     * lock_screen=null
     * width=3264
     * height=1840
     * is_drm=0
     * htc_type=null
     * htc_filter=null
     */

    /**
     * 图库
     */
    private final Uri IMAGES_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    /**
     * 图库缩略图
     */
    private final Uri IMAGE_THUMBNAILS_URI = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    /**
     * 视频
     */
    private final Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    /**
     * 音频
     */
    private final Uri AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // 音频

    // 图片查询的字段
    private final String[] PROJECTIONS_IMAGE = new String[]{
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.DATA, // 路径
            MediaStore.Images.Media.DISPLAY_NAME, // 文件名
            MediaStore.Images.Media.SIZE, // 大小
    };

    // 图片缩略图
    private final String PROJECTIONS_THUMBNAIL[] = new String[]{
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.IMAGE_ID,
            MediaStore.Images.Thumbnails.DATA
    };

    // 视频查询的字段
    private final String[] PROJECTIONS_VIDEO = new String[]{
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
    };

    // 音频查询的字段
    private final String[] PROJECTIONS_AUDIO = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
    };

    /**
     * 查找图片缩略图
     *
     * @return 返回SparseArray key为image_id
     */
    private SparseArray<String> queryThumbnails() {
        SparseArray<String> imageThumbnails = new SparseArray<>();

        Cursor cursor = UIUtils.getContext().getContentResolver().query(IMAGE_THUMBNAILS_URI,
                PROJECTIONS_THUMBNAIL, null, null, null);

        if (!cursor.moveToFirst()) {
            return imageThumbnails;
        }

        int iImageIDColumnIndex = cursor.getColumnIndex(PROJECTIONS_THUMBNAIL[1]);
        int iDataColumnIndex = cursor.getColumnIndex(PROJECTIONS_THUMBNAIL[2]);

        while (!cursor.isAfterLast()) {
            int imageId = cursor.getInt(iImageIDColumnIndex);
            String thumbnail = cursor.getString(iDataColumnIndex);
            imageThumbnails.put(imageId, thumbnail);
            cursor.moveToNext();
        }
        cursor.close();

        return imageThumbnails;
    }

    /**
     * 查找图片文件
     *
     * @return 返回List集合
     */
    public List<MediaEntity> queryImageMedia() {
        // 要看情况,这是个异步耗时的操作
//        AppUtils.scanFile(null); // 全盘的扫描，否则新加入的图片是无法得到显示的

        // 先查询缩略图
        SparseArray<String> thumbnails = queryThumbnails();

        // 再查询图片
        List<MediaEntity> imageMedias = new ArrayList<>();

        Cursor cursor = UIUtils.getContext().getContentResolver().query(IMAGES_URI,
                PROJECTIONS_IMAGE, null,
                null, MediaStore.Images.Media.DATE_ADDED + " DESC"); // 根据添加日期降序排列
        if (cursor == null) {
            return imageMedias;
        }

        if (!cursor.moveToFirst()) {
            return imageMedias;
        }
        int iIdColumnIndex = cursor.getColumnIndex(PROJECTIONS_IMAGE[0]);
        int iPathColumnIndex = cursor.getColumnIndex(PROJECTIONS_IMAGE[1]);
        int iNameColumnIndex = cursor.getColumnIndex(PROJECTIONS_IMAGE[2]);
        int iSizeColumnIndex = cursor.getColumnIndex(PROJECTIONS_IMAGE[3]);
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(iIdColumnIndex);
            String path = cursor.getString(iPathColumnIndex);
            String name = cursor.getString(iNameColumnIndex);
            int size = cursor.getInt(iSizeColumnIndex);
            Uri uri = Uri.withAppendedPath(IMAGES_URI, Integer.toString(id));
            MediaEntity mediaFile = new MediaEntity(path, thumbnails.get(id), uri, name,
                    MediaEntity.MEDIA_TYPE_IMAGE, size);
            imageMedias.add(mediaFile);
            cursor.moveToNext();
        }
        cursor.close();

        return imageMedias;
    }

    /**
     * 查找视频文件
     *
     * @return 返回List集合
     */
    public List<MediaEntity> queryVideoMedia() {
        // 要看情况,这是个异步耗时的操作
//        AppUtils.scanFile(null); // 全盘的扫描，否则新加入的图片是无法得到显示的

        List<MediaEntity> videoMedias = new ArrayList<>();

        Cursor cursor = UIUtils.getContext().getContentResolver().query(VIDEO_URI,
                PROJECTIONS_VIDEO, null,
                null, MediaStore.Video.Media.DATE_ADDED + " DESC"); // 根据添加日期降序排列
        if (cursor == null) {
            return videoMedias;
        }

        if (!cursor.moveToFirst()) {
            return videoMedias;
        }
        int iIdColumnIndex = cursor.getColumnIndex(PROJECTIONS_VIDEO[0]);
        int iPathColumnIndex = cursor.getColumnIndex(PROJECTIONS_VIDEO[1]);
        int iNameColumnIndex = cursor.getColumnIndex(PROJECTIONS_VIDEO[2]);
        int iSizeColumnIndex = cursor.getColumnIndex(PROJECTIONS_VIDEO[3]);
        while (!cursor.isAfterLast()) {
            int iId = cursor.getInt(iIdColumnIndex);
            String path = cursor.getString(iPathColumnIndex);
            String name = cursor.getString(iNameColumnIndex);
            int size = cursor.getInt(iSizeColumnIndex);
            Uri uri = Uri.withAppendedPath(VIDEO_URI, Integer.toString(iId));
            MediaEntity mediaFile = new MediaEntity(path, null, uri, name, MediaEntity
                    .MEDIA_TYPE_VIDEO, size);
            videoMedias.add(mediaFile);
            cursor.moveToNext();
        }
        cursor.close();

        return videoMedias;

    }

    /**
     * 查找音频文件
     *
     * @return 返回List集合
     */
    public List<MediaEntity> queryAudioMedia() {
        // 要看情况,这是个异步耗时的操作
//        AppUtils.scanFile(null); // 全盘的扫描，否则新加入的图片是无法得到显示的

        List<MediaEntity> videoMedias = new ArrayList<>();

        Cursor cursor = UIUtils.getContext().getContentResolver().query(AUDIO_URI,
                PROJECTIONS_AUDIO, null,
                null, MediaStore.Audio.Media.DATE_ADDED + " DESC"); // 根据添加日期降序排列
        if (cursor == null) {
            return videoMedias;
        }

        if (!cursor.moveToFirst()) {
            return videoMedias;
        }
        int iIdColumnIndex = cursor.getColumnIndex(PROJECTIONS_AUDIO[0]);
        int iPathColumnIndex = cursor.getColumnIndex(PROJECTIONS_AUDIO[1]);
        int iNameColumnIndex = cursor.getColumnIndex(PROJECTIONS_AUDIO[2]);
        int iSizeColumnIndex = cursor.getColumnIndex(PROJECTIONS_AUDIO[3]);
        while (!cursor.isAfterLast()) {
            int iId = cursor.getInt(iIdColumnIndex);
            String path = cursor.getString(iPathColumnIndex);
            String name = cursor.getString(iNameColumnIndex);
            int size = cursor.getInt(iSizeColumnIndex);
            Uri uri = Uri.withAppendedPath(AUDIO_URI, Integer.toString(iId));
            MediaEntity mediaFile = new MediaEntity(path, null, uri, name, MediaEntity
                    .MEDIA_TYPE_AUDIO, size);
            videoMedias.add(mediaFile);
            cursor.moveToNext();
        }
        cursor.close();

        return videoMedias;

    }

}
