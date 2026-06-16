package com.almizan.mobile.front.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.almizan.mobile.databinding.FragmentNotificationsBinding
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationAdapter { notif ->
            if (!notif.isRead()) {
                viewModel.marquerCommeLue(notif.id)
            }
            // Si la notification contient un lien (marcheId), vous pouvez naviguer vers MarcheDetailActivity ici
        }

        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter

        binding.btnMarkAllRead.setOnClickListener {
            viewModel.marquerToutesLues()
            Snackbar.make(binding.root, "Toutes les alertes ont été marquées comme lues.", Snackbar.LENGTH_SHORT).show()
        }

        viewModel.notifications.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = resource.data
                    adapter.submitList(list)
                    binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = resource.message
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadNotifications()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}