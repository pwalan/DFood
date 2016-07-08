package github.com.pwalan.dfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.List;

import github.com.pwalan.dfood.R;
import github.com.pwalan.dfood.ShowRecipeActivity;
import github.com.pwalan.dfood.utils.SuperTreeViewAdapter;
import github.com.pwalan.dfood.utils.TreeViewAdapter;

/**
 * 美食食谱
 */
public class RecipeFragment extends Fragment {
    ExpandableListView expandableList;
    TreeViewAdapter adapter;
    Button btn_chinese, btn_foreign;

    public String[] groups;
    public String[][] child;

    public String[] group_chinese={"鲁菜","川菜","粤菜","闽菜","湘菜","徽菜","浙江菜","淮扬菜"};
    public String[][] child_chinese={
            {"糖醋鲤鱼","百花大虾","德州扒鸡","蟹黄海参"},
            {"麻婆豆腐","夫妻肺片","宫保鸡丁","糖醋排骨"},
            {"白切鸡","叉烧","肠粉","干蒸凤爪"},
            {"七星鱼丸","福跳墙","醉排骨","红槽鱼排"},
            {"湘西酸肉","东安鸡","祖庵鱼翅","荷包肚"},
            {"火腿炖甲鱼","红烧果子狸","黄山炖鸽","腌鲜鳜鱼"},
            {"金鸡报春","菊花鱼面","鱼羊鲜","鹊桥相会"},
            {"软兜长鱼","红烧狮子头","平桥豆腐","虾籽蒲菜"}
    };

    public String[] group_foreign={"韩国菜","日本料理","东南亚菜","法国菜","土耳其菜"};
    public String[][] child_foreign={
        {"石锅拌饭","炒年糕","紫菜包饭","辣白菜","大酱汤"},
        {"寿司","味增汤","日式炸豆腐","海鲜刺身","北海道拉面"},
        {"咖喱鸡块","星洲炒河粉","越南春卷","海鲜菠萝饭","冬阴功汤"},
        {"奶油蘑菇汤","法式烤纸蒸鱼","法式鹅肝","法式千层糕","法式烩土豆","烟熏鸭脯肉"},
        {"土耳其烤肉","土耳其披萨","葡萄叶卷养肝","羊肚清汤"},
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        btn_chinese = (Button) view.findViewById(R.id.btn_chinese);
        btn_chinese.setOnClickListener(new ClickEvent());
        btn_foreign = (Button) view.findViewById(R.id.btn_foreign);
        btn_foreign.setOnClickListener(new ClickEvent());
        adapter = new TreeViewAdapter(getActivity(), TreeViewAdapter.PaddingLeft >> 1);
        expandableList = (ExpandableListView) view.findViewById(R.id.ExpandableListView01);

        return view;
    }

    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            adapter.RemoveAll();
            adapter.notifyDataSetChanged();

            if(btn_chinese==v){
                groups=group_chinese;
                child=child_chinese;
            }else if(btn_foreign==v){
                groups=group_foreign;
                child=child_foreign;
            }

            List<TreeViewAdapter.TreeNode> treeNode = adapter.GetTreeNode();
            for (int i = 0; i < groups.length; i++) {
                TreeViewAdapter.TreeNode node = new TreeViewAdapter.TreeNode();
                node.parent = groups[i];
                for (int ii = 0; ii < child[i].length; ii++) {
                    node.childs.add(child[i][ii]);
                }
                treeNode.add(node);
            }
            adapter.UpdateTreeNode(treeNode);
            expandableList.setAdapter(adapter);
            expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView arg0, View arg1,
                                            int parent, int children, long arg4) {
                    String str = "parent id:" + String.valueOf(parent) + ",children id:" + String.valueOf(children);
                    Toast.makeText(getActivity(), child[parent][children], Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),ShowRecipeActivity.class);
                    intent.putExtra("rname",child[parent][children]);
                    intent.putExtra("uid",1);
                    startActivity(intent);
                    return false;
                }
            });
        }
    }
}
