/*
 * Copyright 2015 Worldline.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package worldline.com.foldablelayout;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Layout which allow a foldable animation between two other layouts. The only limitation of this
 * layout is than the big view should be exactly twice bigger in height than the small view {@link #setupViews(int, int, int, Context)}.
 */
public class FoldableLayout extends RelativeLayout {

    private static final int ANIMATION_DURATION = 600;

    protected RelativeLayout mContentLayout;
    protected ImageView mImageViewBelow;
    protected ImageView mImageViewAbove;
    protected ViewGroup mViewGroupCover;
    protected ViewGroup mViewGroupDetail;
    protected View mRootView;
    protected Bitmap mDetailBitmap;
    protected Bitmap mDetailTopBitmap;
    protected Bitmap mDetailBottomBitmap;
    protected int mCoverHeight;
    private boolean mIsFolded = true;
    private boolean mIsAnimating = false;
    private Matrix mMatrix;
    private FoldListener mFoldListener = new FoldListener() {
        @Override
        public void onUnFoldStart() {

        }

        @Override
        public void onUnFoldEnd() {

        }

        @Override
        public void onFoldStart() {

        }

        @Override
        public void onFoldEnd() {

        }
    };

    private final TimeInterpolator mTimeInterpolator = new AccelerateDecelerateInterpolator();

    /**
     * Basic constructor.
     *
     * @param context is a valid context.
     */
    public FoldableLayout(Context context) {
        super(context);
        setupView(context);
    }

