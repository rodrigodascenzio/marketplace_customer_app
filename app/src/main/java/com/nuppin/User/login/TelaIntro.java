package com.nuppin.User.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.nuppin.R;

public class TelaIntro extends AppCompatActivity {

    private MaterialButton email, celular;
    private ViewPager mImageViewPager;
    private AutoScrollViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        email = findViewById(R.id.email);
        celular = findViewById(R.id.celular);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it;
                it = new Intent(TelaIntro.this, TelaCadastroEmail.class);
                startActivity(it);
            }
        });

        celular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it;
                it = new Intent(TelaIntro.this, TelaCadastroNumber.class);
                startActivity(it);
            }
        });


        //mImageViewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        //tabLayout.setupWithViewPager(mImageViewPager, true);
        //mImageViewPager.setAdapter(new CustomPagerAdapter(this));

        viewPager = findViewById(R.id.viewPager);
        viewPager.startAutoScroll();
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setStopScrollWhenTouch(true);
        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.setAdapter(new CustomPagerAdapter(this));

    }

    class CustomPagerAdapter extends PagerAdapter {

        int[] mResources = {
                R.drawable.ic_undraw_shopping_food,
                R.drawable.ic_undraw_shopping_product,
                R.drawable.ic_undraw_barber,
                R.drawable.ic_undraw_chatting
        };

        String[] mResourcesString = {
                "Fácil e rapido para pedir sua comida",
                "Tem todo tipo de produto aqui também",
                "Alem disso, da para agendar horário para cuidar da beleza",
                "E caso haja alguma dúvida, pode ser resolvida pelo chat do pedido"
        };

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.vp_slider, container, false);
            ImageView imageView = itemView.findViewById(R.id.viewPagerItem);
            TextView textView = itemView.findViewById(R.id.txt);
            imageView.setImageResource(mResources[position]);
            textView.setText(mResourcesString[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ConstraintLayout) object);
        }
    }
}
