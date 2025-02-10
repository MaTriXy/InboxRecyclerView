package me.saket.inboxrecyclerview.animation

import android.view.View
import me.saket.inboxrecyclerview.InboxRecyclerView
import me.saket.inboxrecyclerview.page.ExpandablePageLayout

/**
 * [https://www.youtube.com/watch?v=WQGtweo-2dc]
 */
internal class SplitExpandAnimator : ItemExpandAnimator() {

  override fun onPageMove(
    recyclerView: InboxRecyclerView,
    page: ExpandablePageLayout,
    anchorViewOverlay: View?
  ) {
    if (!page.isMoving) {
      // Reset everything. This is also useful when the content size
      // changes, say as a result of the soft-keyboard getting dismissed.
      resetAnimation(recyclerView, anchorViewOverlay)
      return
    }

    val anchorIndex = recyclerView.expandedItemLoc.viewIndex
    val anchorViewLocation = recyclerView.expandedItemLoc.locationOnScreen

    val pageTop = page.locationOnScreen().top
    val pageBottom = pageTop + page.clippedDimens.height()

    // Move the RecyclerView rows with the page.
    if (anchorViewOverlay != null) {
      val distanceExpandedTowardsTop = pageTop - anchorViewLocation.top
      val distanceExpandedTowardsBottom = pageBottom - anchorViewLocation.bottom
      recyclerView.moveListItems(anchorIndex, distanceExpandedTowardsTop, distanceExpandedTowardsBottom)

    } else {
      // Anchor View can be null when the page was expanded from
      // an arbitrary location. See InboxRecyclerView#expandFromTop().
      recyclerView.moveListItems(anchorIndex, 0, pageBottom - pageTop)
    }

    // Fade in the anchor row with the expanding/collapsing page.
    anchorViewOverlay?.alpha = 1f - page.expandRatio(recyclerView)
  }

  private fun InboxRecyclerView.moveListItems(
    anchorIndex: Int,
    distanceExpandedTowardsTop: Int,
    distanceExpandedTowardsBottom: Int
  ) {
    for (childIndex in 0 until childCount) {
      getChildAt(childIndex).translationY = when {
        childIndex <= anchorIndex -> distanceExpandedTowardsTop.toFloat()
        else -> distanceExpandedTowardsBottom.toFloat()
      }
    }
  }

  override fun resetAnimation(
    recyclerView: InboxRecyclerView,
    anchorViewOverlay: View?
  ) {
    for (childIndex in 0 until recyclerView.childCount) {
      val childView = recyclerView.getChildAt(childIndex)
      childView.translationY = 0F
      childView.alpha = 1F
    }
    anchorViewOverlay?.alpha = 0f
  }
}
