package com.openerp;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;

public class FieldsGetAndM2PopulateAT extends FieldsGetAsyncTask {
	private HashMap<String, List<HashMap<String, Object>>> M2data;

	public FieldsGetAndM2PopulateAT(Activity act, String[] fields2) {
		super(act, fields2);
		M2data = null;
	}

	@Override
	protected OpenErpConnect doInBackground(String... params) {
		this.M2data = new HashMap<String, List<HashMap<String, Object>>>();
		this.oc = super.doInBackground(params);
		String[] retFields = { "id", "name" };
		if (this.data != null) {
			for (String fieldname : this.fields) {
				String type = getFieldType(fieldname);
				if (type.equals("many2one") || type.equals("many2many")) {
					String rel = getFieldRelation(fieldname);
					Long[] ids = this.oc.search(rel, new Object[0]);
					if (ids != null) {
						List<HashMap<String, Object>> listData = oc.read(rel,
								ids, retFields);
						M2data.put(fieldname, listData);
					}
				}
			}
		}
		return this.oc;
	}

	private String getFieldRelation(String fieldname) {
		String relation = null;
		if (this.data != null) {
			HashMap<String, Object> fieldAttr = (HashMap<String, Object>) data
					.get(fieldname);
			relation = (String) fieldAttr.get("relation");
		}
		return relation;
	}

	public String getFieldType(String fieldname) {
		String type = null;
		if (this.data != null) {
			HashMap<String, Object> fieldAttr = (HashMap<String, Object>) data
					.get(fieldname);
			type = (String) fieldAttr.get("type");
		}
		return type;
	}

	public List<HashMap<String, Object>> getList(String fieldname) {
		return M2data.get(fieldname);
	}

}
