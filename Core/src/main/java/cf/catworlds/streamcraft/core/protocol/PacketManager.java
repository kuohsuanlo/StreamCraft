package cf.catworlds.streamcraft.core.protocol;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.NonNull;

public class PacketManager {

	private static Map<Object, Set<PacketListener>> plugin_Listeners = new HashMap<>();
	private static Map<Class<? extends AbstractPacket>, EnumMap<HandlePriority, Map<Method, Set<PacketListener>>>> packetHandlers = new HashMap<>();

	static public void registerListener(@NonNull final PacketListener listener, @NonNull final Object plugin,
			@NonNull final Logger logger) {
		if (!registerListener(listener, logger))
			return;
		if (!plugin_Listeners.containsKey(plugin))
			plugin_Listeners.put(plugin, new HashSet<>());
		plugin_Listeners.get(plugin).add(listener);
	}

	/**
	 * 
	 * @param listener
	 * @param logger
	 * @return true if any method is registered
	 */
	static private boolean registerListener(@NonNull final PacketListener listener, @NonNull final Logger logger) {
		Set<Method> methods;
		try {
			Method[] publicMethods = listener.getClass().getMethods();
			Method[] privateMethods = listener.getClass().getDeclaredMethods();
			methods = new HashSet<Method>(publicMethods.length + privateMethods.length, 1.0f);
			for (Method method : publicMethods) {
				methods.add(method);
			}
			for (Method method : privateMethods) {
				methods.add(method);
			}
		} catch (NoClassDefFoundError e) {
			logger.severe("Failed to register packet handlers for " + listener.getClass() + " because " + e.getMessage()
					+ " does not exist.");
			return false;
		}

		boolean registered = false;
		for (Method method : methods) {
			// check if packet handler method
			if (method.isBridge() || method.isSynthetic())
				continue;

			final PacketHandler ph = method.getAnnotation(PacketHandler.class);
			if (ph == null)
				continue;

			// check handle packet type
			final Class<?> packetType = method.getParameterTypes()[0];
			if (method.getParameterTypes().length != 1 || !AbstractPacket.class.isAssignableFrom(packetType)) {
				logger.severe("Register an invalid EventHandler method signature \"" + method.toGenericString()
						+ "\" in " + listener.getClass());
				continue;
			}

			// Add to handlers map
			method.setAccessible(true);
			final HandlePriority priority = ph.priority();

			if (!packetHandlers.containsKey(packetType))
				packetHandlers.put(packetType.asSubclass(AbstractPacket.class), new EnumMap<>(HandlePriority.class));
			EnumMap<HandlePriority, Map<Method, Set<PacketListener>>> priorityMap = packetHandlers.get(packetType);

			if (!priorityMap.containsKey(priority))
				priorityMap.put(priority, new HashMap<>());
			Map<Method, Set<PacketListener>> methodMap = priorityMap.get(priority);

			if (!methodMap.containsKey(method))
				methodMap.put(method, new HashSet<>());
			Set<PacketListener> packetListenersSet = methodMap.get(method);

			packetListenersSet.add(listener);
			registered = true;
		}
		return registered;
	}

	static public void unregisterAllListener(@NonNull final Object plugin, @NonNull final Logger logger) {
		if (plugin_Listeners.containsKey(plugin))
			for (PacketListener pl : plugin_Listeners.get(plugin))
				unregisterListener(pl, logger);
	}

	static public void unregisterListener(@NonNull PacketListener listener, @NonNull final Logger logger) {
		Set<Method> methods;
		try {
			Method[] publicMethods = listener.getClass().getMethods();
			Method[] privateMethods = listener.getClass().getDeclaredMethods();
			methods = new HashSet<Method>(publicMethods.length + privateMethods.length, 1.0f);
			for (Method method : publicMethods) {
				methods.add(method);
			}
			for (Method method : privateMethods) {
				methods.add(method);
			}
		} catch (NoClassDefFoundError e) {
			logger.severe("Failed to register packet handlers for " + listener.getClass() + " because " + e.getMessage()
					+ " does not exist.");
			return;
		}

		for (Method method : methods) {
			// check if packet handler method
			if (method.isBridge() || method.isSynthetic())
				continue;

			final PacketHandler ph = method.getAnnotation(PacketHandler.class);
			if (ph == null)
				continue;

			// check handle packet type
			final Class<?> packetType = method.getParameterTypes()[0];
			if (method.getParameterTypes().length != 1 || !AbstractPacket.class.isAssignableFrom(packetType)) {
				continue;
			}

			// Add to handlers map
			method.setAccessible(true);
			final HandlePriority priority = ph.priority();

			if (!packetHandlers.containsKey(packetType))
				continue;
			EnumMap<HandlePriority, Map<Method, Set<PacketListener>>> priorityMap = packetHandlers.get(packetType);

			if (!priorityMap.containsKey(priority))
				continue;
			Map<Method, Set<PacketListener>> methodMap = priorityMap.get(priority);

			if (!methodMap.containsKey(method))
				continue;
			Set<PacketListener> packetListenersSet = methodMap.get(method);

			packetListenersSet.remove(listener);

			if (packetListenersSet.isEmpty())
				methodMap.remove(method);

			if (methodMap.isEmpty())
				priorityMap.remove(priority);

			if (priorityMap.isEmpty())
				packetHandlers.remove(packetType);
		}
	}

	public static void firePacket(@NonNull AbstractPacket packet, @NonNull Logger logger) {
		Class<? extends AbstractPacket> packetClass = packet.getClass();

		if (!packetHandlers.containsKey(packetClass))
			return;

		EnumMap<HandlePriority, Map<Method, Set<PacketListener>>> handlerMap = packetHandlers.get(packetClass);
		for (Entry<HandlePriority, Map<Method, Set<PacketListener>>> pair : handlerMap.entrySet()) {
			for (Entry<Method, Set<PacketListener>> method_listeners : pair.getValue().entrySet()) {
				final Method method = method_listeners.getKey();
				for (final PacketListener listener : method_listeners.getValue()) {
					try {
						method.invoke(listener, packet);
					} catch (Throwable e) {
						logger.log(Level.SEVERE, "Could not pass packet " + packet.getClass() + " to "
								+ method_listeners.getKey().getName(), e);
					}
				}
			}
		}
	}
}
