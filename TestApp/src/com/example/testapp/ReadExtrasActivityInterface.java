package com.example.testapp;

import java.util.HashMap;
import java.util.List;

public interface ReadExtrasActivityInterface {

	public void dataFetched(HashMap<String, List<HashMap<String, Object>>> many2DataLists, List<HashMap<String,Object>> listBinary);
    
}
