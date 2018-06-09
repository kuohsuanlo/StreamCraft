package cf.catworlds.streamcraft.core.protocol;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cf.catworlds.streamcraft.core.protocol.packet.HelloWorldPacket;
import lombok.NonNull;

public enum Protocol {
	HelloPacket(0x01, HelloWorldPacket.class);
	;

	private Protocol(int id, Class<? extends AbstractPacket> packetClass) {
		try {
			final byte b_id = (byte) id;
			PacketData.packetConstructors[b_id] = packetClass.getDeclaredConstructor();

			List<Field> fields = new ArrayList<>();
			for (Field f : packetClass.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0)
					continue;
				if ((f.getModifiers() & Modifier.TRANSIENT) != 0)
					continue;
				if (!f.isAccessible())
					f.setAccessible(true);
				fields.add(f);
			}
			PacketData.packetFields.put(packetClass, fields.stream().toArray(size -> new Field[size]));

			PacketData.packetIDs.put(packetClass, b_id);
		} catch (NoSuchMethodException e) {
			try {
				throw new Throwable("No NoArgsConstructor for packet class " + packetClass);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}

	}

	public static final String ChannelTag = "Template_Project";

	private static class PacketData {
		private static final int MAX_PACKET_ID = 0xFF;
		static private final BiMap<Class<? extends AbstractPacket>, Byte> packetIDs = HashBiMap.create(MAX_PACKET_ID);
		@SuppressWarnings("unchecked")
		static private final Constructor<? extends AbstractPacket>[] packetConstructors = new Constructor[MAX_PACKET_ID];
		static private final Map<Class<? extends AbstractPacket>, Field[]> packetFields = new HashMap<>();
	}

	public static class PacketConverter {
		private static final Map<Class<?>, BiConsumer<?, ByteArrayDataOutput>> writeMap = new HashMap<>();
		private static final Map<Class<?>, BiConsumer<?, ByteArrayDataOutput>> writeMapEx = new HashMap<>();
		private static final Map<Class<?>, Function<ByteArrayDataInput, ?>> readMap = new HashMap<>();
		private static final Map<Class<?>, Function<ByteArrayDataInput, ?>> readMapEx = new HashMap<>();

		static {

			writeMap.put(boolean.class, (val, out) -> out.writeBoolean((boolean) val));
			readMap.put(boolean.class, in -> in.readBoolean());
			writeMap.put(char.class, (val, out) -> out.writeChar((char) val));
			readMap.put(char.class, in -> in.readChar());
			writeMap.put(byte.class, (val, out) -> out.writeByte((byte) val));
			readMap.put(byte.class, in -> in.readByte());
			writeMap.put(short.class, (val, out) -> out.writeShort((short) val));
			readMap.put(short.class, in -> in.readShort());
			writeMap.put(int.class, (val, out) -> out.writeInt((int) val));
			readMap.put(int.class, in -> in.readInt());
			writeMap.put(long.class, (val, out) -> out.writeLong((long) val));
			readMap.put(long.class, in -> in.readLong());
			writeMap.put(float.class, (val, out) -> out.writeFloat((float) val));
			readMap.put(float.class, in -> in.readFloat());
			writeMap.put(double.class, (val, out) -> out.writeDouble((double) val));
			readMap.put(double.class, in -> in.readDouble());
			writeMap.put(String.class, (str, out) -> out.writeUTF(str == null ? "" : (String) str));
			readMap.put(String.class, in -> in.readUTF());

			registerType(UUID.class, (UUID id, ByteArrayDataOutput out) -> {
				out.writeLong(id.getMostSignificantBits());
				out.writeLong(id.getLeastSignificantBits());
			}, in -> new UUID(in.readLong(), in.readLong()));

		}

		public static <T> void registerType(@NonNull Class<T> type, @NonNull BiConsumer<T, ByteArrayDataOutput> write,
				@NonNull Function<ByteArrayDataInput, T> read) {
			writeMapEx.put(type, write);
			readMapEx.put(type, read);
		}

		@SuppressWarnings("unchecked")
		public static <T> T read(@NonNull Class<T> type, @NonNull ByteArrayDataInput dataIn) {
			Function<ByteArrayDataInput, ?> conver = readMap.get(type);
			if (conver != null)
				return (T) conver.apply(dataIn);

			conver = readMapEx.get(type);
			if (conver != null)
				// check is Null?
				return dataIn.readBoolean() ? null : (T) conver.apply(dataIn);

			if (type.isArray()) {
				// check is Null?
				if (dataIn.readBoolean())
					return null;
				short size = dataIn.readShort();
				Class<?> subType = type.getComponentType();
				Object array = Array.newInstance(subType, size);
				for (int i = 0; i < size; i++)
					Array.set(array, i, read(subType, dataIn));
				return (T) array;
			}

			new Throwable("Unknow Type: " + type.getName()).printStackTrace();
			return null;
		}

		@SuppressWarnings("unchecked")
		public static <T> void write(@NonNull Class<T> type, Object value, @NonNull ByteArrayDataOutput dataOut) {
			BiConsumer<Object, ByteArrayDataOutput> conver = (BiConsumer<Object, ByteArrayDataOutput>) writeMap
					.get(type);
			if (conver != null) {
				conver.accept(value, dataOut);
				return;
			}

			conver = (BiConsumer<Object, ByteArrayDataOutput>) writeMapEx.get(type);
			if (conver != null) {
				boolean isNull = value == null;
				dataOut.writeBoolean(isNull);
				if (!isNull)
					conver.accept(value, dataOut);
				return;
			}

			if (type.isArray()) {
				boolean isNull = value == null;
				dataOut.writeBoolean(isNull);
				if (!isNull) {
					Class<?> subType = type.getComponentType();
					short size = (short) Array.getLength(value);
					dataOut.writeShort(size);
					for (int i = 0; i < size; i++)
						write(subType, Array.get(value, i), dataOut);
				}
				return;
			}
			new Throwable("Unknow Type: " + type.getName()).printStackTrace();
		}

	}

	static public byte[] toByte(@NonNull AbstractPacket packet) {
		Preconditions.checkArgument(PacketData.packetIDs.containsKey(packet.getClass()),
				"Cannot get ID for packet " + packet.getClass().getName());
		ByteArrayDataOutput dataOut = ByteStreams.newDataOutput();
		// AbstractPacket packet = null;
		dataOut.writeByte(PacketData.packetIDs.get(packet.getClass()));
		// byte p_id = dataIn.readByte();

		for (Field field : PacketData.packetFields.get(packet.getClass())) {
			try {
				PacketConverter.write(field.getType(), field.get(packet), dataOut);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return dataOut.toByteArray();
	}

	static public AbstractPacket toPacket(byte[] data) {
		ByteArrayDataInput dataIn = ByteStreams.newDataInput(data);
		byte p_id = dataIn.readByte();
		AbstractPacket packet = null;
		try {
			packet = PacketData.packetConstructors[p_id].newInstance();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

		for (Field field : PacketData.packetFields.get(packet.getClass())) {
			try {
				field.set(packet, PacketConverter.read(field.getType(), dataIn));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return packet;
	}
}
