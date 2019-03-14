package bokecc.shortvideosdk.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bokecc.shortvideosdk.R;


public class DialogFilter extends Dialog {
    private Activity context;
    private FilterAdapter filterAdapter;
    private List<FilterType> datas;
    private SelectFilter selectFilter;

    public DialogFilter(@NonNull Activity context,SelectFilter selectFilter) {
        super(context, R.style.DialogFilter);
        this.context = context;
        this.selectFilter = selectFilter;
        datas = new ArrayList<>();
    }

    public interface SelectFilter{
        void type(int i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFilterDatas();
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_filter, null);
        setContentView(view);
        RecyclerView rv_view = (RecyclerView) view.findViewById(R.id.rv_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(OrientationHelper. HORIZONTAL);
        rv_view.setLayoutManager(layoutManager);
        filterAdapter = new FilterAdapter(datas);
        rv_view.setAdapter(filterAdapter);

        filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FilterType item, int position) {
                selectFilter.type(item.getType());
            }
        });


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 1.0);
//        lp.height = (int) (d.heightPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setDimAmount(0f);

    }

    private void initFilterDatas() {
        FilterType filterType0 = new FilterType();
        filterType0.setEnglishName("NONE");
        filterType0.setType(0);
        datas.add(filterType0);

        FilterType filterType1 = new FilterType();
        filterType1.setEnglishName("BEAUTY");
        filterType1.setType(1);
        datas.add(filterType1);

        FilterType filterType2 = new FilterType();
        filterType2.setEnglishName("FAIRYTALE");
        filterType2.setType(2);
        datas.add(filterType2);

        FilterType filterType3 = new FilterType();
        filterType3.setEnglishName("SUNRISE");
        filterType3.setType(3);
        datas.add(filterType3);

        FilterType filterType4 = new FilterType();
        filterType4.setEnglishName("SUNSET");
        filterType4.setType(4);
        datas.add(filterType4);

        FilterType filterType5 = new FilterType();
        filterType5.setEnglishName("WHITECAT");
        filterType5.setType(5);
        datas.add(filterType5);

        FilterType filterType6 = new FilterType();
        filterType6.setEnglishName("BLACKCAT");
        filterType6.setType(6);
        datas.add(filterType6);

        FilterType filterType7 = new FilterType();
        filterType7.setEnglishName("SKINWHITEN");
        filterType7.setType(7);
        datas.add(filterType7);

        FilterType filterType8 = new FilterType();
        filterType8.setEnglishName("HEALTHY");
        filterType8.setType(8);
        datas.add(filterType8);

        FilterType filterType9 = new FilterType();
        filterType9.setEnglishName("SWEETS");
        filterType9.setType(9);
        datas.add(filterType9);

        FilterType filterType10 = new FilterType();
        filterType10.setEnglishName("ROMANCE");
        filterType10.setType(10);
        datas.add(filterType10);

        FilterType filterType11 = new FilterType();
        filterType11.setEnglishName("SAKURA");
        filterType11.setType(11);
        datas.add(filterType11);

        FilterType filterType12 = new FilterType();
        filterType12.setEnglishName("WARM");
        filterType12.setType(12);
        datas.add(filterType12);

        FilterType filterType13 = new FilterType();
        filterType13.setEnglishName("ANTIQUE");
        filterType13.setType(13);
        datas.add(filterType13);

        FilterType filterType14 = new FilterType();
        filterType14.setEnglishName("NOSTALGIA");
        filterType14.setType(14);
        datas.add(filterType14);

        FilterType filterType15 = new FilterType();
        filterType15.setEnglishName("CALM");
        filterType15.setType(15);
        datas.add(filterType15);

        FilterType filterType16 = new FilterType();
        filterType16.setEnglishName("LATTE");
        filterType16.setType(16);
        datas.add(filterType16);

        FilterType filterType17 = new FilterType();
        filterType17.setEnglishName("TENDER");
        filterType17.setType(17);
        datas.add(filterType17);

        FilterType filterType18 = new FilterType();
        filterType18.setEnglishName("COOL");
        filterType18.setType(18);
        datas.add(filterType18);

        FilterType filterType19 = new FilterType();
        filterType19.setEnglishName("EMERALD");
        filterType19.setType(19);
        datas.add(filterType19);

        FilterType filterType20 = new FilterType();
        filterType20.setEnglishName("EVERGREEN");
        filterType20.setType(20);
        datas.add(filterType20);

        FilterType filterType21 = new FilterType();
        filterType21.setEnglishName("CRAYON");
        filterType21.setType(21);
        datas.add(filterType21);

        FilterType filterType22 = new FilterType();
        filterType22.setEnglishName("SKETCH");
        filterType22.setType(22);
        datas.add(filterType22);

        FilterType filterType23 = new FilterType();
        filterType23.setEnglishName("AMARO");
        filterType23.setType(23);
        datas.add(filterType23);

        FilterType filterType24 = new FilterType();
        filterType24.setEnglishName("BRANNAN");
        filterType24.setType(24);
        datas.add(filterType24);

        FilterType filterType25 = new FilterType();
        filterType25.setEnglishName("BROOKLYN");
        filterType25.setType(25);
        datas.add(filterType25);

        FilterType filterType26 = new FilterType();
        filterType26.setEnglishName("EARLYBIRD");
        filterType26.setType(26);
        datas.add(filterType26);

        FilterType filterType27 = new FilterType();
        filterType27.setEnglishName("FREUD");
        filterType27.setType(27);
        datas.add(filterType27);

        FilterType filterType28 = new FilterType();
        filterType28.setEnglishName("HEFE");
        filterType28.setType(28);
        datas.add(filterType28);

        FilterType filterType29 = new FilterType();
        filterType29.setEnglishName("HUDSON");
        filterType29.setType(29);
        datas.add(filterType29);

        FilterType filterType30 = new FilterType();
        filterType30.setEnglishName("INKWELL");
        filterType30.setType(30);
        datas.add(filterType30);

        FilterType filterType31 = new FilterType();
        filterType31.setEnglishName("KEVIN");
        filterType31.setType(31);
        datas.add(filterType31);

        FilterType filterType32 = new FilterType();
        filterType32.setEnglishName("LOMO");
        filterType32.setType(32);
        datas.add(filterType32);

        FilterType filterType33 = new FilterType();
        filterType33.setEnglishName("N1977");
        filterType33.setType(33);
        datas.add(filterType33);

        FilterType filterType34 = new FilterType();
        filterType34.setEnglishName("NASHVILLE");
        filterType34.setType(34);
        datas.add(filterType34);

        FilterType filterType35 = new FilterType();
        filterType35.setEnglishName("PIXAR");
        filterType35.setType(35);
        datas.add(filterType35);

        FilterType filterType36 = new FilterType();
        filterType36.setEnglishName("RISE");
        filterType36.setType(36);
        datas.add(filterType36);

        FilterType filterType37 = new FilterType();
        filterType37.setEnglishName("SIERRA");
        filterType37.setType(37);
        datas.add(filterType37);

        FilterType filterType38 = new FilterType();
        filterType38.setEnglishName("SUTRO");
        filterType38.setType(38);
        datas.add(filterType38);

        FilterType filterType39 = new FilterType();
        filterType39.setEnglishName("TOASTER2");
        filterType39.setType(39);
        datas.add(filterType39);

        FilterType filterType40 = new FilterType();
        filterType40.setEnglishName("VALENCIA");
        filterType40.setType(40);
        datas.add(filterType40);

        FilterType filterType41 = new FilterType();
        filterType41.setEnglishName("WALDEN");
        filterType41.setType(41);
        datas.add(filterType41);

        FilterType filterType42 = new FilterType();
        filterType42.setEnglishName("XPROII");
        filterType42.setType(42);
        datas.add(filterType42);

    }

}
