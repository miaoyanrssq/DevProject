package cn.zgy.news.callback;


/**
* ItemTouchHelper.Callback 接口方法的再次抽取
* @author zhengy
* create at 2018/9/12 上午9:57
**/
public interface ItemTouchCallback {

    boolean onMove(int fromPosition, int toPosition);

    void onSwiped(int position);

}
