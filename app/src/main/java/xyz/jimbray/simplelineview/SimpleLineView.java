package xyz.jimbray.simplelineview;

import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by Jimbray  .
 * on 2016/8/25
 * Email: jimbray16@gmail.com
 * Description: TODO
 */
public class SimpleLineView extends View {

    private Context mContext;

    private float mViewHeight, mViewWidth;
    private Paint mNormalPointPaint,mSelectedPointPaint, mLinePaint, mBgLinePaint, mTestPaint,
            mBottomTextPaint, mTopTextPaint, mAverageLinePaint, mLifeLongLinePaint, mBottomValuePaint, mBgPaint;//各种画笔

    private float mVerticalOffset               = dp2px(5);     //上下边距
    private float mPointWidth                   = dp2px(4f);    //圆点大小（现已修改为图片）
    private float mHorizontalOffset             = dp2px(15f);   //左右边距
    private float mValuePaddingOffset;
    private boolean mIsHorizontalValue          = false;        //所有值都相等（是一条水平线）将所有点都画在中间位置

    private List<SimpleLineData> mData;         // 数据
    private List<String> mBottomTexts;          // 底部文字集合
    private float mBottomTextSize;              // 底部文字大小
    private int mBottomTextStepSize;            // 底部文字 相隔展示间距

    private String mTopText;                    // 顶部中间文字内容
    private float mTopTextSize;                 // 顶部中间文字大小

    // 点到点之间的动画相关变量
    private int mDrawingLineIndex;
    private float mDrawingStopX = -1f, mDrawingStopY = -1f;
    private AnimatorSet mAnimatorLine;
    private boolean isAnimatingLine;

    // 平均线的动画相关变量
    private boolean isAnimatingAverageLine;
    private AnimatorSet mAnimatorAverageLine;
    private float mDrawingStopAverageLineX = -1f;

    private String mAverageIconText; //平均线图示文字
    private float mAverageIconTextSize; //平均线图示文字大小
    private float mAverageValue = -1;

    // lifelong 的动画相关变量
    private boolean isAnimatingLifelongLine;
    private AnimatorSet mAnimatorLifelongLine;
    private float mDrawingStopLifelongLineX = -1;

    private String mLifeLongIconText; //linflong 图示文字
    private float mLifelongIconTextSize;
    private float mLifeLongValue = -1;

    // 点击点到底部的动画相关变量
    private boolean isAnimatingSelectedLine;
    private AnimatorSet mAnimatorSelectedLine;
    private float mDrawingStopSelectedLineY = -1f;

    // 数据值 原点的 图片
    private Bitmap mBitmapNormalCircle, mBitmapSelectedCircle;

    // 点击位置相关变量
    private float mTouchDownX, mTouchDownY;
    private float mTouchPadding = dp2px(2.5f);

    // 是否绘制的控制
    private boolean mIsDrawBottomText           = false;  //是否绘制底部文字
    private boolean mIsDrawAverageLine          = true;  //是否绘制平均线
    private boolean mIsDrawLiflongLine          = false;  //是否绘制 lifelong
    private boolean mIsDrawVerticalLine         = true;  //是否绘制背景竖线
    private boolean mIsDrawHorizontalLine       = false; //是否绘制背景横线
    private boolean mIsDrawTopSideLine          = true;  //是否绘制顶部边线
    private boolean mIsDrawBottomSideLine       = true;  //是否绘制底部边线
    private boolean mIsDrawRightSideLine        = true;  //是否绘制右部边线
    private boolean mIsDrawLeftSideLine         = true;  //是否绘制左部边线
    private boolean mIsDrawPointSelectedLine    = true;  //是否绘制点击时的竖线
    private boolean mIsDrawValueTextBottom      = true;  //是否在底部空间显示点击值的内容


    private float mBottomValueTextSize; //底部空间文字大小
    private String mBottomValueSuffix = "";
    private String mBottomValuePrefix = "";

    //各模块颜色配置
    private final int DEFAULT_NORMAL_POINT_COLOR = Color.rgb(0,181,255);
    private final int DEFAULT_SELECTED_POINT_COLOR = Color.rgb(255,128,97);
    private final int DEFAULT_POINT_TO_LINE_COLOR = Color.rgb(0,181,255);
    private final int DEFAULT_BACKGROUNG_LINE_COLOR = Color.rgb(194,200,208);
    private final int DEFAULT_BOTTOM_TEXT_COLOR = Color.rgb(194,200,208);
    private final int DEFAULT_TOP_TEXT_COLOR = Color.rgb(0,181,255);
    private final int DEFAULT_AVERAGE_LINE_COLOR = Color.rgb(254,117,117);
    private final int DEFAULT_LIFELONG_LINE_COLOR = Color.rgb(175,117,254);
    private final int DEFAULT_VIEW_BACKGROUND_COLOR = Color.rgb(250,251,254);
    private final int DEFAULT_BOTTOM_VALUE_TEXT_COLOR = Color.rgb(0,181,255);

    private int mNormalPointColor = DEFAULT_NORMAL_POINT_COLOR;
    private int mSelectedPointColor = DEFAULT_SELECTED_POINT_COLOR;
    private int mPointToLineColor = DEFAULT_POINT_TO_LINE_COLOR;
    private int mBackgroungLineColor = DEFAULT_BACKGROUNG_LINE_COLOR;
    private int mBottomTextColor = DEFAULT_BOTTOM_TEXT_COLOR;
    private int mTopTextColor = DEFAULT_TOP_TEXT_COLOR;
    private int mAverageLineColor = DEFAULT_AVERAGE_LINE_COLOR;
    private int mLifelongLineColor = DEFAULT_LIFELONG_LINE_COLOR;
    private int mViewBackgroundColor = DEFAULT_VIEW_BACKGROUND_COLOR;
    private int mBottomValueTextColor = DEFAULT_BOTTOM_VALUE_TEXT_COLOR;

    private static final int DEFAULT_COLUMN_COUNT = 7;
    private int mColumnCount;

    public SimpleLineView(Context context) {
        super(context, null);
    }

