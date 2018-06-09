package cf.catworlds.streamcraft.core.protocol.packet;

import cf.catworlds.streamcraft.core.protocol.AbstractPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
public class HelloWorldPacket extends AbstractPacket {
	private int times;
	private String name;
}
