package cn.zgy.detail;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;

import org.json.JSONObject;

import cn.zgy.base.BaseAgentWebFragment;
import cn.zgy.detail.androidjs.AndroidInterface;
/**
* android  js 调用
* @author zhengy
* create at 2018/9/20 下午5:13
**/
public class JsFragment extends BaseAgentWebFragment{


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_js, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mAgentWeb!=null){
            //注入对象
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface(mAgentWeb,this.getActivity()));
        }
        view.findViewById(R.id.callJsNoParamsButton).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.callJsOneParamsButton).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.callJsMoreParamsButton).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.jsJavaCommunicationButton).setOnClickListener(mOnClickListener);
    }


    private View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {


            int i = v.getId();
            if (i == R.id.callJsNoParamsButton) {
                mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroid");

            } else if (i == R.id.callJsOneParamsButton) {
                mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidParam", "Hello ! Agentweb");

            } else if (i == R.id.callJsMoreParamsButton) {
                mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidMoreParams", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("Info", "value:" + value);
                    }
                }, getJson(), "say:", " Hello! Agentweb");


            } else if (i == R.id.jsJavaCommunicationButton) {
                mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidInteraction", "你好Js");

            }

        }
    };

    private String getJson(){

        String result="";
        try {

            JSONObject mJSONObject=new JSONObject();
            mJSONObject.put("id",1);
            mJSONObject.put("name","Agentweb");
            mJSONObject.put("age",18);
            result= mJSONObject.toString();
        }catch (Exception e){

        }

        return result;
    }

    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return findViewById(R.id.container);
    }

    @Nullable
    @Override
    protected String getUrl() {
        return "file:///android_asset/hello.html";
    }
}
