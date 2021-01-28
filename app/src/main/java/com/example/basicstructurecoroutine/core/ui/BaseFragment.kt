package com.example.basicstructurecoroutine.core.ui

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.basicstructurecoroutine.core.util.hideKeyboard

/**
 * Created by JeeteshSurana.
 */

abstract class BaseFragment : Fragment() {

    var mBaseActivity: BaseActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mBaseActivity = context as BaseActivity?
    }

    override fun onDestroyView() {
        activity?.hideKeyboard()
        super.onDestroyView()
    }
}