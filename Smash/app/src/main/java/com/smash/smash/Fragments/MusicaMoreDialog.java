package com.smash.smash.Fragments;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;
import com.smash.smash.*;
import com.smash.smash.Helpers.*;
import com.smash.smash.Models.*;
import java.io.*;
import java.net.*;

import com.smash.smash.R;

public class MusicaMoreDialog extends DialogFragment
{

	ArrayAdapter<MoreDialogItem> mOptionsAdapter;
	Musica mMusica;

	public MusicaMoreDialog(Musica m)
	{
		this.mMusica = m;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_options, null, false);
		((TextView)view.findViewById(R.id.dialogheaderTextView1)).setText(mMusica.nome);
		((TextView)view.findViewById(R.id.dialogheaderTextView2)).setText(mMusica.albumNome+"  "+mMusica.artistaNome);
		ListView mListView = (ListView)view.findViewById(R.id.dialogoptionsListView);
		mOptionsAdapter = new ArrayAdapter<MoreDialogItem>(getActivity(), R.layout.options_dialog, R.id.optionsdialogTextView,
														   MoreDialogItem.getMusicaList(getActivity(), mMusica)){
			@Override
			public  View getView(int pos, View view, ViewGroup container)
			{
				view = getActivity().getLayoutInflater().inflate(R.layout.options_dialog, container, false);

				TextView mText = (TextView) view.findViewById(R.id.optionsdialogTextView);
				ImageView mImage = (ImageView) view.findViewById(R.id.optionsdialogImageView);

				mText.setText(getItem(pos).text);
				mImage.setImageResource(getItem(pos).imageRes);

				return view;
			}
		};
		mListView.setAdapter(mOptionsAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int position, long p4)
				{
					switch(position){
						case 0:User.containsMusica(mMusica.key, new User.OnContainsListener(){

								@Override
								public void isContained(boolean isInMyMusicas)
								{
									if(isInMyMusicas)User.removeMusica(mMusica.key);
									else
										User.addMusica(mMusica);
									}
							});break;
						case 1:User.setFavorite(mMusica);break;
						case 2:
							if(MusicMasterHandler.isOffline(getActivity(), mMusica)){
								MusicMasterHandler.removeMusicaOffline(getActivity(),mMusica.key);
								dismiss();
								return;
							}
							File f = new File(getActivity().getFilesDir(), mMusica.key);
							if(f.exists() && f.length()>100){
								MusicMasterHandler.storeMusicaOffline(getActivity(), mMusica.key);
								return;
							}
							new DownloadFileFromURL().execute(mMusica);
							break;
								
						case 3: //Add to..
						case 4:
							MusicMasterHandler.addToNext(mMusica);break;
						case 5:Intent i = new Intent(getActivity(), AlbumActivity.class);
							i.putExtra(AlbumActivity.ALBUM_KEY, mMusica.albumKey);
							startActivity(i);break;
						case 6: //Artist
						case 7: //link
						case 8: //QR
					}
					dismiss();
				}
			});
		return view;
	}
	public class DownloadFileFromURL extends AsyncTask<Musica, String, String> {

        /**
         * Before starting background thread
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(Musica... ms) {
            int count;
            try {
                String root = Environment.getExternalStorageDirectory().toString();

                System.out.println("Downloading");
               	URL url = new URL(ms[0].getMusicaUri());

                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                OutputStream output = new FileOutputStream(new File(MyApplication.getContext().getFilesDir(), ms[0].key));
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return ms[0].key;
        }



        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String key) {
            System.out.println("Downloaded");
			Toast.makeText(MyApplication.getContext()," Doneeee", 2).show();
			MusicMasterHandler.storeMusicaOffline(MyApplication.getContext(), key);
        }

    }
}
