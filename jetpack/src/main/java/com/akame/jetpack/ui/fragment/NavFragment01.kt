package com.akame.jetpack.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.akame.jetpack.R
import kotlinx.android.synthetic.main.fragment_nav_fragment01.*
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder


class NavFragment01 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_fragment01, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        next.setOnClickListener {
            next.setIcon(MaterialDrawableBuilder.IconValue.PAUSE)
            val bundle = Bundle()
            Navigation.findNavController(it)
                .navigate(R.id.action_navFragment013_to_navFragment023, bundle)
        }
    }
}