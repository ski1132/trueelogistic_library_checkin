package com.trueelogistics.checkin.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.ShakeActivity
import kotlinx.android.synthetic.main.fragment_shake_finding.*

class ShakeFindingFragment : Fragment() {

    private var shakeAnimation: AnimationDrawable? = null

    companion object {
        var showView = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shake_finding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_page.setOnClickListener {
            activity?.onBackPressed()
        }
        nearByAnimation()
        fineHub()
    }

    override fun onResume() {
        fineHub()
        super.onResume()
    }

    private fun nearByAnimation() {
        loading_hub_shake.setBackgroundResource(R.drawable.nearby_finding)
        shakeAnimation = loading_hub_shake.background as AnimationDrawable
        shakeAnimation?.start()
    }

    private fun fineHub(){
        activity?.let {
            ShakeActivity().itemShake(it, object : ShakeActivity.ShakeCallback {
                override fun onFound(hubId: String?, hubName: String?) {
                    if (showView) {
                        it.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_shake, ShakeHubFragment())
                            ?.addToBackStack(null)
                            ?.commit()
                        showView = false
                    }
                }

            })
        }
    }
}
