package github.com.pwalan.dfood.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import github.com.pwalan.dfood.R;

public class UpSucceedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_up_succeed, container, false);

        TextView tv_test=(TextView)view.findViewById(R.id.tv_test);
        Bundle bundle=getArguments();
        if(bundle!=null){
            tv_test.setText(bundle.getString("data"));
        }

        return view;
    }
}
