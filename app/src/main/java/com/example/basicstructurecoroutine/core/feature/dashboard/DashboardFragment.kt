package com.example.basicstructurecoroutine.core.feature.dashboard

/**
 * Created by JeeteshSurana.
 */
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basicstructurecoroutine.R
import com.example.basicstructurecoroutine.core.feature.activity.VideoPlayerActivity
import com.example.basicstructurecoroutine.core.feature.adapter.DashboardAdapter
import com.example.basicstructurecoroutine.core.feature.viewmodel.DashboardViewModel
import com.example.basicstructurecoroutine.core.model.response.YouTubeListResponseItem
import com.example.basicstructurecoroutine.core.ui.BaseFragment
import com.example.basicstructurecoroutine.core.util.KEY_VIDEO_URL
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DashboardFragment : BaseFragment() {

    private var mAdapter: DashboardAdapter? = null
    private var list = ArrayList<YouTubeListResponseItem>()
    private val mViewModel: DashboardViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        mViewModel.list.observe(this, {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                mAdapter?.notifyDataSetChanged()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        apiCall()
    }

    private fun apiCall() {
        lifecycleScope.launch {
            mViewModel.getData()
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context!!)
        rv_dashboard.layoutManager = layoutManager
        mAdapter = DashboardAdapter(list, object : DashboardAdapter.ItemClickListener {
            override fun itemClick(position: Int) {
                startActivity(Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString(KEY_VIDEO_URL, list[position].url)
                    })
                })
            }
        })
        rv_dashboard.adapter = mAdapter
    }
}