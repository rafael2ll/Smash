package paul.arian.fileselector;

/**
 * Created by Paul on 3/7/14.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import android.graphics.*;
import android.graphics.drawable.*;

public class CustomListSingleOnly extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] web;
	ImageView imageView;
    String ParentFolder;
    public CustomListSingleOnly(Activity context, String[] web ,String path) {
        super(context, R.layout.list_single_only, web);
        this.context = context;
        this.web = web;
            ParentFolder = path;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_only, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);
		txtTitle.setTextAppearance(context,R.style.TextAppearance_AppCompat_Body1);
		File f= new File(ParentFolder+"/"+web[position]);
		if(f.isFile()){
			setImage(R.drawable.ic_file);
		}else{
			setImage(R.drawable.ic_folder);
		}
        return rowView;
    }
	private void setImage(int res)
	{
		Drawable d = context.getResources().getDrawable(res);
		d.setColorFilter( context.getResources().getColor(R.color.red_a700), PorterDuff.Mode.MULTIPLY);
		imageView.setImageDrawable(d);
	}
}

