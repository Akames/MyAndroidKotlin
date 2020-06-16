package com.akame.commonlib.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;


public class CodeEditTextView extends EditText {
    private int maxLength = 6; //最大输入长度
    private Paint linePaint; //下划线的画笔
    private int lineHeight = 4; //下划线的高度
    private int height; //控件高度
    private int lineWith = (int) ScreenUtil.dip2px(50); //下划线的宽度
    private int lineSpace = (int) ScreenUtil.dip2px(16); //每个下划线的间隔
    private Paint textPaint; // 文本画笔
    private CharSequence contentText = ""; //输入的文本数据
    private int textColor = Color.BLACK;
    private int textSize = (int) ScreenUtil.dip2px(20);
    private int selectColor = Color.BLACK;
    private int unSelectColor = Color.parseColor("#A8A8A8");


    private TextChangedListener changedListener;

    public CodeEditTextView(Context context) {
        super(context);
        init();
    }

    public CodeEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //输入类型为电话号码
        this.setInputType(InputType.TYPE_CLASS_PHONE);
        this.setTextIsSelectable(false);
        this.setLongClickable(false);

        this.setFocusableInTouchMode(true);
        this.setFocusable(true);
        this.requestFocus();
        this.setCursorVisible(false);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)}); //设置输入最大长度

        this.setBackgroundColor(0); //清空背景

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(lineHeight);


        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        setTextSize(0);

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contentText = s;
                invalidate();
                if (changedListener != null) {
                    if (contentText.length() < maxLength) {
                        changedListener.textChanged(contentText);
                    } else {
                        changedListener.textCompleted(contentText);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        lineWith = (w - lineSpace * (maxLength - 1)) / maxLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawText(canvas, contentText);
    }


    private void drawLine(Canvas canvas) {
        for (int i = 0; i < maxLength; i++) {
            linePaint.setColor(i < contentText.length() ? selectColor : unSelectColor);
            canvas.drawLine(i * (lineWith + lineSpace), height - lineHeight,
                    i * (lineWith + lineSpace) + lineWith, height - lineHeight, linePaint);
        }
    }

    private void drawText(Canvas canvas, CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            int startX = ((lineSpace + lineWith) * i) + lineWith / 2;
            int startY = height - lineHeight;
            String value = String.valueOf(charSequence.charAt(i));
            int baseY = (int) ((startY - (textPaint.descent() + textPaint.ascent())) / 2);
            canvas.drawText(value, startX, baseY, textPaint);
        }
    }


    /**
     * 设置输入文本颜色
     */
    public void setInputColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
    }

    /**
     * 设置输入文本大小
     */
    public void setInputSize(int textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
    }

    /**
     * 设置下划线颜色
     *
     * @param selectColor   已经输入的颜色
     * @param unSelectColor 未输入的颜色
     */
    public void setLineColor(int selectColor, int unSelectColor) {
        this.selectColor = selectColor;
        this.unSelectColor = unSelectColor;
    }

    /**
     * 密码监听者
     */
    public interface TextChangedListener {
        /**
         * 输入/删除监听
         *
         * @param changeText 输入/删除的字符
         */
        void textChanged(CharSequence changeText);

        /**
         * 输入完成
         */
        void textCompleted(CharSequence text);
    }


    public void setChangedListener(TextChangedListener changedListener) {
        this.changedListener = changedListener;
    }

}
