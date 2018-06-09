package cf.catworlds.streamcraft.core.text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cf.catworlds.streamcraft.core.setting.ConfigurationUnit;
import net.md_5.bungee.api.ChatColor;

public class TextHelper {
	static private Map<Object, TextFormater> text_map = new HashMap<Object, TextFormater>();

	static public <E extends Enum<E>> String format(E text, Object... args) {
		TextFormater formater = text_map.get(text);
		try {
			if (formater == null)
				throw new Throwable("The TextEnum \"" + text.getClass().getName() + "\" is not loaded");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return text_map.get(text).format(args);
	}

	static public void clear() {
		text_map.clear();
	}

	/**
	 * Always add this section in your textEnum.
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * final List{@code <String>} defaultTexts;
	 * final List{@code <String>} formatKeys;
	 * 
	 * private TextEnum(String... def) {
	 *     this(new String[0], def);
	 * }
	 * private TextEnum(String[] keys,String... def) {
	 *     this.defaultTexts = Collections.unmodifiableList(Arrays.asList(def));
	 *     this.formatKeys = Collections.unmodifiableList(Arrays.asList(keys));
	 * }
	 * 
	 * static public final String PREFIX; // Optional
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * <h4><b>Don't forget init the BasicText. (if need)</b></h4>
	 * 
	 * @param config
	 * @param textEnum
	 * @param TEXT_PREFIX
	 *            if empty or null, the config path will be <b>"TextName"</b>
	 *            ,else <b>"TEXT_PREFIX.TextName"</b>
	 */
	static public <E extends Enum<E>> void init(ConfigurationUnit config, final Class<E> textEnum, String TEXT_PREFIX) {
		Field field_defaultTexts = null;
		Field field_formatKeys = null;
		try {
			field_defaultTexts = textEnum.getDeclaredField("defaultTexts");
			field_formatKeys = textEnum.getDeclaredField("formatKeys");
		} catch (NoSuchFieldException | SecurityException e) {
			System.out.println("The enum " + textEnum.getName() + " has no \"defaultTexts\" or \"formatKeys\" field");
			e.printStackTrace();
		}

		if (TEXT_PREFIX == null || TEXT_PREFIX.isEmpty())
			TEXT_PREFIX = "";
		else
			TEXT_PREFIX += ".";
		for (E text : EnumSet.allOf(textEnum)) {
			final String textPATH = TEXT_PREFIX + text.name();
			if (!config.isList(textPATH))
				config.set(textPATH, getStringList(field_defaultTexts, text));
			List<String> read = config.getStringList(textPATH);
			read.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
			TextFormater formater = new TextFormater(read, getStringList(field_formatKeys, text));
			text_map.put(text, formater);
		}
	}

	static public void init_BasicText(ConfigurationUnit config, final String prefix) {
		init(config, BasicText.class, prefix);
	}

	static public void init_BasicText(ConfigurationUnit config) {
		init_BasicText(config, BasicText.PREFIX);
	}

	@SuppressWarnings("unchecked")
	static private <E> List<String> getStringList(Field field, E text) {
		try {
			return new ArrayList<>((List<String>) field.get(text));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Throwable e) {
		}
		return Collections.EMPTY_LIST;
	}
}
