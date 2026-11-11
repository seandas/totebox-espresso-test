package com.seandas.totebox.support

import android.view.View
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import com.seandas.totebox.R

/**
 * Drags the handle of a SwipeToConfirmView from where it sits to [fraction] of the track
 * (0 = start, 1 = end), the way a finger would. A plain swipeRight() starts at a fixed point on
 * the view, which may miss the handle; this always starts on it.
 */
fun dragSliderHandle(fraction: Float): ViewAction = GeneralSwipeAction(
    Swipe.SLOW,
    CoordinatesProvider { slider -> handleCenterAt(slider, handle(slider).translationX) }, // where it is now
    CoordinatesProvider { slider -> handleCenterAt(slider, handleTravel(slider) * fraction) },
    Press.FINGER,
)

private fun handle(slider: View): View = slider.findViewById(R.id.swipe_thumb)

private fun handleTravel(slider: View): Float =
    (slider.width - slider.paddingLeft - slider.paddingRight - handle(slider).width).toFloat()

/** Screen coordinates of the handle's centre when it has moved [travel] pixels from the start. */
private fun handleCenterAt(slider: View, travel: Float): FloatArray {
    val thumb = handle(slider)
    val origin = IntArray(2).also { slider.getLocationOnScreen(it) }
    return floatArrayOf(
        origin[0] + thumb.left + travel + thumb.width / 2f,
        origin[1] + thumb.top + thumb.height / 2f,
    )
}
