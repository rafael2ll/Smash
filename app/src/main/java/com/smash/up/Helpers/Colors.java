package com.smash.up.Helpers;
import android.graphics.*;
@SuppressWarnings("unused")
public enum Colors {

    red_500("#F44336"),
    red_accent_400("#FF1744"),
	
    deep_purple_500("#673AB7"),
    deep_purple_accent_400("#651FFF"),
   
	indigo_300("#7986CB"),
    indigo_400("#5C6BC0"),
    indigo_500("#3F51B5"),
    indigo_600("#3949AB"),
    indigo_accent_200("#536DFE"),
    indigo_accent_400("#3D5AFE"),
    indigo_accent_700("#304FFE"),
    
    blue_400("#42A5F5"),
    blue_500("#2196F3"),
    blue_600("#1E88E5"),
    blue_accent_400("#2979FF"),
    blue_accent_700("#2962FF"),
 
	light_blue_500("#03A9F4"),
    light_blue_600("#039BE5"),
    light_blue_accent_400("#00B0FF"),
    light_blue_accent_700("#0091EA"),
   
    cyan_500("#00BCD4"),
    cyan_600("#00ACC1"),
    cyan_accent_200("#18FFFF"),
    cyan_accent_400("#00E5FF"),
    cyan_accent_700("#00B8D4"),
   
	
	teal_400("#26A69A"),
    teal_500("#009688"),
    teal_600("#00897B"),
    teal_700("#00796B"),
    teal_accent_400("#1DE9B6"),
    teal_accent_700("#00BFA5"),
  
	green_300("#81C784"),
    green_400("#66BB6A"),
    green_500("#4CAF50"),
    green_600("#43A047"),
    green_700("#388E3C"),
    green_accent_200("#69F0AE"),
    green_accent_400("#00E676"),
    green_accent_700("#00C853"),
    
	yellow_accent_400("#FFEA00"),
    yellow_accent_700("#FFD600"),
	
    amber_500("#FFC107"),
    amber_accent_400("#FFC400"),
    amber_accent_700("#FFAB00"),
	
    orange_600("#FB8C00"),
    orange_700("#F57C00"),
    orange_accent_400("#FF9100"),
    orange_accent_700("#FF6D00"),
	
    
	deep_orange_400("#FF7043"),
    deep_orange_500("#FF5722"),
    deep_orange_600("#F4511E"),
    deep_orange_accent_400("#FF3D00"),
    deep_orange_accent_700("#DD2600"),
  
	   brown_400("#8D6E63"),
    brown_500("#795548"),
    brown_600("#6D4C41"),
    brown_700("#5D4037"),
    brown_800("#4E342E"),
	
    grey_300("#E0E0E0"),
    grey_400("#BDBDBD"),
    grey_500("#9E9E9E"),
    grey_600("#757575"),
    grey_700("#616161"),
    grey_800("#424242"),
    grey_900("#212121"),
	
    black_1000("#000000"),
    white_1000("#ffffff"),
	
    blue_grey_500("#607D8B"),
    blue_grey_600("#546E7A"),
    blue_grey_700("#455A64"),
    blue_grey_800("#37474F"),
    blue_grey_900("#263238");

    private final int mColor;
    private final String mStringColor;

    Colors(final String mStringColor) {
        this.mStringColor = mStringColor;
        this.mColor = Color.parseColor(mStringColor);
    }

    public int asColor() {
        return mColor;
    }

    public String asString() {
        return mStringColor;
    }

}