    public SimpleLineView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public SimpleLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        mColumnCount = DEFAULT_COLUMN_COUNT;

        mAnimatorLine = new AnimatorSet();
        mViewHeight = dp2px(196);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mViewBackgroundColor);
        mBgPaint.setStyle(Paint.Style.FILL);

        mNormalPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNormalPointPaint.setColor(mNormalPointColor);
        mNormalPointPaint.setStyle(Paint.Style.STROKE);
        mNormalPointPaint.setStrokeWidth(dp2px(1));

        mSelectedPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedPointPaint.setColor(mSelectedPointColor);
        mSelectedPointPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mPointToLineColor);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(dp2px(2));

        mBgLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgLinePaint.setColor(mBackgroungLineColor);
        mBgLinePaint.setStyle(Paint.Style.FILL);

        mTestPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTestPaint.setColor(Color.RED);
        mTestPaint.setStyle(Paint.Style.FILL);

        mBottomTextStepSize = 1;
        mBottomTextSize = sp2px(12);
        mBottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomTextPaint.setColor(mBottomTextColor);
        mBottomTextPaint.setStyle(Paint.Style.FILL);
        mBottomTextPaint.setTextAlign(Paint.Align.CENTER);
        mBottomTextPaint.setTextSize(mBottomTextSize);

        mTopText = "Data";
        mTopTextSize = sp2px(12);
        mTopTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTopTextPaint.setColor(mTopTextColor);
        mTopTextPaint.setStyle(Paint.Style.FILL);
        mTopTextPaint.setTextAlign(Paint.Align.CENTER);
        mTopTextPaint.setTextSize(mTopTextSize);

        mAverageIconText = "Average";
        mAverageIconTextSize = sp2px(12);
        mAverageLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAverageLinePaint.setColor(mAverageLineColor);
        mAverageLinePaint.setStyle(Paint.Style.FILL);
        mAverageLinePaint.setTextSize(mAverageIconTextSize);
        mAverageLinePaint.setTextAlign(Paint.Align.CENTER);

        mLifeLongIconText = "Lifelong";
        mLifelongIconTextSize = sp2px(12);
        mLifeLongLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLifeLongLinePaint.setColor(mLifelongLineColor);
        mLifeLongLinePaint.setStyle(Paint.Style.FILL);
        mLifeLongLinePaint.setTextSize(mLifelongIconTextSize);
        mLifeLongLinePaint.setTextAlign(Paint.Align.CENTER);

        mBottomValueTextSize = dp2px(12);
        mBottomValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomValuePaint.setColor(mBottomValueTextColor);
        mBottomValuePaint.setStyle(Paint.Style.FILL);
        mBottomValuePaint.setTextSize(mBottomValueTextSize);
        mBottomValuePaint.setTextAlign(Paint.Align.CENTER);

        setupData();
        mBitmapNormalCircle = BitmapFactory.decodeResource(getResources(), R.mipmap.normal_circle) ;
        mBitmapSelectedCircle = BitmapFactory.decodeResource(getResources(), R.mipmap.selected_circle);
    }

    private void setupData() {
//        SimpleLineData i1 = new SimpleLineData();
//        i1.setIndex(0);
//        i1.setValue(30);
//        mData.add(i1);
//        SimpleLineData i2 = new SimpleLineData();
//        i2.setIndex(1);
//        i2.setValue(40);
//        mData.add(i2);
//        SimpleLineData i3 = new SimpleLineData();
//        i3.setIndex(2);
//        i3.setValue(30);
//        mData.add(i3);
//        SimpleLineData i4 = new SimpleLineData();
//        i4.setIndex(3);
//        i4.setValue(50);
//        mData.add(i4);
//        SimpleLineData i5 = new SimpleLineData();
//        i5.setIndex(4);
//        i5.setValue(30);
//        mData.add(i5);
//        SimpleLineData i6 = new SimpleLineData();
//        i6.setIndex(5);
//        i6.setValue(40);
//        mData.add(i6);
//        SimpleLineData i7 = new SimpleLineData();
//        i7.setIndex(6);
//        i7.setValue(60);
//        mData.add(i7);

//        mBottomTexts = new ArrayList<>();
//        mBottomTexts.add("Sun");
//        mBottomTexts.add("Mon");
//        mBottomTexts.add("Tue");
//        mBottomTexts.add("Wed");
//        mBottomTexts.add("Thu");
//        mBottomTexts.add("Fri");
//        mBottomTexts.add("Sat");
//
//
//        mData = new ArrayList<>();
//        for(int i = 0 ; i < 7; i++) {
//            SimpleLineData item = new SimpleLineData();
//            item.setIndex(i);
//            item.setValue((int)(Math.random()* 99 + 1));
//
//            mData.add(item);
//        }

    }

    /**
     * 最小宽度 由你定
     * 不定我来给个最小值吧
     * @return
     */
