package com.tablayoutlibrary.app;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ${wei} on 2017/6/1.
 */

public class CommonTabLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "debug_CommonTabLayout";
    private final Context mContext;
    private int mWidth, mHeight;

    private final String INDICATOR_COLOR = "#4B6A87";
    //底部导航
    private LinearLayout mTabsConstainer;

    //indicator
    private int mIndicatorColor;
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginBottom;
    private int mIndicatorGravity;
    private boolean mIndicatorEnable;
    private boolean mIndicatorAnimEnable;


    //text
    private int mTextSelectorColor;
    private int mTextUnSelectorColor;
    private float mTextSize;
    /**
     * 图标是否显示
     */
    private boolean mIconVisible;

    /**
     * 图标的位置
     */
    private int mIconGravity;
    private float mIconHeight;
    private float mIconWidth;
    private float mIconMargin;

    /**
     * tab数量
     */
    private int mTabCount;
    private int mTabWidth;
    private float mTabPadding;


    //当前选中的position
    private int mCurrTabPosition = 0;
    //上一个选中的位置
    private int mPreTabPosition = -1;


    private final int LEFT = 0x11;
    private final int TOP = 0x12;
    private final int RIGHT = 0x13;
    private final int BOTTOM = 0x14;

    private final int INDICATOR_LEFT = 0x1001;
    private final int INDICATOR_TOP = 0x1002;
    private final int INDICATOR_RIGHT = 0x1003;
    private final int INDICATOR_BOTTOM = 0x1004;

    private ArrayList<TabEntity> mTabList;
    private OnSelectorListener mListener;
    //最外层的padding
    private float mPadding;

    private RectF mIndicatorRectF;
    private Paint mIndicatorPaint;

    private IndicatorPoint mCurrentPoint = new IndicatorPoint();
    private IndicatorPoint mPrePoint = new IndicatorPoint();
    private IndicatorPoint mAnimPoint = new IndicatorPoint();
    /**
     * anim
     */
    private ValueAnimator mValueAnimator;
    private OvershootInterpolator mInterpolator = new OvershootInterpolator(1.5f);
    private ViewPager viewPager;

    public CommonTabLayout(@NonNull Context context) {
        this(context, null);
    }

    public CommonTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写onDraw方法，需要调用这个方法清除flag
        setClipChildren(false);
        setClipToPadding(false);


        this.mContext = context;
        mTabsConstainer = new LinearLayout(context);
        addView(mTabsConstainer);

        obtainAttributes(context, attrs);
        setPadding(0, 0, 0, 0);//去除边距
        //初始化画笔和indicator的rect
        init();

        mValueAnimator = ValueAnimator.ofObject(new PointEvaluator(), mPrePoint, mCurrentPoint);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.setDuration(500);

        mValueAnimator.addUpdateListener(this);

    }

    private void init() {
        if (!mIndicatorEnable) {
            return;
        }
        mIndicatorRectF = new RectF();
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(mIndicatorColor);
    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     */
    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTabLayout);

        mTextSize = ta.getDimension(R.styleable.CommonTabLayout_tl_textsize, sp2px(13f));
        mTextSelectorColor = ta.getResourceId(R.styleable.CommonTabLayout_tl_textSelectColor, Color.parseColor("#545454"));
        mTextUnSelectorColor = ta.getResourceId(R.styleable.CommonTabLayout_tl_textUnSelectColor, Color.parseColor("#545454"));

        mIconVisible = ta.getBoolean(R.styleable.CommonTabLayout_tl_iconVisible, true);
        mIconHeight = ta.getDimension(R.styleable.CommonTabLayout_tl_iconHeight, dp2px(0));
        mIconWidth = ta.getDimension(R.styleable.CommonTabLayout_tl_iconWidth, dp2px(0));
        mIconGravity = ta.getInt(R.styleable.CommonTabLayout_tl_iconGravity, TOP);
        mIconMargin = ta.getDimension(R.styleable.CommonTabLayout_tl_iconMargin, dp2px(2.5f));

        mTabPadding = ta.getDimension(R.styleable.CommonTabLayout_tl_tabPadding, dp2px(0));
        mPadding = ta.getDimension(R.styleable.CommonTabLayout_tl_padding, dp2px(5));

        //inidcator
        mIndicatorEnable = ta.getBoolean(R.styleable.CommonTabLayout_tl_indicatorEnable, false);
        mIndicatorAnimEnable = ta.getBoolean(R.styleable.CommonTabLayout_tl_indicatorAnimEnable, false);
        mIndicatorColor = ta.getColor(R.styleable.CommonTabLayout_tl_indicatorColor, Color.parseColor(INDICATOR_COLOR));
        mIndicatorHeight = ta.getDimension(R.styleable.CommonTabLayout_tl_indicatorHeight, dp2px(4));
        mIndicatorWidth = ta.getDimension(R.styleable.CommonTabLayout_tl_indicatorWidth, 0);
        mIndicatorGravity = ta.getInt(R.styleable.CommonTabLayout_tl_indicatorGravity, INDICATOR_TOP);
        mIndicatorMarginLeft = ta.getDimension(R.styleable.CommonTabLayout_tl_indicatorMarginLeft, 0);
        mIndicatorMarginTop = ta.getDimension(R.styleable.CommonTabLayout_tl_indicatorMarginTop, 0);
        mIndicatorMarginRight = ta.getDimension(R.styleable.CommonTabLayout_tl_indicatorMarginRight, 0);
        mIndicatorMarginBottom = ta.getDimension(R.styleable.CommonTabLayout_tl_indicatorMarginBottom, 0);
        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIndicatorEnable || mIndicatorRectF == null || mIndicatorPaint == null) {
            return;
        }

        if (mIndicatorWidth == 0) {
            mIndicatorWidth = mWidth / mTabCount;
        }

        if (mPreTabPosition == mCurrTabPosition || !mIndicatorAnimEnable) {
            mAnimPoint.left = mCurrTabPosition * mIndicatorWidth;
            mAnimPoint.right = mCurrTabPosition * mIndicatorWidth + mIndicatorWidth;
        }
        if (mIndicatorGravity == INDICATOR_TOP) {
            mIndicatorRectF.top = 0;
        } else if (mIndicatorGravity == INDICATOR_BOTTOM) {
            mIndicatorRectF.top = mHeight - mIndicatorHeight;
        }
