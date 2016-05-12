package github.com.pwalan.dfood.fragment;

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
import github.com.pwalan.dfood.utils.SuperTreeViewAdapter;
import github.com.pwalan.dfood.utils.TreeViewAdapter;

/**
 * 美食食谱
 */
public class RecipeFragment extends Fragment {
    ExpandableListView expandableList;
    TreeViewAdapter adapter;
    Button btn_chinese, btn_foreign;

    // Sample data set. children[i] contains the children (String[]) for groups[i].
    public String[] groups;
    public String[][] child;

    public String[] group_chinese={"鲁菜","川菜","粤菜","闽菜","湘菜","徽菜","浙江菜","淮扬菜"};
    public String[][] child_chinese={
            {"糖醋鲤鱼","百花大虾"},
            {"2"},
            {"3"},
            {"4"},
            {"5"},
            {"6"},
            {"7"},
            {"8"}
    };

    public String[] group_foreign={"韩国菜","日本料理","东南亚菜","法国菜","土耳其菜"};
    public String[][] child_foreign={
        {"1"},
        {"2"},
        {"3"},
        {"4"},
        {"5"},
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

            if(v==btn_chinese){
                groups=group_chinese;
                child=child_chinese;
            }else if(v==btn_foreign){
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
                    return false;
                }
            });
        }
    }
}
