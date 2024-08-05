package com.app.dognutrition.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.app.dognutrition.databinding.FragmentDiscoverBinding;

public class DiscoverFragment extends Fragment {

private FragmentDiscoverBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        DiscoverViewModel discoverViewModel =
                new ViewModelProvider(this).get(DiscoverViewModel.class);

    binding = FragmentDiscoverBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}