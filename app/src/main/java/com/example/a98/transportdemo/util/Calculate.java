package com.example.a98.transportdemo.util;

import com.amap.api.maps2d.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;

public class Calculate {
    public static LatLng gen_circle(List<LatLng> point_list) {
//        List<LatLng> point_list=new ArrayList<>();
//        point_list.add(new LatLng(116.356674,39.901695));
//        point_list.add(new LatLng(116.355885,39.898999));
//        point_list.add(new LatLng(116.353402,39.898366));

        // write your code here
        double x1, x2, x3, x, y1, y2, y3, y, r;
        x1 = point_list.get(0).latitude;
        x2 = point_list.get(1).latitude;
        x3 = point_list.get(2).latitude;
        y1 = point_list.get(0).longitude;
        y2 = point_list.get(1).longitude;
        y3 = point_list.get(2).longitude;
        x=((y2-y1)*(y3*y3-y1*y1+x3*x3-x1*x1)-(y3-y1)*(y2*y2-y1*y1+x2*x2-x1*x1))/(2.0*((x3-x1)*(y2-y1)-(x2-x1)*(y3-y1)));
        y=((x2-x1)*(x3*x3-x1*x1+y3*y3-y1*y1)-(x3-x1)*(x2*x2-x1*x1+y2*y2-y1*y1))/(2.0*((y3-y1)*(x2-x1)-(y2-y1)*(x3-x1)));
        r=(x1-x)*(x1-x+(y1-y)*(y1-y));
        LatLng result = new LatLng(x,y);
        System.out.println(result.toString());
        return result;
    }
    public static Double gen_radius(List<LatLng> point_list) {
//        List<LatLng> point_list=new ArrayList<>();
//        point_list.add(new LatLng(116.356674,39.901695));
//        point_list.add(new LatLng(116.355885,39.898999));
//        point_list.add(new LatLng(116.353402,39.898366));

        // write your code here
        double x1, x2, x3, x, y1, y2, y3, y, r;
        x1 = point_list.get(0).latitude;
        x2 = point_list.get(1).latitude;
        x3 = point_list.get(2).latitude;
        y1 = point_list.get(0).longitude;
        y2 = point_list.get(1).longitude;
        y3 = point_list.get(2).longitude;
        x=((y2-y1)*(y3*y3-y1*y1+x3*x3-x1*x1)-(y3-y1)*(y2*y2-y1*y1+x2*x2-x1*x1))/(2.0*((x3-x1)*(y2-y1)-(x2-x1)*(y3-y1)));
        y=((x2-x1)*(x3*x3-x1*x1+y3*y3-y1*y1)-(x3-x1)*(x2*x2-x1*x1+y2*y2-y1*y1))/(2.0*((y3-y1)*(x2-x1)-(y2-y1)*(x3-x1)));
        r=(x1-x)*(x1-x+(y1-y)*(y1-y));
        return r;
    }
    //    求平交角
    public static double gen_angle(List<LatLng> point_list) {
        // write your code here
        // 点1,2在一条街，3,4在一条街，返回360度的角度值

        double x1, x2, x3, x4, y1, y2, y3, y4;
        x1 = point_list.get(0).latitude;
        x2 = point_list.get(1).latitude;
        x3 = point_list.get(2).latitude;
        x4 = point_list.get(3).latitude;
        y1 = point_list.get(0).longitude;
        y2 = point_list.get(1).longitude;
        y3 = point_list.get(2).longitude;
        y4 = point_list.get(3).longitude;

        double k1, k2, K, theta;
        if ((y1 - y2)==0 || (y3 - y4)==0)
        {
            return 0.0;
        }
        k1 = (x1 - x2)/(y1 - y2);
        k2 = (x3 - x4)/(y3 - y4);
        K = abs((k2 - k1)/(1 + k1*k2));
        theta = toDegrees(atan(K));
        System.out.println(theta);
        return theta;
    }
    //    求面积
    public static double gen_area(List<LatLng> point_list) {
        // write your code here
//        List<LatLng> point_list=new ArrayList<>();
//        point_list.add(new LatLng(0.0,0.0));
//        point_list.add(new LatLng(0.0,1.0));
//        point_list.add(new LatLng(1.0,1.0));
//        point_list.add(new LatLng(1.0,0.0));

        if (point_list.size()<3)
        {
            return 0;
        }
        double S=0;
        double K=9101160000.085981;
        int i=0;
        for(i = 0 ;i < point_list.size();i++) {
            point_list.get(i);
            S += 0.5*(point_list.get(i).latitude * point_list.get((i+1)%point_list.size()).longitude - point_list.get(i).longitude * point_list.get((i+1)%point_list.size()).latitude);
        }
        S = abs(S)*K;
        System.out.println(S);
        return S;
    }
    public static String gen_double_string(Double d){
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(d);//format 返回的是字符串
        return p;
    }
}
