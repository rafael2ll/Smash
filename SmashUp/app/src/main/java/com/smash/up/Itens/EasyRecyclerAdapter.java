package com.smash.up.Itens;

import android.support.v7.widget.*;
import android.view.*;
import java.lang.reflect.*;
import java.util.*;

public abstract class EasyRecyclerAdapter <T, VH extends RecyclerView.ViewHolder > extends RecyclerView.Adapter
{

	Class< T> mModelClass;
	List<T> itens=new ArrayList<>();
	protected int mModelLayout;
	Class< VH> mViewHolderClass;

	public EasyRecyclerAdapter(List<T> itens, Class<T> modelClass, int modelLayout , Class< VH> viewHolderClass)
	{
		mModelClass = modelClass;
		mModelLayout = modelLayout;
		mViewHolderClass = viewHolderClass;
		this.itens = itens;
	}


	public void cleanup()
	{
		itens.clear();
	}

	@Override
	public int getItemCount()
	{
		return itens .size();
	}
	public T getItem(int position)
	{
		return itens.get(position);
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent , int viewType)
	{
		ViewGroup view = (ViewGroup) LayoutInflater . from(parent .getContext()) . inflate(viewType, parent,false);
		view.setTag(viewType);
		try
		{
			Constructor< VH > constructor = mViewHolderClass. getConstructor(View .class);
			return constructor . newInstance(view);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
	@Override
	public void onBindViewHolder(VH viewHolder , int position)
	{
		T model = getItem(position);
		populateViewHolder(viewHolder, model, position);
	}
	@Override
	public int getItemViewType(int position)
	{
		return mModelLayout;
	}
	
	abstract protected void populateViewHolder(VH viewHolder , T model , int position);
}
