package ubr.flash.flashcards.util

import android.animation.Animator
import android.os.Bundle
import android.view.ViewPropertyAnimator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import ubr.flash.flashcards.R
import ubr.flash.flashcards.ui.fragments.AllSetsFragment


fun ViewPropertyAnimator.setOnFinishListener(f:()->Unit): ViewPropertyAnimator {

    setListener(object : Animator.AnimatorListener{
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            f()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    })
    return this
}


fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

fun AllSetsFragment.gotoCardPage(bundle: Bundle){
    findNavController().navigate(R.id.action_allSetsFragment_to_cardPageFragment,bundle)
}