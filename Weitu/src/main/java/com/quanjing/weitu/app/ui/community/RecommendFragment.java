/**
 * 达人页面
 */
package com.quanjing.weitu.app.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.model.MWTRecommendManager;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.ui.common.MWTDataRetriever;
import com.quanjing.weitu.app.ui.community.recommend.MWTRecommendAdapter;


public class RecommendFragment extends BaseFragment {

    private PullToRefreshListView circlListView;
    private MWTRecommendAdapter adapter;
    private MWTDataRetriever dataRetriver;
    public static int COUNT = 20;

    public RecommendFragment() {
        super();
        this.setDataRetriver(new MWTDataRetriever() {
            @Override
            public void refresh(MWTCallback callback) {
                if (adapter != null) {
                    adapter.refresh(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }

            @Override
            public void loadMore(MWTCallback callback) {
                if (adapter != null) {
                    adapter.loadMore(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mMainView != null) {
            ViewGroup parent = (ViewGroup) mMainView.getParent();
            if (parent != null)
                parent.removeView(mMainView);
        }

        mMainView = inflater.inflate(R.layout.fragment_talent, container, false);
        circlListView = (PullToRefreshListView) mMainView.findViewById(R.id.talentListView);
        circlListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
        adapter = new MWTRecommendAdapter(getActivity());
        circlListView.setAdapter(adapter);
        MWTRecommendManager cm = MWTRecommendManager.getInstance();
        cm.refreshCircles(1, COUNT, new MWTCallback() {
            @Override
            public void success() {
            }

            @Override
            public void failure(MWTError error) {
            }
        });
        return mMainView;
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshIfNeeded();
    }

    private void performRefresh() {
        if (dataRetriver != null) {
            dataRetriver.refresh(new MWTCallback() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error) {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    private void performLoadMore() {
        if (dataRetriver != null) {
            dataRetriver.loadMore(new MWTCallback() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(MWTError error) {
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    public void setDataRetriver(MWTDataRetriever dataRetriver) {
        this.dataRetriver = dataRetriver;
    }

    private void startRefreshAnimation() {
        if (circlListView != null) {
            circlListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (circlListView != null) {
            circlListView.onRefreshComplete();
        }
    }

}
