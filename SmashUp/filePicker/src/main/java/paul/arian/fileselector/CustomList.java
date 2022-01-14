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
import java.text.*;
import android.graphics.drawable.*;
import android.graphics.*;

public class CustomList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] web;
    String ParentFolder;
	ImageView imageView;
    public CustomList(Activity context, String[] web,String path) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        ParentFolder = path;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		TextView textSize= (TextView) rowView.findViewById(R.id.txt2);
        imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);
        File f= new File(ParentFolder+"/"+web[position]);
		if(f.isFile()){
			setImage(R.drawable.ic_file);
			textSize.setText(android.text.format.Formatter.formatFileSize(context, f.length()));
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