//        mIndicatorRectF.left = mIndicatorMarginLeft + mCurrTabPosition * mIndicatorWidth;
//        mIndicatorRectF.right = mIndicatorRectF.left + mIndicatorWidth;
        mIndicatorRectF.left = mAnimPoint.left;
        mIndicatorRectF.right = mAnimPoint.right;
        mIndicatorRectF.bottom = mIndicatorRectF.top + mIndicatorHeight;
        //绘制indicator
        canvas.drawRect(mIndicatorRectF, mIndicatorPaint);
    }

    private void notifyTab() {
        mTabsConstainer.removeAllViews();//清空原来添加的view
        this.mTabCount = mTabList.size();
        addTab();
    }

    /**
     * 创建并添加tabView
     * 并设置图标位置
     */
    private void addTab() {
        //添加tab
        for (int i = 0; i < mTabCount; i++) {
            final int index = i;
            View tabView = LayoutInflater.from(mContext).inflate(R.layout.layout_common, null);
            tabView.setPadding((int) mTabPadding, (int) mTabPadding, (int) mTabPadding, (int) mTabPadding);
            ImageView iv_icon = (ImageView) tabView.findViewById(R.id.iv_icon);
            final TextView tv_text = (TextView) tabView.findViewById(R.id.tv_text);
            tv_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            RelativeLayout.LayoutParams tvParams = (RelativeLayout.LayoutParams) tv_text.getLayoutParams();
            RelativeLayout.LayoutParams ivParams = (RelativeLayout.LayoutParams) iv_icon.getLayoutParams();
            ivParams.height = mIconHeight > 0 ? (int) mIconHeight : LinearLayout.LayoutParams.WRAP_CONTENT;
            ivParams.width = mIconWidth > 0 ? (int) mIconWidth : LinearLayout.LayoutParams.WRAP_CONTENT;
            if (mIconVisible) {
                iv_icon.setVisibility(View.VISIBLE);
                if (mIconGravity == LEFT) {//图标在左侧
                    tvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    ivParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    tvParams.addRule(RelativeLayout.RIGHT_OF, iv_icon.getId());
                    ivParams.rightMargin = (int) mIconMargin;
                } else if (mIconGravity == RIGHT) {//图标在右侧
                    tvParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    ivParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    ivParams.addRule(RelativeLayout.RIGHT_OF, tv_text.getId());
                    ivParams.leftMargin = (int) mIconMargin;
                } else if (mIconGravity == TOP) {//图标在上面
                    tvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    tvParams.addRule(RelativeLayout.BELOW, iv_icon.getId());
                    ivParams.bottomMargin = (int) mIconMargin;
                } else {//图标在下面
                    tvParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    ivParams.addRule(RelativeLayout.BELOW, tv_text.getId());
                    ivParams.topMargin = (int) mIconMargin;
                }
                iv_icon.setLayoutParams(ivParams);
                tv_text.setLayoutParams(tvParams);
            } else {
                iv_icon.setVisibility(View.GONE);
                tvParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            }

            TabEntity customTabEntity = mTabList.get(i);
            if (mIconVisible) {
                StateListDrawable drawableStateList = createDrawableStateList(customTabEntity.getTabSelectedIcon(), customTabEntity.getTabUnSelectedIcon());
                iv_icon.setImageDrawable(drawableStateList);
            }
            tv_text.setText(customTabEntity.getTabTitle());
            tv_text.setTextColor(createColorStateList(mTextSelectorColor, mTextUnSelectorColor));

            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrTabPosition != index) {
                        setCurrentTab(index);
                    }
                }
            });
            //每一个Tab的布局参数
            LinearLayout.LayoutParams lp_tab = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            //如果已经设置宽度
            if (mTabWidth > 0) {
                lp_tab = new LinearLayout.LayoutParams(mTabWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            }
            mTabsConstainer.addView(tabView, i, lp_tab);
            LinearLayout.LayoutParams tabParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
            tabParams.topMargin = (int) mIndicatorMarginTop;
            tabParams.bottomMargin = (int) mIndicatorMarginBottom;
            tabView.setLayoutParams(tabParams);
            tabView.requestLayout();
        }
        //初始化第一个
        if (mTabCount > 0) {
            setCurrentTab(0);
        }
    }

    /**
     * 设置当前选中的tab
     *
     * @param position
     */
    public void setCurrentTab(int position) {
        if (mTabCount == 0) {
            throw new IllegalArgumentException("必须先添加tab-->setTabData()");
        }
        mPreTabPosition = mCurrTabPosition;
        mCurrTabPosition = position;
        if (mListener != null) {
            mListener.onSeletor(position);
        }
        if (viewPager != null) {
            if (viewPager.getAdapter() == null || viewPager.getAdapter().getCount() == 0) {
                throw new IllegalArgumentException("viewpager还没有设置adapter");
            }
            viewPager.setCurrentItem(position);
        }
        updateIndicator(position);
        updateTabSelect(position);
        if (mPrePoint.left == mCurrentPoint.left && mPrePoint.right == mCurrentPoint.right || !mIndicatorAnimEnable) {
            postInvalidate();
        } else {
            mValueAnimator.start();
        }
    }

    private void updateIndicator(int currPoint) {
        mPrePoint.left = mPreTabPosition * mIndicatorWidth;
        mPrePoint.right = mPreTabPosition * mIndicatorWidth + mIndicatorWidth;
        mCurrentPoint.left = currPoint * mIndicatorWidth;
        mCurrentPoint.right = currPoint * mIndicatorWidth + mIndicatorWidth;
    }

    /**
     * 更新选中按钮，改变状态
     *
     * @param position
     */
    private void updateTabSelect(int position) {
        for (int i = 0; i < mTabCount; i++) {
            View childAtView = mTabsConstainer.getChildAt(i);
            childAtView.findViewById(R.id.tv_text).setSelected(i == position);
            childAtView.findViewById(R.id.iv_icon).setSelected(i == position);
        }
    }

    /**
     * 生成图片状态选择器
     */
    private StateListDrawable createDrawableStateList(int selectorIcon, int unSelectorIcon) {
        if (selectorIcon == -1 || unSelectorIcon == -1) {
            return null;
        }
        Resources resources = getResources();
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_selected}, resources.getDrawable(selectorIcon));
        sd.addState(new int[]{-android.R.attr.state_selected}, resources.getDrawable(unSelectorIcon));
        return sd;
    }

    /**
     * 生成颜色选择器
     *
     * @param selectorColor
     * @param unSelectorColor
     * @return
     */
    private ColorStateList createColorStateList(int selectorColor, int unSelectorColor) {
        if (selectorColor == -1 || unSelectorColor == -1) {
            return null;
        }
        Resources resources = getResources();
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{-android.R.attr.state_selected};
        int[] colors = new int[]{resources.getColor(selectorColor), resources.getColor(unSelectorColor)};
        ColorStateList cs = new ColorStateList(states, colors);
        return cs;

    }

    public void setTabData(ArrayList<TabEntity> list) {
        this.mTabList = list;
        notifyTab();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mAnimPoint = (IndicatorPoint) animation.getAnimatedValue();
        postInvalidate();
        Log.e(TAG, "onAnimationUpdate:  = " + mAnimPoint.left + "   right = " + mAnimPoint.right);
    }

    protected int dp2px(float dpValue) {
        final float density = getResources().getDisplayMetrics().density;
        return (int) (density * dpValue + 0.5f);
    }

    protected int sp2px(float spValue) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

    class IndicatorPoint {
        public float left;
        public float right;
    }

    /**
     * 指示器移动估值器
     */
    class PointEvaluator implements TypeEvaluator<IndicatorPoint> {

        @Override
        public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
            float left = startValue.left + fraction * (endValue.left - startValue.left);
            float right = startValue.right + fraction * (endValue.right - startValue.right);
            IndicatorPoint point = new IndicatorPoint();
            point.left = left;
            point.right = right;
            return point;
        }
    }

    public interface OnSelectorListener {
        void onSeletor(int position);
    }

    public void setOnSelectorListener(OnSelectorListener listener) {
        this.mListener = listener;
        if (mCurrTabPosition > -1) {
            listener.onSeletor(mCurrTabPosition);
        }
    }

    public void setUpWithViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (mAnimPoint == null) {
                    mAnimPoint = new IndicatorPoint();
                }
                mAnimPoint.left = (position + positionOffset) * mIndicatorWidth;
                mAnimPoint.right = mAnimPoint.left + mIndicatorWidth;
                postInvalidate();
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentTab(position);
            }
        });
    }

}
