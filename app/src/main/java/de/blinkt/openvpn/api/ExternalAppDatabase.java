/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class ExternalAppDatabase {

	Context mContext;
	
	public ExternalAppDatabase(Context c) {
		mContext =c;
	}

	private final String PREFERENCES_KEY = "PREFERENCES_KEY";

	boolean isAllowed(String packagename) {
		Set<String> allowedapps = getExtAppList();

		return allowedapps.contains(packagename); 

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Set<String> getExtAppList() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getStringSet(PREFERENCES_KEY, new HashSet<String>());
	}
	
	void addApp(String packagename)
	{
		Set<String> allowedapps = getExtAppList();
		allowedapps.add(packagename);
		saveExtAppList(allowedapps);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void saveExtAppList( Set<String> allowedapps) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);        
		Editor prefedit = prefs.edit();
		prefedit.putStringSet(PREFERENCES_KEY, allowedapps);
		prefedit.apply();
	}
	
	public void clearAllApiApps() {
		saveExtAppList(new HashSet<String>());
	}

	public void removeApp(String packagename) {
		Set<String> allowedapps = getExtAppList();
		allowedapps.remove(packagename);
		saveExtAppList(allowedapps);		
	}

}
