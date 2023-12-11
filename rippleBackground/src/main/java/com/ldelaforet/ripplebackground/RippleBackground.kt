package com.ldelaforet.ripplebackground

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout

class RippleBackground : RelativeLayout {
    private var rippleColor = 0
    private var rippleStrokeWidth = 0f
    private var rippleRadius = 0f
    private var rippleDurationTime = 0
    private var rippleAmount = 0
    private var rippleDelay = 0
    private var rippleScale = 0f
    private var rippleType = 0
    private var paint: Paint? = null
    var isRippleAnimationRunning = false
        private set
    private var animatorSet: AnimatorSet? = null
    private var animatorList: ArrayList<Animator>? = null
    private var rippleParams: LayoutParams? = null
    private val rippleViewList = ArrayList<RippleView>()

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // Utilisez le bon espace de noms pour accéder à vos attributs définis dans attrs.xml
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground)
        if (isInEditMode) return
        requireNotNull(attrs) { "Attributes should be provided to this view," }
        rippleColor = typedArray.getColor(
            R.styleable.RippleBackground_rb_color, resources.getColor(
                R.color.rippleColor
            )
        )
        rippleStrokeWidth = typedArray.getDimension(
            R.styleable.RippleBackground_rb_strokeWidth, resources.getDimension(
                R.dimen.rippleStrokeWidth
            )
        )
        rippleRadius = typedArray.getDimension(
            R.styleable.RippleBackground_rb_radius, resources.getDimension(
                R.dimen.rippleRadius
            )
        )
        rippleDurationTime =
            typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME)
        rippleAmount =
            typedArray.getInt(R.styleable.RippleBackground_rb_rippleAmount, DEFAULT_RIPPLE_COUNT)
        rippleScale = typedArray.getFloat(R.styleable.RippleBackground_rb_scale, DEFAULT_SCALE)
        rippleType = typedArray.getInt(R.styleable.RippleBackground_rb_type, DEFAULT_FILL_TYPE)
        typedArray.recycle()
        rippleDelay = rippleDurationTime / rippleAmount
        paint = Paint()
        paint!!.isAntiAlias = true
        if (rippleType == DEFAULT_FILL_TYPE) {
            rippleStrokeWidth = 0f
            paint!!.style = Paint.Style.FILL
        } else paint!!.style = Paint.Style.STROKE
        paint!!.color = rippleColor
        rippleParams = LayoutParams(
            (2 * (rippleRadius + rippleStrokeWidth)).toInt(),
            (2 * (rippleRadius + rippleStrokeWidth)).toInt()
        )
        rippleParams!!.addRule(CENTER_IN_PARENT, TRUE)
        animatorSet = AnimatorSet()
        animatorSet!!.interpolator = AccelerateDecelerateInterpolator()
        animatorList = ArrayList()
        for (i in 0 until rippleAmount) {
            val rippleView = RippleView(getContext())
            addView(rippleView, rippleParams)
            rippleViewList.add(rippleView)
            val scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale)
            scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleXAnimator.repeatMode = ObjectAnimator.RESTART
            scaleXAnimator.startDelay = (i * rippleDelay).toLong()
            scaleXAnimator.duration = rippleDurationTime.toLong()
            animatorList!!.add(scaleXAnimator)
            val scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale)
            scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleYAnimator.repeatMode = ObjectAnimator.RESTART
            scaleYAnimator.startDelay = (i * rippleDelay).toLong()
            scaleYAnimator.duration = rippleDurationTime.toLong()
            animatorList!!.add(scaleYAnimator)
            val alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f)
            alphaAnimator.repeatCount = ObjectAnimator.INFINITE
            alphaAnimator.repeatMode = ObjectAnimator.RESTART
            alphaAnimator.startDelay = (i * rippleDelay).toLong()
            alphaAnimator.duration = rippleDurationTime.toLong()
            animatorList!!.add(alphaAnimator)
        }
        animatorSet!!.playTogether(animatorList as Collection<Animator>?)
    }

    fun changeRippleColor(newColor: Int) {
        if (isRippleAnimationRunning) {
            // Change the color of the Paint object
            paint!!.color = newColor

            // Invalidate the view to trigger a redraw with the new color
            invalidate()

            // Update the color for all existing RippleViews
            for (rippleView in rippleViewList) {
                rippleView.updateColor(newColor)
            }
        }
    }

    fun setRippleSpeed(rippleDurationTime: Int) {
        this.rippleDurationTime = rippleDurationTime
        rippleDelay = rippleDurationTime / rippleAmount

        for (i in 0 until rippleAmount) {
            val scaleXAnimator = animatorList!![i * 3] as ObjectAnimator
            val scaleYAnimator = animatorList!![i * 3 + 1] as ObjectAnimator
            val alphaAnimator = animatorList!![i * 3 + 2] as ObjectAnimator

            scaleXAnimator.startDelay = (i * rippleDelay).toLong()
            scaleXAnimator.duration = rippleDurationTime.toLong()

            scaleYAnimator.startDelay = (i * rippleDelay).toLong()
            scaleYAnimator.duration = rippleDurationTime.toLong()

            alphaAnimator.startDelay = (i * rippleDelay).toLong()
            alphaAnimator.duration = rippleDurationTime.toLong()
        }
    }

    fun setRippleCount(rippleAmount: Int) {
        this.rippleAmount = rippleAmount
        rippleDelay = rippleDurationTime / rippleAmount

        animatorSet!!.end()
        animatorSet!!.cancel()
        animatorList!!.clear()

        for (i in 0 until rippleAmount) {
            val rippleView = RippleView(getContext())
            addView(rippleView, rippleParams)
            rippleViewList.add(rippleView)
            val scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale)
            scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleXAnimator.repeatMode = ObjectAnimator.RESTART
            scaleXAnimator.startDelay = (i * rippleDelay).toLong()
            scaleXAnimator.duration = rippleDurationTime.toLong()
            animatorList!!.add(scaleXAnimator)
            val scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale)
            scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleYAnimator.repeatMode = ObjectAnimator.RESTART
            scaleYAnimator.startDelay = (i * rippleDelay).toLong()
            scaleYAnimator.duration = rippleDurationTime.toLong()
            animatorList!!.add(scaleYAnimator)
            val alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f)
            alphaAnimator.repeatCount = ObjectAnimator.INFINITE
            alphaAnimator.repeatMode = ObjectAnimator.RESTART
            alphaAnimator.startDelay = (i * rippleDelay).toLong()
            alphaAnimator.duration = rippleDurationTime.toLong()
            animatorList!!.add(alphaAnimator)
        }
        animatorSet!!.playTogether(animatorList as Collection<Animator>?)
    }

    private inner class RippleView(context: Context?) : View(context) {
        private var rippleViewColor: Int

        init {
            this.visibility = INVISIBLE
            rippleViewColor = rippleColor // Set the initial color
        }

        fun updateColor(newColor: Int) {
            rippleViewColor = newColor
            // Invalidate the view to trigger a redraw with the new color
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            val radius = Math.min(width, height) / 2
            paint!!.color = rippleViewColor // Use the updated color
            canvas.drawCircle(
                radius.toFloat(), radius.toFloat(), radius - rippleStrokeWidth,
                paint!!
            )
        }
    }

    fun startRippleAnimation() {
        if (!isRippleAnimationRunning) {
            for (rippleView in rippleViewList) {
                rippleView.visibility = VISIBLE
            }
            animatorSet!!.start()
            isRippleAnimationRunning = true
        }
    }

    fun stopRippleAnimation() {
        if (isRippleAnimationRunning) {
            animatorSet!!.end()
            isRippleAnimationRunning = false
        }
    }

    companion object {
        private const val DEFAULT_RIPPLE_COUNT = 6
        private const val DEFAULT_DURATION_TIME = 3000
        private const val DEFAULT_SCALE = 6.0f
        private const val DEFAULT_FILL_TYPE = 0
    }
}