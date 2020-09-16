package com.example.whatsapp_facebook_videosaver;

/**
 * Created by Tushar on 9/10/2017.
 */

class Methods {
    public void setColorTheme(){

        switch (Constant.color){
            case 0xffF44336:
                Constant.theme = R.style.AppTheme_red;
                break;
            case 0xffE91E63:
                Constant.theme = R.style.AppTheme_pink;
                break;
            case 0xff9C27B0:
                Constant.theme = R.style.AppTheme_darpink;
                break;
            case 0xff673AB7:
                Constant.theme = R.style.AppTheme_violet;
                break;
            case 0xff3F51B5:
                Constant.theme = R.style.AppTheme_blue;
                break;
            case 0xff03A9F4:
                Constant.theme = R.style.AppTheme_skyblue;
                break;
            case 0xff4CAF50:
                Constant.theme = R.style.AppTheme_green;
                break;
            case 0xffFF9800:
                Constant.theme = R.style.AppTheme_orange;
                break;
            case 0xff9E9E9E:
                Constant.theme = R.style.AppTheme_grey;
                break;

            case 0xff795548:
                Constant.theme = R.style.AppTheme_brown;
                break;
            case 0xff2196F3:
                Constant.theme = R.style.AppTheme_bluee;
                break;
            case 0xff00BCD4:
                Constant.theme = R.style.AppTheme_cyan;
                break;
            case 0xff009688:
                Constant.theme = R.style.AppTheme_teal;
                break;
            case 0xff8BC34A:
                Constant.theme = R.style.AppTheme_lgreen;
                break;
            case 0xffCDDC39:
                Constant.theme = R.style.AppTheme_lime;
                break;
            case 0xffFFEB3B:
                Constant.theme = R.style.AppTheme_yellow;
                break;
            case 0xffFFC107:
                Constant.theme = R.style.AppTheme_amber;
                break;
            case 0xffFF5722:
                Constant.theme = R.style.AppTheme_dorange;
                break;
            case 0xff000000:
                Constant.theme = R.style.AppTheme_black;
                break;
            case 0xff607D8B:
                Constant.theme = R.style.AppTheme_gray;
                break;
            default:
                Constant.theme = R.style.AppTheme;
                break;
        }
    }
}
