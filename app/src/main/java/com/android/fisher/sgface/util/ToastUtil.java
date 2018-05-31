package com.android.fisher.sgface.util;
import com.mwj.lhn.sgdk.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;




/**
 * @description :  所有弹出信息提示效果
 */
public class ToastUtil {

    public static Toast toast;
    private static TextView tvMessage;

    public static void showToast(Context context, Object title, int duration) {
        if (context == null)
            return;
        if (title != null) {
            if (toast == null) {
                tvMessage = (TextView) LayoutInflater.from(context).inflate(R.layout.layout_toast1, null);
                toast = new Toast(context);
                toast.setDuration(duration);
                toast.setView(tvMessage);
            }
            if (title instanceof String) {
                if (StringUtils.isEmpty((String) title))
                    return;
                tvMessage.setText((String) title);
            } else {
                tvMessage.setText((Integer) title);
            }
            toast.show();
        }
    }

    public static void showToast(Context context, String title) {
        showToast(context, title, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int resid) {
        showToast(context, resid, Toast.LENGTH_SHORT);
    }
}
 
