package com.ookiisoftware.protips.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PostAdapter;
import com.ookiisoftware.protips.auxiliar.Import;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class InicioFragment extends Fragment {

    private Activity activity;
    private PostAdapter adapter;

    public InicioFragment (Activity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        Init(view);
        return view;
    }

    private void Init(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.recycler);

        adapter = new PostAdapter(activity, Import.get.tipsters.postes()) {
            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                ImageViewTouch viewTouch = (ImageViewTouch) v;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            if (viewTouch.getScale() > 1)
                                recyclerView.setLayoutFrozen(true);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            recyclerView.setLayoutFrozen(false);
                            break;
                        }
                    }
                return super.onTouch(v, event);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public void adapterUpdate() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
