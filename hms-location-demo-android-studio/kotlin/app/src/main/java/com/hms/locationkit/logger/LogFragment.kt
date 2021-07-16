/**
 * Copyright 2020 Huawei Technologies co, Ltd All
 * Rights reserved
 * Licenced under the Apache License,Version 2.0(the "License");
 * you may not use this file except in compliance with license
 * you may obtain a copy of the license at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by application law or agreed to in writing software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permission and
 * limitations under the License
 */

package com.hms.locationkit.logger

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.hms.locationkit.R

class LogFragment : Fragment() {

    private var mLogView: LogView? = null

    private var mScrollView: ScrollView? = null

    private fun inflateViews(): View {
        mScrollView = ScrollView(activity)
        mScrollView?.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mLogView = LogView(activity).apply {
            minLines = 8
            isClickable = true
            gravity = Gravity.TOP
            id = R.id.output_information_id
            isCursorVisible = false
            isFocusable = false
            isFocusableInTouchMode = false
        }
        mScrollView?.addView(
            mLogView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        return mScrollView as ScrollView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val result = inflateViews()
        mLogView?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                mScrollView?.post { mScrollView!!.fullScroll(ScrollView.FOCUS_DOWN) }
            }
        })
        val gestureDetector = GestureDetector(activity, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                mLogView?.setText("")
                return true
            }
        })

        mLogView?.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        return result
    }

    fun getLogView(): LogView? {
        return mLogView
    }
}
