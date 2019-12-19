package io.github.bananapuncher714.cartographer.core.implementation.v1_15_R1;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursor.Type;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import io.github.bananapuncher714.cartographer.core.api.GeneralUtil;
import io.github.bananapuncher714.cartographer.core.api.PacketHandler;
import io.github.bananapuncher714.cartographer.core.internal.Util_1_13;
import io.github.bananapuncher714.cartographer.core.util.MapUtil;
import io.github.bananapuncher714.cartographer.tinyprotocol.TinyProtocol;
import io.netty.channel.Channel;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.MapIcon;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;

public class NMSHandler implements PacketHandler {
	private static Field[] MAP_FIELDS = new Field[ 10 ];
	private static Map< MapCursor.Type, MapIcon.Type > CURSOR_TYPES = new EnumMap< MapCursor.Type, MapIcon.Type >( MapCursor.Type.class );
	
	static {
		try {
			MAP_FIELDS[ 0 ] = PacketPlayOutMap.class.getDeclaredField( "a" );
			MAP_FIELDS[ 1 ] = PacketPlayOutMap.class.getDeclaredField( "b" );
			MAP_FIELDS[ 2 ] = PacketPlayOutMap.class.getDeclaredField( "c" );
			MAP_FIELDS[ 3 ] = PacketPlayOutMap.class.getDeclaredField( "d" );
			MAP_FIELDS[ 4 ] = PacketPlayOutMap.class.getDeclaredField( "e" );
			MAP_FIELDS[ 5 ] = PacketPlayOutMap.class.getDeclaredField( "f" );
			MAP_FIELDS[ 6 ] = PacketPlayOutMap.class.getDeclaredField( "g" );
			MAP_FIELDS[ 7 ] = PacketPlayOutMap.class.getDeclaredField( "h" );
			MAP_FIELDS[ 8 ] = PacketPlayOutMap.class.getDeclaredField( "i" );
			MAP_FIELDS[ 9 ] = PacketPlayOutMap.class.getDeclaredField( "j" );

			for ( Field field : MAP_FIELDS ) {
				field.setAccessible( true );
			}
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		
		CURSOR_TYPES.put( MapCursor.Type.WHITE_POINTER, MapIcon.Type.PLAYER );
		CURSOR_TYPES.put( MapCursor.Type.GREEN_POINTER, MapIcon.Type.FRAME );
		CURSOR_TYPES.put( MapCursor.Type.RED_POINTER, MapIcon.Type.PLAYER_OFF_LIMITS );
		CURSOR_TYPES.put( MapCursor.Type.BLUE_POINTER, MapIcon.Type.BLUE_MARKER );
		CURSOR_TYPES.put( MapCursor.Type.WHITE_CROSS, MapIcon.Type.TARGET_X );
		CURSOR_TYPES.put( MapCursor.Type.RED_MARKER, MapIcon.Type.RED_MARKER );
		CURSOR_TYPES.put( MapCursor.Type.WHITE_CIRCLE, MapIcon.Type.PLAYER );
		CURSOR_TYPES.put( MapCursor.Type.SMALL_WHITE_CIRCLE, MapIcon.Type.PLAYER_OFF_MAP );
		CURSOR_TYPES.put( MapCursor.Type.MANSION, MapIcon.Type.MANSION );
		CURSOR_TYPES.put( MapCursor.Type.TEMPLE, MapIcon.Type.MONUMENT );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_WHITE, MapIcon.Type.BANNER_WHITE );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_ORANGE, MapIcon.Type.BANNER_ORANGE );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_MAGENTA, MapIcon.Type.BANNER_MAGENTA );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_LIGHT_BLUE, MapIcon.Type.BANNER_LIGHT_BLUE );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_YELLOW, MapIcon.Type.BANNER_YELLOW );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_LIME, MapIcon.Type.BANNER_LIME );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_PINK, MapIcon.Type.BANNER_PINK );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_GRAY, MapIcon.Type.BANNER_GRAY );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_LIGHT_GRAY, MapIcon.Type.BANNER_LIGHT_GRAY );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_CYAN, MapIcon.Type.BANNER_CYAN );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_PURPLE, MapIcon.Type.BANNER_PURPLE );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_BLUE, MapIcon.Type.BANNER_BLUE );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_BROWN, MapIcon.Type.BANNER_BROWN );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_GREEN, MapIcon.Type.BANNER_GREEN );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_RED, MapIcon.Type.BANNER_RED );
		CURSOR_TYPES.put( MapCursor.Type.BANNER_BLACK, MapIcon.Type.BANNER_BLACK );
		CURSOR_TYPES.put( MapCursor.Type.RED_X, MapIcon.Type.RED_X );
	}

	private final Set< Integer > maps = new TreeSet< Integer >();
	private Util_1_13 util = new Util_1_13();
	
	@Override
	public void sendDataTo( int id, byte[] data, @Nullable MapCursor[] cursors, UUID... uuids ) {
		MapIcon[] icons;
		if ( cursors == null ) {
			icons = new MapIcon[ 0 ];
		} else {
			icons = new MapIcon[ cursors.length ];
			
			for ( int index = 0; index < cursors.length; index++ ) {
				MapCursor cursor = cursors[ index ];
				
				icons[ index ] = new MapIcon( CURSOR_TYPES.get( cursor.getType() ), cursor.getX(), cursor.getY(), cursor.getDirection(), cursor.getCaption() != null ? new ChatComponentText( cursor.getCaption() ) : null );
			}
		}
		
		PacketPlayOutMap packet = new PacketPlayOutMap();
		
		try {
			MAP_FIELDS[ 0 ].set( packet, id );
			MAP_FIELDS[ 1 ].set( packet, ( byte ) 0 );
			MAP_FIELDS[ 2 ].set( packet, false );
			MAP_FIELDS[ 3 ].set( packet, false );
			MAP_FIELDS[ 4 ].set( packet, icons );
			MAP_FIELDS[ 5 ].set( packet, 0 );
			MAP_FIELDS[ 6 ].set( packet, 0 );
			MAP_FIELDS[ 7 ].set( packet, 128 );
			MAP_FIELDS[ 8 ].set( packet, 128 );
			MAP_FIELDS[ 9 ].set( packet, data );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		
		PacketPlayOutMinimap mapPacket = new PacketPlayOutMinimap( packet );
		
		TinyProtocol protocol = Cartographer.getInstance().getProtocol();
		for ( UUID uuid : uuids ) {
			if ( uuid != null ) {
				Channel channel = protocol.getChannel( uuid, null );
				if ( channel != null ) {
					protocol.sendPacket( channel, mapPacket );
				}
			}
		}
	}

	@Override
	public Object onPacketInterceptOut( Player viewer, Object packet ) {
		if ( packet instanceof PacketPlayOutMinimap ) {
			return ( ( PacketPlayOutMinimap ) packet ).packet;
		} else if ( packet instanceof PacketPlayOutMap ) {
			if ( packet.getClass().equals( PacketPlayOutMap.class ) ) {
				try {
					int id = MAP_FIELDS[ 0 ].getInt( packet );
					if ( maps.contains( id ) ) {
						return null;
					}
				} catch ( IllegalArgumentException | IllegalAccessException e ) {
					e.printStackTrace();
				}
			}
		}
		return packet;
	}
	
	@Override
	public Object onPacketInterceptIn( Player viewer, Object packet ) {
		return packet;
	}
	
	@Override
	public boolean isMapRegistered( int id ) {
		return maps.contains( id );
	}
	
	@Override
	public void registerMap( int id ) {
		maps.add( id );
	}
	
	@Override
	public void unregisterMap( int id ) {
		maps.remove( id );
	}
	
	@Override
	public MapCursor constructMapCursor( int x, int y, double yaw, Type cursorType, String name ) {
		return new MapCursor( ( byte ) x, ( byte ) y, MapUtil.getDirection( yaw ), cursorType, true, name );
	}
	
	@Override
	public double getTPS() {
		return MinecraftServer.getServer().recentTps[ 0 ];
	}

	@Override
	public GeneralUtil getUtil() {
		return util;
	}
	
	private class PacketPlayOutMinimap extends PacketPlayOutMap {
		protected final PacketPlayOutMap packet;
		
		protected PacketPlayOutMinimap( PacketPlayOutMap packet ) {
			this.packet = packet;
		}
	}
}
