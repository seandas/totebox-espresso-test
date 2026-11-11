package com.seandas.totebox.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import com.seandas.totebox.R

/**
 * A "swipe to confirm" slider: drag the handle to the end of the track to confirm. Letting go
 * before [CONFIRM_FRACTION] of the way slides it back without confirming.
 *
 * Swiping is not the only way: TalkBack offers a "Place order" action and a keyboard can press
 * Enter, both through [performClick].
 */
class SwipeToConfirmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    /** Called once the slider is confirmed. Call [reset] if the action can't go ahead. */
    var onConfirmed: (() -> Unit)? = null

    /** True after confirming, until [reset]: the slider stays at the end and ignores touches. */
    var isProcessing = false
        private set

    private val thumb: View
    private val label: TextView
    private var dragging = false
    private var downX = 0f
    private var startTranslation = 0f

    /** How far the handle can travel: the track's inner width less the handle. */
    private val maxTravel: Float
        get() = (width - paddingLeft - paddingRight - thumb.width).toFloat().coerceAtLeast(0f)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_swipe_to_confirm, this, true)
        thumb = findViewById(R.id.swipe_thumb)
        label = findViewById(R.id.swipe_label)
        setBackgroundResource(R.drawable.bg_swipe_track)
        val inset = resources.getDimensionPixelSize(R.dimen.swipe_track_inset)
        setPadding(inset, inset, inset, inset)
        isClickable = true // for the keyboard and accessibility, not for taps (see onTouchEvent)
        isFocusable = true
        contentDescription = context.getString(R.string.swipe_to_place_order)
        ViewCompat.replaceAccessibilityAction(
            this, AccessibilityActionCompat.ACTION_CLICK, context.getString(R.string.place_order),
        ) { _, _ -> performClick() }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || isProcessing) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!isOnThumb(event.x)) return false // only the handle starts a drag
                dragging = true
                downX = event.x
                startTranslation = thumb.translationX
                thumb.animate().cancel()
                parent?.requestDisallowInterceptTouchEvent(true) // keep the scroll view from taking it
                return true
            }
            MotionEvent.ACTION_MOVE -> if (dragging) {
                moveThumbTo(startTranslation + event.x - downX)
                return true
            }
            MotionEvent.ACTION_UP -> if (dragging) {
                dragging = false
                // Count where the finger lifted too: a quick swipe can end well past its last move event
                moveThumbTo(startTranslation + event.x - downX)
                if (maxTravel > 0 && thumb.translationX >= maxTravel * CONFIRM_FRACTION) performClick()
                else slideThumbTo(0f)
                return true
            }
            MotionEvent.ACTION_CANCEL -> if (dragging) {
                dragging = false
                slideThumbTo(0f)
                return true
            }
        }
        return false // taps do nothing; only a full swipe confirms
    }

    /** Confirms: from a completed swipe, the keyboard (Enter) or an accessibility action. */
    override fun performClick(): Boolean {
        super.performClick()
        if (isProcessing || !isEnabled) return false
        isProcessing = true
        slideThumbTo(maxTravel)
        label.alpha = 1f
        label.setText(R.string.placing_order)
        onConfirmed?.invoke()
        return true
    }

    /** Back to the start, ready to swipe again (for example after a validation error). */
    fun reset() {
        isProcessing = false
        label.setText(R.string.swipe_to_place_order)
        slideThumbTo(0f)
    }

    private fun isOnThumb(x: Float): Boolean {
        val left = thumb.left + thumb.translationX
        return x >= left && x <= left + thumb.width
    }

    private fun moveThumbTo(translation: Float) {
        thumb.translationX = translation.coerceIn(0f, maxTravel)
        // The label fades as the handle moves across it
        label.alpha = if (maxTravel > 0) 1f - thumb.translationX / maxTravel else 1f
    }

    private fun slideThumbTo(translation: Float) {
        thumb.animate().translationX(translation).setDuration(SNAP_MS)
            .setUpdateListener { if (!isProcessing) label.alpha = 1f - thumb.translationX / maxTravel.coerceAtLeast(1f) }
            .start()
    }

    private companion object {
        const val CONFIRM_FRACTION = 0.85f
        const val SNAP_MS = 200L
    }
}
