package com.example.limbitlesssummerproject19.Album;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.limbitlesssummerproject19.R;

import java.io.File;
import java.util.ArrayList;

public class FragmentStarredAlbums extends Fragment {

    private InternalStorage internalStorage = new InternalStorage("Starred");

    public FragmentStarredAlbums() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_albums_fragment, container, false);

        // Creating a list of file from internal storage
        File[] files = internalStorage.getFilesFromInternalStorage(getContext());
        ArrayList<Pair<String, String>> sessionFiles = internalStorage.getListFromFiles(getContext(), files);


        // Creates a grid layout of two columns to display the images
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        // Uses RecyclerView to inflate the images in the position and recycle the child view
        RecyclerView adapter = v.findViewById(R.id.recycle_view);
        //adapter.setLayoutManager(gridLayoutManager);
        adapter.setLayoutManager(linearLayoutManager);
        GalleryAdapter recyclerAdapter = new GalleryAdapter(getContext(), sessionFiles, files, "Starred");
        adapter.setAdapter(recyclerAdapter);

        return v;
    }
}
