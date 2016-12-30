package com.cytmxk.customview.slidingmenu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.cytmxk.customview.R;
import com.cytmxk.customview.divider.RecycleViewDivider;

public class SlidingMenu extends PopupWindow {

	private RecyclerView popupMenuRV = null;
	private Context context;
	private MenuBuilder menuBuilder;

	public SlidingMenu(Context context, int menuResId, int animationStyle) {
		super(context);
		this.context = context;

		setMenu(menuResId);

		View slidingMenu = LayoutInflater.from(context).inflate(R.layout.sliding_menu, null);
		popupMenuRV = (RecyclerView) slidingMenu.findViewById(R.id.lv_popup_menu);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
		popupMenuRV.setLayoutManager(linearLayoutManager);
		popupMenuRV.addItemDecoration(new RecycleViewDivider(context,
				RecycleViewDivider.LayoutManagerType.LINEAR, RecycleViewDivider.OrientationType.VERTICAL));
		MyAdapter adapter = new MyAdapter();
		popupMenuRV.setAdapter(adapter);

		// 设置SlidingMenu的View
		this.setContentView(slidingMenu);
		// 设置SlidingMenu弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SlidingMenu弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SlidingMenu弹出窗体可点击
		this.setFocusable(true);
		// 设置SlidingMenu弹出窗体动画效果
		this.setAnimationStyle(animationStyle);

		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xffffffff);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		this.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub

			}
		});
		popupMenuRV.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = popupMenuRV.findViewById(R.id.lv_popup_menu)
						.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return false;
			}
		});

	}

	private void setMenu(int menuResId) {
		menuBuilder = new MenuBuilder(this.context);
		MenuInflater menuInflater = new MenuInflater(this.context);
		menuInflater.inflate(menuResId, menuBuilder);
	}

	private class MyAdapter extends RecyclerView.Adapter {

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slidingmenu_item, parent, false));
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			if (holder instanceof MyViewHolder) {
				((MyViewHolder)holder).updateView(menuBuilder.getItem(position));
			}
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != onItemClickListener) {
						onItemClickListener.onItemClick(v.getId());
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return menuBuilder.size();
		}
	}

	private class MyViewHolder extends RecyclerView.ViewHolder {

		private ImageView icon;
		private TextView title;

		MyViewHolder(View itemView) {
			super(itemView);
			icon = (ImageView) itemView.findViewById(R.id.imageview_icon);
			title = (TextView) itemView.findViewById(R.id.textview_title);
		}

		void updateView(MenuItem menuItem) {
			icon.setImageDrawable(menuItem.getIcon());
			title.setText(menuItem.getTitle());
			itemView.setId(menuItem.getItemId());
		}
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	private OnItemClickListener onItemClickListener;
	public interface OnItemClickListener {
		void onItemClick(int menuItemId);
	}

}