//    @Override
//    protected int getSuggestedMinimumWidth() {
//        return (int) mViewWidth;
//    }

    /**
     * 跟宽度一个意思
     * @return
     */
    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) mViewHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    /**
     * 测量 view 的 宽高信息
     * @param measureSpec
     * @param isWidth
     * @return
     */
    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(mViewBackgroundColor);

        mValuePaddingOffset = (getHeight() - (mIsDrawBottomText ? mBottomTextSize : 0))*0.2f; //把上下两部分（20%）区域 不做绘图区域

        drawBackgroundLine(canvas);

        if(mData != null) {
            drawPoint2Line(canvas);

            drawPoint(canvas);
        }

        drawTopText(canvas);

        if(mData != null) {
            drawValueIcon(canvas);
        }
    }

    private void drawValueIcon(Canvas canvas) {
        if(mLifeLongValue < 0) {
            mIsDrawLiflongLine = false;
        }

        float lifelong_text_width = mLifeLongLinePaint.measureText(mLifeLongIconText);
        if(mIsDrawLiflongLine) {

            canvas.drawText(mLifeLongIconText, getWidth() - mHorizontalOffset - lifelong_text_width,
                    mVerticalOffset + mValuePaddingOffset/2 + mLifelongIconTextSize/2 + (mLifeLongLinePaint.descent() + mLifeLongLinePaint.ascent() / 2.0f), mLifeLongLinePaint);//文字居中
            canvas.drawCircle(getWidth() - mHorizontalOffset - lifelong_text_width - mLifelongIconTextSize*2 - dp2px(2), mVerticalOffset + mValuePaddingOffset/2, mLifelongIconTextSize/3, mLifeLongLinePaint);
        }

        float average_text_width = mAverageLinePaint.measureText(mAverageIconText);


//        canvas.drawText(mAverageIconText,
//                getWidth() - mHorizontalOffset - average_text_width,
//                mVerticalOffset + mAverageIconTextSize + mAverageIconTextSize/3 + (mAverageLinePaint.descent() + mAverageLinePaint.ascent() / 2.0f),
//                mAverageLinePaint);//文字居中
        canvas.drawText(mAverageIconText, getWidth() - mHorizontalOffset - average_text_width - (mIsDrawLiflongLine ? (lifelong_text_width + mLifelongIconTextSize + dp2px(2) + dp2px(2)) : 0),
                mVerticalOffset + mValuePaddingOffset/2 + mAverageIconTextSize/2 + (mAverageLinePaint.descent() + mAverageLinePaint.ascent() / 2.0f), mAverageLinePaint);


//        canvas.drawCircle(getWidth() - mHorizontalOffset - average_text_width - mAverageIconTextSize*2 - dp2px(2),
//                mVerticalOffset + mAverageIconTextSize/2 + mAverageIconTextSize/3, mAverageIconTextSize/3, mAverageLinePaint);

        canvas.drawCircle(getWidth() - mHorizontalOffset - average_text_width - (mIsDrawLiflongLine ? (lifelong_text_width + mLifelongIconTextSize + dp2px(2) + dp2px(2)) : 0) - mAverageIconTextSize*2 - dp2px(2),
                mVerticalOffset + mValuePaddingOffset/2, mAverageIconTextSize/3, mAverageLinePaint);

    }

    private void drawBackgroundLine(Canvas canvas) {
        //画背景线


        for (int i = 0 ; i < mColumnCount; i++) {
            float verticalStartX = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset;
            if(mIsDrawVerticalLine) {
                mIsDrawRightSideLine = true; //如果需要画竖线，默认需要画最右边的竖线
                mIsDrawLeftSideLine  = true; //如果需要画竖线，默认需要画最左边的竖线
                float verticalStartY = mVerticalOffset;
                float verticalStopX  = verticalStartX;
                float verticalStopY  = getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0);
                canvas.drawLine(verticalStartX, verticalStartY, verticalStopX, verticalStopY, mBgLinePaint);
            }

            if(mIsDrawHorizontalLine) {
                mIsDrawTopSideLine = true;
                mIsDrawBottomSideLine = true;
                float horizontalStartX = mHorizontalOffset;
                float horizontalStartY = ((getHeight() - mVerticalOffset*2 - (mIsDrawBottomText ? mBottomTextSize : 0))/(mColumnCount -1))*i + mVerticalOffset;
                float horizontalStopX  = getWidth() - mHorizontalOffset;
                float horiontalStopY   = horizontalStartY;
                canvas.drawLine(horizontalStartX, horizontalStartY, horizontalStopX, horiontalStopY, mBgLinePaint);
            }

            if(mIsDrawBottomText) {
                if (mBottomTexts != null) {
                    //draw bottom text
                    String bottom_text_str = mBottomTexts.get(i);
//                    float bottom_text_width = mBottomTextPaint.measureText(bottom_text_str);
                    if(i % mBottomTextStepSize == 0) {
                        canvas.drawText(bottom_text_str, verticalStartX, getHeight() - mVerticalOffset, mBottomTextPaint);
                    } else if(i == mColumnCount - 1) {
                        if((i - 1) % mBottomTextStepSize != 0) {
                            canvas.drawText(bottom_text_str, verticalStartX, getHeight() - mVerticalOffset, mBottomTextPaint);
                        }
                    }
                }
            }
        }

        if(mIsDrawRightSideLine) {
            //最后一条竖线
            canvas.drawLine(getWidth() - mHorizontalOffset, mVerticalOffset, getWidth() - mHorizontalOffset, getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0), mBgLinePaint);
        }

        if(mIsDrawLeftSideLine) {
            canvas.drawLine(mHorizontalOffset, mVerticalOffset, mHorizontalOffset, getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0), mBgLinePaint);
        }

        if(mIsDrawBottomSideLine) {
            canvas.drawLine(mHorizontalOffset, getHeight()- mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0), getWidth()- mHorizontalOffset, getHeight()- mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0), mBgLinePaint);//底部横线
        }
        if(mIsDrawTopSideLine) {
            canvas.drawLine(mHorizontalOffset, mVerticalOffset, getWidth() - mHorizontalOffset, mVerticalOffset, mBgLinePaint);//顶部横线
        }

    }

    private void drawPoint2Line(Canvas canvas) {
        float max_value = getMaxValue();
        float min_value = getMinValue();
        float max_pos_y = getHeight() - mVerticalOffset - mValuePaddingOffset - (mIsDrawBottomText ? mBottomTextSize : 0);
        float min_pos_y = mValuePaddingOffset;

        if(max_value == min_value) {
            mIsHorizontalValue = true;
        } else {
            mIsHorizontalValue = false;
        }

        //画连接线 不带动画的全部连接线
//        for (int i = 0 ; i < mData.size(); i++) {
//            if(i < mData.size() - 1) {
//                if(mIsHorizontalValue) {
//                    canvas.drawLine(((getWidth()- mHorizontalOffset *2)/(mData.size() - 1))*i + mHorizontalOffset,
//                            min_pos_y + (max_pos_y - min_pos_y)/2,
//                            ((getWidth()- mHorizontalOffset *2)/(mData.size() - 1))*(i+1) + mHorizontalOffset,
//                            min_pos_y + (max_pos_y - min_pos_y)/2, mLinePaint);
//                } else {
//                    canvas.drawLine(((getWidth()- mHorizontalOffset *2)/(mData.size() - 1))*i + mHorizontalOffset,
//                            mValuePaddingOffset + ((max_value-mData.get(i).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y),
//                            ((getWidth()- mHorizontalOffset *2)/(mData.size() - 1))*(i+1) + mHorizontalOffset,
//                            mValuePaddingOffset + ((max_value-mData.get(i+1).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y), mLinePaint);
//                }
//
//            }
//        }

        boolean hadDrawed = false;
        for (int k = 0 ; k < mData.size() - 1; k++) {
            if(mData.get(k).getValue() < 0) {
                continue;
            }
            if(k < mDrawingLineIndex) {
                hadDrawed = true;
                float line_start_x;
                if(mColumnCount > 1) {
                    line_start_x = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*k + mHorizontalOffset;
                } else {
                    line_start_x = mHorizontalOffset;
                }
                if(k == mDrawingLineIndex - 1) {

                    if(isAnimatingLine) {

                        if(mIsHorizontalValue) {
                            canvas.drawLine(line_start_x,
                                    min_pos_y + (max_pos_y - min_pos_y)/2,
                                    mDrawingStopX, mDrawingStopY, mLinePaint);
                        } else {
                            canvas.drawLine(line_start_x,
                                    mValuePaddingOffset + ((max_value-mData.get(k).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y),
                                    mDrawingStopX, mDrawingStopY, mLinePaint);
                        }
                    } else {
                        float line_stop_x;
                        if(mColumnCount > 1) {
                            line_stop_x = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*(mDrawingLineIndex) + mHorizontalOffset;
                        } else {
                            line_stop_x = mHorizontalOffset;
                        }
                        if(mIsHorizontalValue) {
                            startLineToAnimation(line_start_x,
                                    min_pos_y + (max_pos_y - min_pos_y)/2,
                                    line_stop_x,
                                    min_pos_y + (max_pos_y - min_pos_y)/2);
                        } else {
                            startLineToAnimation(line_start_x,
                                    mValuePaddingOffset + ((max_value-mData.get(k).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y),
                                    line_stop_x,
                                    mValuePaddingOffset + ((max_value-mData.get(mDrawingLineIndex).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y));
                        }
                    }
                } else {
                    float line_stop_x;
                    if(mColumnCount > 1) {
                        line_stop_x = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*(k+1) + mHorizontalOffset;
                    } else {
                        line_stop_x = mHorizontalOffset;
                    }
                    if(mIsHorizontalValue) {
                        canvas.drawLine(line_start_x,
                                min_pos_y + (max_pos_y - min_pos_y)/2,
                                line_stop_x,
                                min_pos_y + (max_pos_y - min_pos_y)/2, mLinePaint);
                    } else {
                        canvas.drawLine(line_start_x,
                                mValuePaddingOffset + ((max_value-mData.get(k).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y),
                                line_stop_x,
                                mValuePaddingOffset + ((max_value-mData.get(k+1).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y), mLinePaint);
                    }
                }
            }
        }

        if(!hadDrawed) {
            mDrawingLineIndex++;
            invalidate();
        }
    }

    private void drawPoint(Canvas canvas) {

        float max_value = getMaxValue();
        float min_value = getMinValue();
        float max_pos_y = getHeight() - mVerticalOffset - mValuePaddingOffset - (mIsDrawBottomText ? mBottomTextSize : 0);
        float min_pos_y = mValuePaddingOffset;
        float average_value = 0;
        for (int i = 0 ; i < mData.size(); i++) {
            if(mData.get(i).getValue() < 0) {
                continue;
            }
            average_value += mData.get(i).getValue();
            if(mIsHorizontalValue) {
                //之前的圆点是用画的，现在修改为图片了
//                canvas.drawCircle(((getWidth()- mHorizontalOffset *2)/(mData.size() - 1))*i + mHorizontalOffset,
//                        min_pos_y + (max_pos_y - min_pos_y)/2, mPointWidth, mNormalPointPaint);


                float left, top;
                if(mColumnCount > 1) {
                    left = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset - mBitmapNormalCircle.getWidth()/2;
                } else {
                    left = mHorizontalOffset - mBitmapNormalCircle.getWidth()/2;
                }
                top = (min_pos_y + (max_pos_y - min_pos_y)/2) - mBitmapNormalCircle.getHeight()/2;
                boolean isInTouchArea= false;
                if(mTouchDownX > left - mTouchPadding && mTouchDownX < left + mBitmapNormalCircle.getWidth() + mTouchPadding) {
                    //X 符合要求
                    if(mTouchDownY > top - mTouchPadding && mTouchDownY < top + mBitmapNormalCircle.getHeight() + mTouchPadding) {
                        // Y 符合要求
                        isInTouchArea = true;
                    }
                }
                if(isInTouchArea) {
                    if(mIsDrawPointSelectedLine) {
                        if(mDrawingStopSelectedLineY != getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0)) {
                            if(isAnimatingSelectedLine) {
                                float line_x;
                                if(mColumnCount > 1) {
                                    line_x = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset;
                                } else {
                                    line_x = mHorizontalOffset;
                                }
                                canvas.drawLine(line_x,
                                        min_pos_y + (max_pos_y - min_pos_y)/2, line_x,
                                        mDrawingStopSelectedLineY, mLinePaint);
                            } else {
                                if(mDrawingStopSelectedLineY == -1) {
                                    startSelectedLineAnimation(min_pos_y + (max_pos_y - min_pos_y)/2,
                                            getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0));
                                }
                            }
                        } else {
                            float line_x;
                            if(mColumnCount > 1) {
                                line_x = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset;
                            } else {
                                line_x = mHorizontalOffset;
                            }
                            canvas.drawLine(line_x,
                                    min_pos_y + (max_pos_y - min_pos_y)/2, line_x,
                                    getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0), mLinePaint);
                            if(mIsDrawBottomText) {
                                if (mBottomTexts != null) {
                                    //draw bottom text
                                    String bottom_text_str = mBottomTexts.get(i);
                                    mBottomTextPaint.setColor(mPointToLineColor);
                                    canvas.drawText(bottom_text_str, line_x, getHeight() - mVerticalOffset, mBottomTextPaint);
                                    mBottomTextPaint.setColor(mBottomTextColor);
                                }
                            }
                        }
                    }

                    if(mColumnCount > 1) {
                        left = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset - mBitmapSelectedCircle.getWidth()/2;
                    } else {
                        left = mHorizontalOffset - mBitmapSelectedCircle.getWidth()/2;
                    }
                    top = (min_pos_y + (max_pos_y - min_pos_y)/2) - mBitmapSelectedCircle.getHeight()/2;
                    canvas.drawBitmap(mBitmapSelectedCircle, left, top, mNormalPointPaint);

                    if(mIsDrawValueTextBottom) {
                        String bottom_value_text = null;
                        if(TextUtils.isEmpty(mData.get(i).getValue_text())) {
                            bottom_value_text = String.valueOf(mData.get(i).getValue());
                            bottom_value_text = mBottomValuePrefix + bottom_value_text + mBottomValueSuffix;
                        } else {
                            bottom_value_text = mData.get(i).getValue_text();
                        }

                        float bottom_value_text_width = mBottomValuePaint.measureText(bottom_value_text);

                        RectF rectF_bg = new RectF();
                        if(mColumnCount > 1) {
                            rectF_bg.left = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset - bottom_value_text_width/2;
                        } else {
                            rectF_bg.left = mHorizontalOffset - bottom_value_text_width/2;
                        }

                        if(rectF_bg.left < mHorizontalOffset) {
                            rectF_bg.left = mHorizontalOffset + dp2px(2);
                        }

                        if((rectF_bg.left + bottom_value_text_width) > getWidth() - mHorizontalOffset) {
                            rectF_bg.left = rectF_bg.left - bottom_value_text_width/2 - dp2px(2);
                        }

                        rectF_bg.top = getHeight() - mValuePaddingOffset/2 - mBottomValueTextSize/2 - (mIsDrawBottomText ? mBottomTextSize : 0) ;
                        rectF_bg.right = rectF_bg.left + bottom_value_text_width;
                        rectF_bg.bottom = rectF_bg.top + mBottomTextSize;
                        canvas.drawRect(rectF_bg, mBgPaint);

                        canvas.drawText(bottom_value_text, rectF_bg.left + bottom_value_text_width/2,
                                getHeight() - mValuePaddingOffset/2 - (mIsDrawBottomText ? mBottomTextSize : 0) + mBottomValueTextSize/2 + (mBottomValuePaint.descent() + mBottomValuePaint.ascent() / 2.0f), mBottomValuePaint);
                    }
                } else {
                    canvas.drawBitmap(mBitmapNormalCircle, left, top, mNormalPointPaint);
                }



            } else {
//                canvas.drawCircle(((getWidth()- mHorizontalOffset *2)/(mData.size() - 1))*i + mHorizontalOffset,
//                        mValuePaddingOffset + ((max_value-mData.get(i).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y), mPointWidth, mNormalPointPaint);

                float left, top;
                if(mColumnCount > 1) {
                    left = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset - mBitmapNormalCircle.getWidth()/2;
                } else {
                    left = mHorizontalOffset - mBitmapNormalCircle.getWidth()/2;
                }
                top = (mValuePaddingOffset + ((max_value-mData.get(i).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y)) - mBitmapNormalCircle.getHeight()/2;
                boolean isInTouchArea= false;
                if(mTouchDownX > left - mTouchPadding && mTouchDownX < left + mBitmapNormalCircle.getWidth() + mTouchPadding) {
                    //X 符合要求
                    if(mTouchDownY > top - mTouchPadding && mTouchDownY < top + mBitmapNormalCircle.getHeight() + mTouchPadding) {
                        // Y 符合要求
                        isInTouchArea = true;
                    }
                }
                if(isInTouchArea) {
                    if(mIsDrawPointSelectedLine) {
                        if(mDrawingStopSelectedLineY != getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0)) {
                            if(isAnimatingSelectedLine) {
                                float line_x;
                                if(mColumnCount > 1) {
                                    line_x = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset;
                                } else {
                                    line_x = mHorizontalOffset;
                                }
                                canvas.drawLine(line_x,
                                        mValuePaddingOffset + ((max_value-mData.get(i).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y),
                                        line_x,
                                        mDrawingStopSelectedLineY, mLinePaint);
                            } else {
                                if(mDrawingStopSelectedLineY == -1) {
                                    startSelectedLineAnimation(mValuePaddingOffset + ((max_value-mData.get(i).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y),
                                            getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0));
                                }
                            }
                        } else {
                            float line_x;
                            if(mColumnCount > 1) {
                                line_x = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset;
                            } else {
                                line_x = mHorizontalOffset;
                            }
                            canvas.drawLine(line_x,
                                    mValuePaddingOffset + ((max_value-mData.get(i).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y),
                                    line_x,
                                    getHeight() - mVerticalOffset - (mIsDrawBottomText ? mBottomTextSize : 0), mLinePaint);
                            if(mIsDrawBottomText) {
                                if (mBottomTexts != null) {
                                    //draw bottom text
                                    String bottom_text_str = mBottomTexts.get(i);
                                    mBottomTextPaint.setColor(mPointToLineColor);
                                    canvas.drawText(bottom_text_str, line_x, getHeight() - mVerticalOffset, mBottomTextPaint);
                                    mBottomTextPaint.setColor(mBottomTextColor);
                                }
                            }
                        }
                    }


                    if(mColumnCount > 1) {
                        left = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset - mBitmapSelectedCircle.getWidth()/2;
                    } else {
                        left = mHorizontalOffset - mBitmapSelectedCircle.getWidth()/2;
                    }
                    top = (mValuePaddingOffset + ((max_value-mData.get(i).getValue())/(max_value-min_value))*(max_pos_y-min_pos_y)) - mBitmapSelectedCircle.getHeight()/2;
                    canvas.drawBitmap(mBitmapSelectedCircle, left, top, mNormalPointPaint);

                    if(mIsDrawValueTextBottom) {
                        String bottom_value_text = null;
                        if(TextUtils.isEmpty(mData.get(i).getValue_text())) {
                            bottom_value_text = String.valueOf(mData.get(i).getValue());
                            bottom_value_text = mBottomValuePrefix + bottom_value_text + mBottomValueSuffix;
                        } else {
                            bottom_value_text = mData.get(i).getValue_text();
                        }

                        float bottom_value_text_width = mBottomValuePaint.measureText(bottom_value_text);

                        RectF rectF_bg = new RectF();
                        if(mColumnCount > 1) {
                            rectF_bg.left = ((getWidth()- mHorizontalOffset *2)/(mColumnCount - 1))*i + mHorizontalOffset - bottom_value_text_width/2;
                        } else {
                            rectF_bg.left = mHorizontalOffset - bottom_value_text_width/2;
                        }
                        if(rectF_bg.left < mHorizontalOffset) {
                            rectF_bg.left = mHorizontalOffset + dp2px(2);
                        }
                        if((rectF_bg.left + bottom_value_text_width) > getWidth() - mHorizontalOffset) {
                            rectF_bg.left = rectF_bg.left - bottom_value_text_width/2 - dp2px(2);
                        }

                        rectF_bg.top = getHeight() - mValuePaddingOffset/2 - mBottomValueTextSize/2 - (mIsDrawBottomText ? mBottomTextSize : 0) ;
                        rectF_bg.right = rectF_bg.left + bottom_value_text_width;
                        rectF_bg.bottom = rectF_bg.top + mBottomTextSize;
                        canvas.drawRect(rectF_bg, mBgPaint);

                        canvas.drawText(bottom_value_text, rectF_bg.left + bottom_value_text_width/2,
                                getHeight() - mValuePaddingOffset/2 - (mIsDrawBottomText ? mBottomTextSize : 0) + mBottomValueTextSize/2 + (mBottomValuePaint.descent() + mBottomValuePaint.ascent() / 2.0f), mBottomValuePaint);
                    }
                } else {
                    canvas.drawBitmap(mBitmapNormalCircle, left, top, mNormalPointPaint);
                }

            }


        }
        if(mAverageValue < 0) {
            average_value = average_value/mData.size();
        } else {
            average_value = mAverageValue;
        }

        if(mLifeLongValue < 0) {
            mIsDrawLiflongLine = false;
        }

        if(mIsDrawLiflongLine) { // lifelong 是另外一种平均值，可以不用（我用在多个simpleLine所有的平均值）
            if(mDrawingStopLifelongLineX != getWidth() - mHorizontalOffset) {
                if(mIsHorizontalValue) {
                    if(isAnimatingLifelongLine) {
                        //draw average line
                        canvas.drawLine(mHorizontalOffset, min_pos_y + (max_pos_y - min_pos_y)/2,
                                mDrawingStopLifelongLineX, min_pos_y + (max_pos_y - min_pos_y)/2,
                                mLifeLongLinePaint);
                    } else {
                        if(mDrawingStopLifelongLineX  == -1) {
                            startLifelongLineAnimation(mHorizontalOffset, getWidth() - mHorizontalOffset);
                        }
                    }
                } else {
                    if(isAnimatingLifelongLine) {
                        canvas.drawLine(mHorizontalOffset,
                                mValuePaddingOffset + ((max_value-mLifeLongValue)/(max_value-min_value))*(max_pos_y-min_pos_y),
                                mDrawingStopLifelongLineX,
                                mValuePaddingOffset + ((max_value-mLifeLongValue)/(max_value-min_value))*(max_pos_y-min_pos_y),
                                mLifeLongLinePaint);
                    } else {
                        if(mDrawingStopLifelongLineX == -1) {
                            startLifelongLineAnimation(mHorizontalOffset, getWidth() - mHorizontalOffset);
                        }
                    }

                }
            } else {
                if(mIsHorizontalValue) {
                    canvas.drawLine(mHorizontalOffset, min_pos_y + (max_pos_y - min_pos_y)/2,
                            getWidth() - mHorizontalOffset, min_pos_y + (max_pos_y - min_pos_y)/2,
                            mLifeLongLinePaint);
                } else {
                    canvas.drawLine(mHorizontalOffset,
                            mValuePaddingOffset + ((max_value-mLifeLongValue)/(max_value-min_value))*(max_pos_y-min_pos_y),
                            getWidth() - mHorizontalOffset,
                            mValuePaddingOffset + ((max_value-mLifeLongValue)/(max_value-min_value))*(max_pos_y-min_pos_y),
                            mLifeLongLinePaint);
                }
            }
        }

        if(mIsDrawAverageLine) {
            if(mDrawingStopAverageLineX != getWidth() - mHorizontalOffset) {
                if(mIsHorizontalValue) {
                    if(isAnimatingAverageLine) {
                        //draw average line
                        canvas.drawLine(mHorizontalOffset, min_pos_y + (max_pos_y - min_pos_y)/2,
                                mDrawingStopAverageLineX, min_pos_y + (max_pos_y - min_pos_y)/2,
                                mAverageLinePaint);
                    } else {
                        if(mDrawingStopAverageLineX  == -1) {
                            startAverageLineAnimation(mHorizontalOffset, getWidth() - mHorizontalOffset);
                        }
                    }
                } else {
                    if(isAnimatingAverageLine) {
                        canvas.drawLine(mHorizontalOffset,
                                mValuePaddingOffset + ((max_value-average_value)/(max_value-min_value))*(max_pos_y-min_pos_y),
                                mDrawingStopAverageLineX,
                                mValuePaddingOffset + ((max_value-average_value)/(max_value-min_value))*(max_pos_y-min_pos_y),
                                mAverageLinePaint);
                    } else {
                        if(mDrawingStopAverageLineX == -1) {
                            startAverageLineAnimation(mHorizontalOffset, getWidth() - mHorizontalOffset);
                        }
                    }

                }
            } else {
                if(mIsHorizontalValue) {
                    canvas.drawLine(mHorizontalOffset, min_pos_y + (max_pos_y - min_pos_y)/2,
                            getWidth() - mHorizontalOffset, min_pos_y + (max_pos_y - min_pos_y)/2,
                            mAverageLinePaint);
                } else {
                    canvas.drawLine(mHorizontalOffset,
                            mValuePaddingOffset + ((max_value-average_value)/(max_value-min_value))*(max_pos_y-min_pos_y),
                            getWidth() - mHorizontalOffset,
                            mValuePaddingOffset + ((max_value-average_value)/(max_value-min_value))*(max_pos_y-min_pos_y),
                            mAverageLinePaint);
                }
            }
        }


    }

    private void drawTopText(Canvas canvas) {

        float top_text_width = mTopTextPaint.measureText(mTopText);

        RectF rectF_bg = new RectF();
        rectF_bg.left = mHorizontalOffset*2 + top_text_width/2 - top_text_width/2;
        rectF_bg.top = mVerticalOffset + mValuePaddingOffset/2 - mTopTextSize/2;
        rectF_bg.right = rectF_bg.left + top_text_width;
        rectF_bg.bottom = rectF_bg.top + mTopTextSize;
        canvas.drawRect(rectF_bg, mBgPaint);

        //draw top text
        canvas.drawText(mTopText,
                mHorizontalOffset*2 + top_text_width/2,
                mVerticalOffset + mValuePaddingOffset/2 - mTopTextSize/2 + (mTopTextPaint.descent() - mTopTextPaint.ascent() / 2.0f), mTopTextPaint);

    }

    private float getMaxValue() {
        float max = 0;
        if(mLifeLongValue > 0) {
            max = mLifeLongValue;
        }
        for(int i = 0 ; i < mData.size() ; i++) {
            if(mData.get(i).getValue() < 0) {
                continue;
            }
            max = Math.max(max, mData.get(i).getValue());
        }

        return max;
    }

    private float getMinValue() {
        float min = getMaxValue();
        if(mLifeLongValue > 0) {
            min = mLifeLongValue;
        }
        for (int i = 0; i < mData.size(); i++) {
            if(mData.get(i).getValue() < 0) {
                continue;
            }
            min = Math.min(min, mData.get(i).getValue());
        }

        return min;
    }

    private void startSelectedLineAnimation(float startY, final float stopY) {
//        Log.d("simpleLineView", "startAnim --> startY-->" + startY + " | " +  "stopY->" + stopY );
        mDrawingStopSelectedLineY = -1;
        isAnimatingSelectedLine = true;

        ValueAnimator animator = ValueAnimator.ofFloat(startY, stopY);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDrawingStopSelectedLineY = (float) animation.getAnimatedValue();
                if(mDrawingStopSelectedLineY == stopY) {
                    isAnimatingSelectedLine = false;
                    if(mAnimatorSelectedLine != null) {
                        mAnimatorSelectedLine.cancel();
                    }
                }
                postInvalidate();
            }
        });

        mAnimatorSelectedLine = new AnimatorSet();
        mAnimatorSelectedLine.setDuration(1000);
        mAnimatorSelectedLine.play(animator);
        mAnimatorSelectedLine.start();
    }

    private void startLifelongLineAnimation(float startX, final float stopX) {
//        Log.d("simpleLineView", "startAnim --> startX-->" + startX + " | " +  "stopX->" + stopX );
        mDrawingStopLifelongLineX = -1;
        isAnimatingLifelongLine = true;

        ValueAnimator animator = ValueAnimator.ofFloat(startX, stopX);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDrawingStopLifelongLineX = (float) animation.getAnimatedValue();
                if(mDrawingStopLifelongLineX == stopX) {
                    isAnimatingLifelongLine = false;
                    if(mAnimatorLifelongLine != null) {
                        mAnimatorLifelongLine.cancel();
                    }
                }
                postInvalidate();
            }
        });

        mAnimatorLifelongLine = new AnimatorSet();
        mAnimatorLifelongLine.setDuration(2500);
        mAnimatorLifelongLine.play(animator);
        mAnimatorLifelongLine.start();
    }

    private void startAverageLineAnimation(float startX, final float stopX) {
//        Log.d("simpleLineView", "startAnim --> startX-->" + startX + " | " +  "stopX->" + stopX );
        mDrawingStopAverageLineX = -1;
        isAnimatingAverageLine = true;

        ValueAnimator animator = ValueAnimator.ofFloat(startX, stopX);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDrawingStopAverageLineX = (float) animation.getAnimatedValue();
                if(mDrawingStopAverageLineX == stopX) {
                    isAnimatingAverageLine = false;
                    if(mAnimatorAverageLine != null) {
                        mAnimatorAverageLine.cancel();
                    }
                }
                postInvalidate();
            }
        });

        mAnimatorAverageLine = new AnimatorSet();
        mAnimatorAverageLine.setDuration(2500);
        mAnimatorAverageLine.play(animator);
        mAnimatorAverageLine.start();
    }

    private void startLineToAnimation(float startX, float startY, final float stopX, final float stopY) {
//        Log.d("simpleLineView", "startAnim --> startX-->" + startX + " | startY->" + startY + " | stopX->" + stopX + " | stopY->" + stopY);
        isAnimatingLine = true;

        ValueAnimator xAnimator = ValueAnimator.ofObject(new LineEvaluator(), startX, stopX);
        xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animation_value  = (float) animation.getAnimatedValue();
                mDrawingStopX = animation_value;
                if(animation_value == stopX) {
                    isAnimatingLine = false;
                    mDrawingLineIndex++;
                    if(mAnimatorLine != null) {
                        mAnimatorLine.cancel();
                    }
                }
                postInvalidate();
            }
        });

        ValueAnimator yAnimator = ValueAnimator.ofObject(new LineEvaluator(), startY, stopY);
        yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float animation_value  = (float) animation.getAnimatedValue();
                mDrawingStopY = animation_value;

            }
        });



        mAnimatorLine.playTogether(xAnimator, yAnimator);
        mAnimatorLine.setDuration(2500/ mColumnCount);
        mAnimatorLine.start();
    }

    private class LineEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            return (1 - fraction) * (float) startValue + fraction * (float) endValue;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimatorLine != null) {
            mAnimatorLine.cancel();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchDownX = event.getX();
            mTouchDownY = event.getY();
            mDrawingStopSelectedLineY = -1;
            invalidate();
        }

        return super.onTouchEvent(event);
    }

    //---开放接口----------------------------
    public void setVerticalOffset(float offset) {
        this.mVerticalOffset = offset;
    }

    public float getVerticalOffset() {
        return mVerticalOffset;
    }

    public void setHorizontalOffset(float offset) {
        this.mHorizontalOffset = offset;
    }

    public float getHorizontalOffset() {
        return mHorizontalOffset;
    }

    public void setValuePaddingOffset(float offset) {
        this.mValuePaddingOffset = offset;
    }

    public float getValuePaddingOffset() {
        return mValuePaddingOffset;
    }

    public void setData(List<SimpleLineData> data) {
        this.mData = data;
//        mDrawingStopX = -1f;
//        mDrawingStopY = -1f;
//        mDrawingStopAverageLineX = -1f;
//        mDrawingStopSelectedLineY = -1f;
//        mDrawingStopLifelongLineX = -1;
        invalidate();
    }

    public void clearData() {
        this.mData = null;
        invalidate();
    }

    public void setBottomTextList(List<String> bottomTexts) {
        if(bottomTexts != null) {
            if(bottomTexts.size() == mColumnCount) {
                //底部文字的个数必须与列数相等 才生效
                this.mBottomTexts = bottomTexts;
            }
        }

    }

    public void setBottomTextSize(float textSize) {
        this.mBottomTextSize = textSize;
    }

    public void setTopText(String text) {
        this.mTopText = text;
    }

    public void setTopTextSize(float textSize) {
        this.mTopTextSize =  textSize;
    }

    public void setBitmapNormalCircle(Bitmap bitmap) {
        this.mBitmapNormalCircle = bitmap;
    }

    public void setBitmapSelectedCircle(Bitmap bitmap) {
        this.mBitmapSelectedCircle = bitmap;
    }

    public void setTouchPadding(float offset) {
        this.mTouchPadding = offset;
    }

    public void setIsDrawBottomText(boolean enable) {
        this.mIsDrawBottomText = enable;
    }

    public void setBottomTextStepSize(int size) {
        mBottomTextStepSize = size;
    }

    public void setIsDrawAverageLine(boolean enable) {
        this.mIsDrawAverageLine = enable;
    }

    public void setIsDrawVerticalLine(boolean enable) {
        this.mIsDrawVerticalLine = enable;
    }

    public void setIsDrawHorizontalLine(boolean enable) {
        this.mIsDrawHorizontalLine = enable;
    }

    public void setIsDrawTopSideLine(boolean enable) {
        this.mIsDrawTopSideLine = enable;
    }

    public void setIsDrawBottomSideLine(boolean enable) {
        this.mIsDrawBottomSideLine = enable;
    }

    public void setIsDrawLeftSideLine(boolean enable) {
        this.mIsDrawLeftSideLine = enable;
    }

    public void setIsDrawPointSelectedLine(boolean enable) {
        this.mIsDrawPointSelectedLine = enable;
    }

    public void setIsDrawValueTextBottom (boolean enable) {
        this.mIsDrawValueTextBottom = enable;
    }

    public void setAverageIconText(String text) {
        this.mAverageIconText = text;
    }

    public void setAverageIconTextSize(float textSize) {
        this.mAverageIconTextSize = textSize;
    }

    public void setLifelongIconText(String text) {
        this.mLifeLongIconText = text;
    }

    public void setLifelongIconTextSize(float textSize) {
        this.mLifelongIconTextSize = textSize;
    }

    public void setBottomValueTextSize(float textSize) {
        this.mBottomValueTextSize = textSize;
    }

    public void setBottomValuePrefix(String text) {
        this.mBottomValuePrefix = text;
    }

    public void setBottomValueSuffix(String text) {
        this.mBottomValueSuffix = text;
    }

    public void setNormalPointColor(int color) {
        this.mNormalPointColor = color;
        mNormalPointPaint.setColor(mNormalPointColor);
    }

    public void setSelectedPointColor(int color) {
        this.mSelectedPointColor = color;
        mSelectedPointPaint.setColor(mSelectedPointColor);
    }

    public void setPointToLineColor(int color) {
        this.mPointToLineColor = color;
        mLinePaint.setColor(mPointToLineColor);
    }

    public void setBackgroundLineColor(int color) {
        this.mBackgroungLineColor = color;
        mBgLinePaint.setColor(mBackgroungLineColor);
    }

    public void setBottomTextColor(int color) {
        this.mBottomTextColor = color;
        mBottomTextPaint.setColor(mBottomTextColor);
    }

    public void setTopTextColor(int color) {
        this.mTopTextColor = color;
        mTopTextPaint.setColor(mTopTextColor);
    }

    public void setAverageLineColor(int color) {
        this.mAverageLineColor = color;
        mAverageLinePaint.setColor(mAverageLineColor);
    }

    public void setViewBackgroundColor(int color) {
        this.mViewBackgroundColor = color;
        mBgPaint.setColor(mViewBackgroundColor);
    }

    public void setBottomValueTextColor(int color) {
        this.mBottomValueTextColor = color;
        mBottomValuePaint.setColor(mBottomValueTextColor);
    }

    public void setColumnCount(int column) {
        this.mColumnCount = column;
    }

    public void setAverageValue(float value) {
        this.mAverageValue = value;
    }

    public void setLifelongValue(float value) {
        this.mLifeLongValue = value;
    }


}
