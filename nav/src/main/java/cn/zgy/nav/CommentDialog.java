package cn.zgy.nav;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CommentDialog extends AlertDialog implements View.OnClickListener {

    Context context;
    View view;

    TextView tvMsg;
    TextView tvTitle;

    Button btnLeft;
    Button btnRight;

    private View.OnClickListener mOnClickListener;

    public CommentDialog(Context context) {
        super(context, android.R.style.Theme_Dialog);
        this.context = context;
        initView();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        configDialog();
    }

    private void configDialog() {
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        //设置对话框居中
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = getScreenW() * 5 / 6;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(
                R.layout.commentdialog, null);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvMsg = (TextView) view.findViewById(R.id.tv_message);
        btnLeft = (Button) view.findViewById(R.id.btn_left);
        btnRight = (Button) view.findViewById(R.id.btn_right);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        dismiss();
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }

    public void setBuilder(Builder builder) {
        if (builder == null) return;
        if (!TextUtils.isEmpty(builder.title)) {
            tvTitle.setText(builder.title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(builder.message)) {
            tvMsg.setText(builder.message);
        } else {
            tvMsg.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(builder.leftText)) {
            btnLeft.setText(builder.leftText);
        }
        if (!TextUtils.isEmpty(builder.rightText)) {
            btnRight.setText(builder.rightText);
        }
        mOnClickListener = builder.onClickListener;
    }

    /**
     * Builder
     *
     * @author a_liYa
     * @date 2017/11/6 下午2:29.
     */
    public static class Builder {
        String title;
        String message;
        String leftText;
        String rightText;

        View.OnClickListener onClickListener;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setLeftText(String leftText) {
            this.leftText = leftText;
            return this;
        }

        public Builder setRightText(String rightText) {
            this.rightText = rightText;
            return this;
        }

        public Builder setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }
    }


    public int getScreenW() {
        WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        return Math.min(point.x, point.y);
    }

    public int getScreenH() {
        WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        return Math.max(point.x, point.y);
    }
}
