package cf.catworlds.streamcraft.core.setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cf.catworlds.streamcraft.core.bungee.BungeeConfigUnit;
import cf.catworlds.streamcraft.core.spigot.SpigotConfigUnit;
import cf.catworlds.streamcraft.core.text.BasicText;
import cf.catworlds.streamcraft.core.text.TextHelper;

public abstract class SettingLoader {

	// static public void loadSetting(ConfigurationUnit config, Logger logger,
	// Class<? extends SettingLoader> clazz) {
	// // set default value
	//
	// // save default then load
	// default_load(config, logger, clazz);
	// // make value translate
	// }
	//
	// static public void loadSetting(ConfigurationUnit config, Plugin plugin,
	// Class<? extends SettingLoader> clazz) {
	// loadSetting(config, plugin.getProxy().getLogger(), clazz);
	// }

	/**
	 * The {@link #default_load(ConfigurationUnit, Logger, Class)
	 * default_load()} will automatically call this method before read config.
	 */
	abstract protected void default_setting();

	/**
	 * The {@link #default_load(ConfigurationUnit, Logger, Class)
	 * default_load()} will automatically call this method after load config.
	 */
	abstract protected void setting_postload();

	/**
	 * Make sure {@link BasicText} is loaded.
	 * 
	 * @param config
	 *            use wrapper like {@link SpigotConfigUnit} or
	 *            {@link BungeeConfigUnit}
	 * @param logger
	 * @param clazz
	 * @return error messages
	 */
	protected static List<String> default_load(ConfigurationUnit config, Logger logger,
			Class<? extends SettingLoader> clazz) {
		SettingLoader instance = null;
		List<String> errMsgs = new ArrayList<>();

		try {
			instance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}

		if (instance == null)
			return errMsgs;
		// set default
		instance.default_setting();

		// load
		for (final Field field : clazz.getDeclaredFields()) {
			final SettingInfo info = field.getAnnotation(SettingInfo.class);
			if (info == null)
				continue;
			// if not static, do nothing
			if ((field.getModifiers() & Modifier.STATIC) == 0)
				continue;
			field.setAccessible(true);
			String path = info.path();
			config.addDefault(path, getValue(field));
			try {
				field.set(null, config.get(path));
			} catch (IllegalArgumentException e) {
				final String err = TextHelper.format(BasicText.ConfigLoadError, path, config.get(path),
						field.getType().getName(), getValue(field));
				errMsgs.add(err);
				if (logger != null)
					logger.warning(err);
			} catch (Throwable ignore) {
			}
		}

		// post load
		instance.setting_postload();
		return errMsgs;
	}

	private static Object getValue(Field field) {
		try {
			return field.get(null);
		} catch (Throwable ignore) {
		}
		// TODO read fail message
		return null;
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SettingInfo {
		String path();
	}
}
