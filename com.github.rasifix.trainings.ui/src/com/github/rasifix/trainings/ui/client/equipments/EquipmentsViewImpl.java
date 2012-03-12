package com.github.rasifix.trainings.ui.client.equipments;

import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.github.rasifix.trainings.ui.client.ViewRow;
import com.github.rasifix.trainings.ui.client.ViewRowCell;
import com.github.rasifix.trainings.ui.client.activities.ActivityOverview;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EquipmentsViewImpl extends Composite implements EquipmentsView {

	private EquipmentsPresenter presenter;
	
	private CellTable<Equipment> table;

	public EquipmentsViewImpl() {
		table = new CellTable<Equipment>();
		
		table.addColumn(new Column<Equipment, String>(new TextCell()) {
			@Override
			public String getValue(Equipment equipment) {
				return equipment.getName();
			}
		});
		
		table.addColumn(new Column<Equipment, String>(new TextCell()) {
			@Override
			public String getValue(Equipment equipment) {
				return equipment.getBrand();
			}
		});
				
		table.addColumn(new Column<Equipment, String>(new TextCell()) {
			@Override
			public String getValue(Equipment equipment) {
				return equipment.dateOfPurchase();
			}
		});
		
		initWidget(table);
	}
	
	@Override
	public void setPresenter(EquipmentsPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setEquipments(ViewResult<Equipment> result) {
		table.setRowData(toList(result.getRows()));
	}

	private List<? extends Equipment> toList(JsArray<ViewRow<Equipment>> rows) {
		List<Equipment> result = new LinkedList<Equipment>();
		for (int i = 0; i < rows.length(); i++) {
			result.add(rows.get(i).getValue());
		}
		return result;
	}

}
