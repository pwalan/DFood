package github.com.pwalan.dfood.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.com.pwalan.dfood.R;

/**
 * 季节套餐
 */
public class SeasonsetFragment extends Fragment {

    private Button btn_spring, btn_summer, btn_autumn, btn_winter;
    SimpleAdapter simpleAdapter;
    ListView list;

    public String[] names = new String[]{
            "白切鸡", "夫妻肺片", "麻婆豆腐", "七星鱼丸", "石锅拌饭", "糖醋鲤鱼"
    };

    public int[] imgs = new int[]{
            R.drawable.picture0, R.drawable.picture1, R.drawable.picture2,
            R.drawable.picture3, R.drawable.picture4, R.drawable.picture5
    };

    //春
    public String[] spr_names = new String[]{
            "七星鱼丸", "石锅拌饭", "糖醋鲤鱼"
    };
    public int[] spr_imgs = new int[]{
            R.drawable.picture3, R.drawable.picture4, R.drawable.picture5
    };
    //夏
    public String[] sum_names = new String[]{
            "白切鸡", "夫妻肺片", "七星鱼丸", "石锅拌饭"
    };
    public int[] sum_imgs = new int[]{
            R.drawable.picture0, R.drawable.picture1,
            R.drawable.picture3, R.drawable.picture4
    };
    //秋
    public String[] aut_names = new String[]{
            "白切鸡", "七星鱼丸", "石锅拌饭"
    };
    public int[] aut_imgs = new int[]{
            R.drawable.picture0, R.drawable.picture3, R.drawable.picture4
    };
    //冬
    public String[] win_names = new String[]{
            "麻婆豆腐", "七星鱼丸", "石锅拌饭", "糖醋鲤鱼"
    };
    public int[] win_imgs = new int[]{
            R.drawable.picture2, R.drawable.picture3, R.drawable.picture4, R.drawable.picture5
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seasonset, container, false);

        list = (ListView) view.findViewById(R.id.season_list);

        btn_spring=(Button)view.findViewById(R.id.btn_spring);
        btn_spring.setOnClickListener(new ClickEvent());
        btn_summer=(Button)view.findViewById(R.id.btn_summer);
        btn_summer.setOnClickListener(new ClickEvent());
        btn_autumn=(Button)view.findViewById(R.id.btn_autumn);
        btn_autumn.setOnClickListener(new ClickEvent());
        btn_winter=(Button)view.findViewById(R.id.btn_winter);
        btn_winter.setOnClickListener(new ClickEvent());

        return view;
    }

    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(btn_spring==v){
                names=spr_names;
                imgs=spr_imgs;
            }else if(btn_summer==v){
                names=sum_names;
                imgs=sum_imgs;
            }else if(btn_autumn==v){
                names=aut_names;
                imgs=aut_imgs;
            }else if(btn_winter==v){
                names=win_names;
                imgs=win_imgs;
            }

            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

            for(int i = 0; i < names.length;i++) {
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("img", imgs[i]);
                listItem.put("name", names[i]);
                listItems.add(listItem);
            }

            simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
                    new String[]{"img", "name"},
                    new int[]{R.id.shared_pictures, R.id.picture_name});

            list.setAdapter(simpleAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity(), "你点击了 " + names[position], Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
