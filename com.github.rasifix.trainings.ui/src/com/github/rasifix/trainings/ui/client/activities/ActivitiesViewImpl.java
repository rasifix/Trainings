package com.github.rasifix.trainings.ui.client.activities;

import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.github.rasifix.trainings.ui.client.ViewRow;
import com.github.rasifix.trainings.ui.client.ViewRowCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ActivitiesViewImpl extends Composite implements ActivitiesView {

	private CellList<ViewRow<ActivityOverview>> list;
	
	private ActivitiesPresenter presenter;

	public ActivitiesViewImpl() {
		VerticalPanel panel = new VerticalPanel();
		
		// Create a cell to render each value in the list.
		ViewRowCell cell = new ViewRowCell();

		list = new CellList<ViewRow<ActivityOverview>>(cell);
		
		Anchor link = new Anchor("previous");
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.previous();
			}
		});		
		panel.add(link);
		
		link = new Anchor("next");
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.next();
			}
		});		
		panel.add(link);
		
		panel.add(list);
		
		initWidget(panel);
	}
	
	@Override
	public void setPresenter(ActivitiesPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setActivities(ViewResult<ActivityOverview> result) {
		list.setRowData(toList(result.getRows()));
	}

	private List<? extends ViewRow<ActivityOverview>> toList(JsArray<ViewRow<ActivityOverview>> rows) {
		List<ViewRow<ActivityOverview>> result = new LinkedList<ViewRow<ActivityOverview>>();
		for (int i = 0; i < rows.length(); i++) {
			result.add(rows.get(i));
		}
		return result;
	}

}
