package br.com.pokettaxi.taxista.utils;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


public class ImagensOverlay extends ItemizedOverlay<OverlayItem>{
	private ArrayList<OverlayItem> imagens = new ArrayList<OverlayItem>();	
	private Context context;
	
	public ImagensOverlay(Drawable drawable, Context context) {
		  super(boundCenterBottom(drawable));
		  this.context = context;	
	}
	
	public void addOverlay(OverlayItem overlay) {
	    imagens.add(overlay);
		// O populate() dispara os eventos e chama cada createItem(i)
	    populate();
	}
	
	@Override
	public int size() {
		return imagens.size();
	}
	
	@Override
	protected OverlayItem createItem(int index) {
		return imagens.get(index);
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem imagemClicada = imagens.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(imagemClicada.getTitle());
		dialog.setMessage(imagemClicada.getSnippet());
		dialog.show();
		return true;
	}
	
}