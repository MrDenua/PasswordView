package red.djh.passwordview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * time   : 2019/08/16 14:40
 * desc   :
 * </pre>
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class PasswordView extends View
        implements View.OnKeyListener, View.OnFocusChangeListener {

    private static final int INPUT_TYPE_WORLDS = 0x01;
    private static final int INPUT_TYPE_NUMBER = 0x10;
    private static final int INPUT_TYPE_DEFAULT = 0x10;

    private static final int DEFAULT_HEIGHT = 45;
    private static final int DEFAULT_WIDTH = 270;

    private static final String DEFAULT_VALUE = "";

    private String[] mPassword;
    private int mCurrentIndex;
    private int mLength;
    private int mInputType;

    private RectF mBorderRectF;
    private float mBorderWidth;
    private float mBorderRadius;
    private int mBorderColor;
    private int mMaskColor;
    private int mBackgroundColor;
    private int mTextSize;
    private float mMaskSize;
    private boolean mShowPassword;

    private Paint mBorderPaint;
    private Paint mMaskPaint;

    private OnInputFinishListener mListener;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusableInTouchMode(true);
        setClickable(true);
        setFocusable(true);

        mLength = 5;
        mCurrentIndex = 0;
        mInputType = INPUT_TYPE_NUMBER;
        mMaskPaint = new Paint();
        mBorderPaint = new Paint();
        mBorderRectF = new RectF(0, 0, 0, 0);
        mBorderRadius = dp2px(4);
        mBorderColor = getResources().getColor(R.color.gray);
        mMaskColor = getResources().getColor(R.color.black);
        mTextSize = getResources().getDimensionPixelSize(R.dimen.sp14);
        mMaskSize = getResources().getDimensionPixelSize(R.dimen.dp5);
        mBorderWidth = getResources().getDimensionPixelSize(R.dimen.dp1);
        mBackgroundColor = getResources().getColor(R.color.backgroundColor);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordView);
        mLength = typedArray.getInt(R.styleable.PasswordView_length, mLength);
        mBorderColor = typedArray.getColor(R.styleable.PasswordView_borderColor, mBorderColor);
        mMaskColor = typedArray.getColor(R.styleable.PasswordView_maskColor, mMaskColor);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.PasswordView_textSize, mTextSize);
        mMaskSize = typedArray.getDimensionPixelSize(
                R.styleable.PasswordView_maskSize, (int) mMaskSize);
        mBorderRadius = typedArray.getDimensionPixelSize(
                R.styleable.PasswordView_borderRadius, (int) mBorderRadius);
        mShowPassword = typedArray.getBoolean(
                R.styleable.PasswordView_showPassword, false);
        mBorderWidth = typedArray.getDimensionPixelSize(
                R.styleable.PasswordView_borderWidth, (int) mBorderWidth);
        mBackgroundColor = typedArray.getColor(
                R.styleable.PasswordView_backgroundColor, mBackgroundColor);
        mInputType = typedArray.getInteger(R.styleable.PasswordView_inputType, mInputType);
        typedArray.recycle();

        initPassword(mLength);

        mMaskSize = mMaskSize / 2f;
        mMaskPaint.setDither(false);
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setTextSize(dp2px(mTextSize));
        mMaskPaint.setColor(mMaskColor);

        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setDither(false);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mBorderRectF.top = mBorderWidth / 2;
        mBorderRectF.left = mBorderWidth / 2;
        setBackgroundColor(mBackgroundColor);
        setOnKeyListener(this);
        setOnClickListener(v -> showKeyBoard());
        setOnFocusChangeListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (modeHeight == MeasureSpec.AT_MOST) {
            height = getPaddingTop() + getPaddingBottom();
            height += dp2px(DEFAULT_HEIGHT);
        }
        if (modeWidth == MeasureSpec.AT_MOST) {
            width = getPaddingLeft() + getPaddingRight(); // padding
            width += dp2px(DEFAULT_WIDTH);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBorder(canvas);
        int colWidth = getWidth() / mLength;
        int left;
        int top = getHeight() >> 1;
        for (int i = 0; i < mLength; i++) {
            left = i * colWidth;
            if (i > 0) {
                drawDivide(canvas, left, colWidth);
            }
            drawMask(canvas, left + colWidth / 2, top, i, mPassword[i]);
        }
    }

    protected void drawBorder(Canvas canvas) {
        mBorderRectF.bottom = getHeight() - mBorderRectF.top;
        mBorderRectF.right = getWidth() - mBorderRectF.left;
        canvas.drawRoundRect(mBorderRectF, mBorderRadius, mBorderRadius, mBorderPaint);
    }

    protected void drawDivide(Canvas canvas, int left, int colWidth) {
        canvas.drawLine(left, 0, left, getHeight(), mBorderPaint);
    }

    protected void drawMask(Canvas canvas, int x, int y, int index, String mask) {
        if (mask.equals(DEFAULT_VALUE)) {
            return;
        }
        if (mShowPassword) {
            Rect rect = measureText(mask);
            float x1 = x - (rect.width() / 2f);
            float y1 = y + rect.height() / 2f;
            canvas.drawText(mask, x1, y1, mMaskPaint);
        } else {
            canvas.drawCircle(x, y, mMaskSize, mMaskPaint);
        }
    }

    public void setLength(int length) {
        this.mLength = length;
    }

    public void clear() {
        initPassword(mLength);
    }

    public String getPassword() {
        StringBuilder builder = new StringBuilder();
        for (String p : mPassword) {
            builder.append(p);
        }
        return builder.toString();
    }

    public void setOnInputFinishListener(OnInputFinishListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (isKeyEnter(keyCode)) {
            hideKeyBoard();
        }
        if (!isKeyUp(event.getAction())) {
            return false;
        }
        if (isKeyDelete(keyCode) && mCurrentIndex > 0) {
            mCurrentIndex--;
            mPassword[mCurrentIndex] = DEFAULT_VALUE;
        }
        if (mCurrentIndex < mLength) {
            if (isKeyWorld(keyCode) && (mInputType & INPUT_TYPE_WORLDS) != 0) {
                String world = KeyEvent.keyCodeToString(keyCode);
                mPassword[mCurrentIndex] = world.substring(world.length() - 1);
                mCurrentIndex++;
            }
            if (isKeyNumber(keyCode) && (mInputType & INPUT_TYPE_NUMBER) != 0) {
                int number = keyCode - KeyEvent.KEYCODE_0;
                mPassword[mCurrentIndex] = String.valueOf(number);
                mCurrentIndex++;
            }
            if (mListener != null && mCurrentIndex == mLength) {
                mListener.onInputFinish(mPassword);
            }
        }
        requestLayout();
        invalidate();

        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            showKeyBoard();
        } else {
            hideKeyBoard();
        }
        requestLayout();
        invalidate();
    }

    private Rect measureText(String text) {
        Rect rect = new Rect();
        mMaskPaint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void initPassword(int length) {
        mPassword = new String[length];
        for (int i = 0; i < length; i++) {
            mPassword[i] = DEFAULT_VALUE;
        }
    }

    private int dp2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private boolean isKeyUp(int action) {
        return action == KeyEvent.ACTION_UP;
    }

    private boolean isKeyDelete(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DEL;
    }

    private boolean isKeyEnter(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
    }

    private boolean isKeyWorld(int keyCode) {
        return keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z;
    }

    private boolean isKeyNumber(int keyCode) {
        return keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9;
    }

    public interface OnInputFinishListener {
        void onInputFinish(String[] password);
    }
}
