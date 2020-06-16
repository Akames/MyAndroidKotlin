package com.akame.jetpack.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.akame.jetpack.R
import kotlinx.android.synthetic.main.fragment_nav_fragment02.*

/**
 * A simple [Fragment] subclass.
 */
class NavFragment02 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nav_fragment02, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvIntent.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_navFragment023_to_jetPackActivity)
        }


    }


}
