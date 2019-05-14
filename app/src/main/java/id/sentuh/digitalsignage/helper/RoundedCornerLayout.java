package id.sentuh.digitalsignage.helper;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RoundedCornerLayout extends LinearLayout {
    private RectF rect;
    private Paint paint;

    public RoundedCornerLayout(Context context) {
        super(context);
        init();
    }
    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#7EB5D6"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rect, 20, 20, paint);
    }
}