    /**
     * Basic constructor.
     *
     * @param context is a valid context.
     * @param attrs   used to build this view.
     */
    public FoldableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    /**
     * Basic constructor.
     *
     * @param context      is a valid context.
     * @param attrs        used to build this view.
     * @param defStyleAttr used to build this view.
     */
    public FoldableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(context);
    }

    /**
     * Basic constructor.
     *
     * @param context      is a valid context.
     * @param attrs        used to build this view.
     * @param defStyleAttr used to build this view.
     * @param defStyleRes  used to build this view.
     */
    public FoldableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView(context);
    }

    /**
     * Add a listener to folding animations.
     *
     * @param foldListener is the listener to add.
     */
    public void setFoldListener(FoldListener foldListener) {
        mFoldListener = foldListener;
    }

    public View getCoverView() {
        return mViewGroupCover;
    }

    public View getDetailView() {
        return mViewGroupDetail;
    }

    private void setupView(Context context) {
        setClipChildren(false);
        mRootView = LayoutInflater.from(context).inflate(R.layout.foldable_layout, this, true);
        mContentLayout = (RelativeLayout) findViewById(R.id.foldable_content_view);
        mImageViewBelow = (ImageView) findViewById(R.id.foldable_layout_below_bitmap);
        mImageViewAbove = (ImageView) findViewById(R.id.foldable_layout_above_bitmap);
        mMatrix = new Matrix();
        mMatrix.postScale(1, -1);
    }

    public boolean isFolded() {
        return mIsFolded;
    }

    /**
     * Init the two views which will compose the foldable sides. To ensure nice animations,
     * the big view height should be exactly twice bigger than the small view height.
     *
     * @param coverLayoutId  is the "small" view which is used as the cover.
     * @param detailLayoutId is the "big" view which is used as the detail.
     * @param coverHeight is the height of the cover view.
     * @param context        is a valid context.
     */
    public void setupViews(@LayoutRes int coverLayoutId, @LayoutRes int detailLayoutId, @DimenRes int coverHeight, Context context) {
        mViewGroupCover = (ViewGroup) LayoutInflater.from(context).inflate(coverLayoutId, mContentLayout, false);
        mViewGroupDetail = (ViewGroup) LayoutInflater.from(context).inflate(detailLayoutId, mContentLayout, false);
        mContentLayout.addView(mViewGroupCover);
        mContentLayout.addView(mViewGroupDetail);
        mViewGroupDetail.setVisibility(GONE);
        mCoverHeight = context.getResources().getDimensionPixelSize(coverHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsAnimating) {
            return false;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void clearImageView(ImageView imageView) {
        setImageBackground(imageView, null);
        imageView.setImageDrawable(null);
    }

    private void setImageBackground(ImageView imageView, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(drawable);
        } else {
            imageView.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Fold the layout without animations.
     */
    public void foldWithoutAnimation() {
        if (!mIsFolded && !mIsAnimating) {
            mIsFolded = true;
            clearImageView(mImageViewAbove);
            clearImageView(mImageViewBelow);
            mViewGroupCover.setVisibility(VISIBLE);
            mViewGroupDetail.setVisibility(GONE);
            if (FoldableLayout.this.getLayoutParams() != null) {
                FoldableLayout.this.getLayoutParams().height = mCoverHeight;
            }
            requestLayout();
        }
    }

    /**
     * Unfold the layout without animations.
     */
    public void unfoldWithoutAnimation() {
        if (mIsFolded && !mIsAnimating) {
            mViewGroupCover.setVisibility(GONE);
            mViewGroupDetail.setVisibility(VISIBLE);
            clearImageView(mImageViewAbove);
            clearImageView(mImageViewBelow);
            if (FoldableLayout.this.getLayoutParams() != null) {
                FoldableLayout.this.getLayoutParams().height = mCoverHeight * 2;
            }
            requestLayout();
            mIsFolded = false;
        }
    }

    /**
     * Fold the layout with animations.
     */
    public void foldWithAnimation() {
        if (!mIsAnimating) {
            computeBitmaps();
            final Bitmap rotatedAboveBitmap;
            rotatedAboveBitmap = Bitmap.createBitmap(mDetailBottomBitmap, 0, 0, mDetailBottomBitmap.getWidth(), mDetailBottomBitmap.getHeight(), mMatrix, true);
            final Drawable belowShadow;
            final Drawable aboveShadow;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                belowShadow = getResources().getDrawable(R.drawable.shadow, null);
                aboveShadow = getResources().getDrawable(R.drawable.shadow, null);
            } else {
                belowShadow = getResources().getDrawable(R.drawable.shadow);
                aboveShadow = getResources().getDrawable(R.drawable.shadow);
            }

            ValueAnimator animator;
            animator = ValueAnimator.ofFloat(-180, 0);

            final int initialHeight = this.getHeight();

            animator.setInterpolator(mTimeInterpolator);
            animator.setDuration(ANIMATION_DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private boolean mReplaceDone = false;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedFraction() >= 0.5 && !mReplaceDone) {
                        mReplaceDone = true;
                        clearImageView(mImageViewAbove);
                        mViewGroupCover.setVisibility(VISIBLE);
                    }
                    mContentLayout.setRotationX((Float) animation.getAnimatedValue());
                    belowShadow.setAlpha((int) (255 * animation.getAnimatedFraction()));
                    aboveShadow.setAlpha((int) (255 * animation.getAnimatedFraction()));
                    FoldableLayout.this.getLayoutParams().height = (int) (initialHeight - initialHeight / 2 * animation.getAnimatedFraction());
                    FoldableLayout.this.requestLayout();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mFoldListener.onFoldStart();
                    mIsAnimating = true;
                    mContentLayout.setRotationX(180);
                    mViewGroupDetail.setVisibility(GONE);
                    mImageViewAbove.setImageDrawable(belowShadow);
                    setImageBackground(mImageViewAbove, new BitmapDrawable(getResources(), rotatedAboveBitmap));
                    setImageBackground(mImageViewBelow, new BitmapDrawable(getResources(), mDetailTopBitmap));
                    mImageViewBelow.setImageDrawable(aboveShadow);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    clearImageView(mImageViewBelow);
                    mIsFolded = true;
                    mIsAnimating = false;
                    mFoldListener.onFoldEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }

    /**
     * Unfold the view with animations.
     */
    public void unfoldWithAnimation() {
        if (!mIsAnimating) {
            computeBitmaps();
            final Bitmap rotatedAboveBitmap;
            rotatedAboveBitmap = Bitmap.createBitmap(mDetailBottomBitmap, 0, 0, mDetailBottomBitmap.getWidth(), mDetailBottomBitmap.getHeight(), mMatrix, true);
            ValueAnimator animator;
            animator = ValueAnimator.ofFloat(0, -180);

            mContentLayout.setPivotY(mCoverHeight);
            mContentLayout.setPivotX(mViewGroupCover.getWidth() / 2);

            final int initialHeight = mCoverHeight;

            final Drawable belowShadow;
            final Drawable aboveShadow;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                belowShadow = getResources().getDrawable(R.drawable.shadow, null);
                aboveShadow = getResources().getDrawable(R.drawable.shadow, null);
            } else {
                belowShadow = getResources().getDrawable(R.drawable.shadow);
                aboveShadow = getResources().getDrawable(R.drawable.shadow);
            }

            animator.setInterpolator(mTimeInterpolator);
            animator.setDuration(ANIMATION_DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private boolean mReplaceDone = false;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedFraction() >= 0.5 && !mReplaceDone) {
                        mReplaceDone = true;
                        mViewGroupCover.setVisibility(GONE);
                        setImageBackground(mImageViewAbove, new BitmapDrawable(getResources(), rotatedAboveBitmap));
                        mImageViewAbove.setImageDrawable(aboveShadow);
                    }
                    mContentLayout.setRotationX((Float) animation.getAnimatedValue());

                    belowShadow.setAlpha((int) (255 * (1 - animation.getAnimatedFraction())));
                    aboveShadow.setAlpha((int) (255 * (1 - animation.getAnimatedFraction())));

                    FoldableLayout.this.getLayoutParams().height = (int) (initialHeight + initialHeight * animation.getAnimatedFraction());
                    FoldableLayout.this.requestLayout();

                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mFoldListener.onUnFoldStart();
                    mIsAnimating = true;
                    setImageBackground(mImageViewBelow, new BitmapDrawable(getResources(), mDetailTopBitmap));
                    mImageViewBelow.setImageDrawable(belowShadow);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mViewGroupDetail.setVisibility(VISIBLE);
                    clearImageView(mImageViewBelow);
                    clearImageView(mImageViewAbove);
                    mContentLayout.setRotationX(0);
                    mIsFolded = false;
                    mIsAnimating = false;
                    mFoldListener.onUnFoldEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }

    private void computeBitmaps() {
        mDetailBitmap = computeBitmap(mViewGroupDetail);
        mDetailTopBitmap = Bitmap.createBitmap(mDetailBitmap, 0, 0, mDetailBitmap.getWidth(), mDetailBitmap.getHeight() / 2);
        mDetailBottomBitmap = Bitmap.createBitmap(mDetailBitmap, 0, mDetailBitmap.getHeight() / 2, mDetailBitmap.getWidth(), mDetailBitmap.getHeight() / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private Bitmap computeBitmap(ViewGroup viewGroup) {
        Bitmap bitmap;
        Rect rect = new Rect();
        viewGroup.getWindowVisibleDisplayFrame(rect);
        viewGroup.destroyDrawingCache();
        viewGroup.setDrawingCacheEnabled(true);
        viewGroup.buildDrawingCache(true);
        bitmap = viewGroup.getDrawingCache(true);
        /**
         * After rotation, the DecorView has no height and no width. Therefore
         * .getDrawingCache() return null. That's why we  have to force measure and layout.
         */
        if (bitmap == null) {
            viewGroup.measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mCoverHeight * 2, MeasureSpec.EXACTLY)
            );
            viewGroup.layout(0, 0, viewGroup.getMeasuredWidth(),
                    viewGroup.getMeasuredHeight());
            viewGroup.destroyDrawingCache();
            viewGroup.setDrawingCacheEnabled(true);
            viewGroup.buildDrawingCache(true);
            bitmap = viewGroup.getDrawingCache(true);
        }
        if (bitmap == null) {
            return Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        } else {
            return bitmap.copy(Bitmap.Config.ARGB_8888, false);
        }
    }

    /**
     * Interface to dispatch folding events.
     */
    public interface FoldListener {

        /**
         * Dispatch when un foldWithAnimation start.
         */
        void onUnFoldStart();

        /**
         * Dispatch when un foldWithAnimation end.
         */
        void onUnFoldEnd();

        /**
         * Dispatch when foldWithAnimation start.
         */
        void onFoldStart();

        /**
         * Dispatch when foldWithAnimation end.
         */
        void onFoldEnd();
    }
}