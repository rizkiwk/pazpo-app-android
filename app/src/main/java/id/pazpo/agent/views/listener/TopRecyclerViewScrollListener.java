package id.pazpo.agent.views.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by wais on 2/2/17.
 */

public abstract class TopRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    RecyclerView.LayoutManager mLayoutManager;

    public TopRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public TopRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold    = visibleThreshold * layoutManager.getSpanCount();
    }

    public TopRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold    = visibleThreshold * layoutManager.getSpanCount();
    }

    public int getFirstVisibleItem(int[] firstVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < firstVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = firstVisibleItemPositions[i];
            }
            else if (firstVisibleItemPositions[i] > maxSize) {
                maxSize = firstVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        super.onScrolled(view, dx, dy);

        int firstVisibleItemPosition    = 0;
        int lastVisibleItemPosition     = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] firstVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(null);
            int[] lastVisibleItemPositions  = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            // get maximum element within the list
            firstVisibleItemPosition    = getFirstVisibleItem(firstVisibleItemPositions);
            lastVisibleItemPosition     = getLastVisibleItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof GridLayoutManager) {
            firstVisibleItemPosition    = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            lastVisibleItemPosition     = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            firstVisibleItemPosition    = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            lastVisibleItemPosition     = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        Log.e("[ TopScroll ]", "onScrolled || firstVisibleItemPosition = "+(firstVisibleItemPosition + totalItemCount)+" || totalItemCount = "+totalItemCount);

        if (loading && (firstVisibleItemPosition + totalItemCount) > totalItemCount) {
            this.currentPage            = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            this.loading                = false;
        }

        if (!loading && (firstVisibleItemPosition + totalItemCount) == totalItemCount) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    // Call this method whenever performing new searches
    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}
