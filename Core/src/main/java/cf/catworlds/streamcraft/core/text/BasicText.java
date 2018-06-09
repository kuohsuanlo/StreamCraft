package cf.catworlds.streamcraft.core.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BasicText {
	NoPermission("&cYou don't have permission."),
	NotPlayer("&cYou must be a player."),
	/** "Path", "ConfigValue", "ValueType", "DefaultValue" **/
	ConfigLoadError(new String[] { "Path", "ConfigValue", "ValueType", "DefaultValue" }, "&cConfig load fail [&e${Path} &c= &e${ConfigValue,Null}&c] : Cannot cast to type &b${ValueType}&c, use default &e${DefaultValue,Null}");
	final List<String> defaultTexts;
	final List<String> formatKeys;

	private BasicText(String... def) {
		this(new String[0], def);
	}

	private BasicText(String[] keys, String... def) {
		this.defaultTexts = Collections.unmodifiableList(Arrays.asList(def));
		this.formatKeys = Collections.unmodifiableList(Arrays.asList(keys));
	}

	static public final String PREFIX = "Basic";
}
