package com.mhandharbeni.saklaruniversal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mhandharbeni.saklaruniversal.adapter.DetailAdapter;
import com.mhandharbeni.saklaruniversal.databinding.FragmentScreenBinding;

import java.util.ArrayList;
import java.util.List;

public class ScreenFragment extends Fragment implements DetailAdapter.DetailCallback {
    private final String TAG = ScreenFragment.class.getSimpleName();
    FragmentScreenBinding binding;

    List<FullscreenActivity.DataDetailScreen> listDetail;
    DetailAdapter detailAdapter;

    public static ScreenFragment newInstance(List<FullscreenActivity.DataDetailScreen> listDetail) {
        return new ScreenFragment(listDetail);
    }

    public ScreenFragment(List<FullscreenActivity.DataDetailScreen> listDetail) {
        this.listDetail = listDetail;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
    }

    void initAdapter() {
        detailAdapter = new DetailAdapter(requireActivity().getApplicationContext(), listDetail, this);
        GridLayoutManager glm = new GridLayoutManager(requireActivity(), 2);
        binding.listData.setLayoutManager(glm);
        binding.listData.setAdapter(detailAdapter);
    }

    @Override
    public void onDetailClick(FullscreenActivity.DataDetailScreen dataDetailScreen) {

    }
}
