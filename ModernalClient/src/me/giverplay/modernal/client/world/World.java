package me.giverplay.modernal.client.world;

import static me.giverplay.modernal.client.graphics.SpriteManager.TILE_SIZE;

import java.awt.Graphics;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import me.giverplay.modernal.client.Game;
import me.giverplay.modernal.client.entity.Entity;
import me.giverplay.modernal.client.entity.HumanEntity;
import me.giverplay.modernal.client.utils.Log;
import me.giverplay.modernal.client.world.tile.GrassTile;

public class World
{
	private HashMap<String, HumanEntity> humans  = new HashMap<>();
	private HashMap<String, Entity> entities = new HashMap<>();
	
	private Tile[] tiles;
	
	private Game game;
	
	private int width;
	private int height;
	
	public World(final JSONObject world)
	{
		width = world.getInt("width");
		height = world.getInt("height");
		
		tiles = new Tile[width * height];
		load(world.getJSONObject("tiles"));
		game = Game.getGame();
		
		try
		{
			spreadPlayers(world.getJSONObject("players"));
		}
		catch(JSONException e)
		{
			
		}
	}
	
	private void load(JSONObject obj)
	{
		boolean error = false;
		
		for(int xx = 0; xx < width; xx++)
			for(int yy = 0; yy < height; yy++)
			{
					tiles[xx + yy * width] = new GrassTile(xx * TILE_SIZE, yy * TILE_SIZE);
			}
		
		for(String key : obj.keySet())
		{
			String tile = obj.getString(key);
			String[] data = key.split(":");
			Tile resp;
			
			try
			{
				int x = Integer.parseInt(data[0]);
				int y = Integer.parseInt(data[1]);
				
				if((resp = Tile.getTileByName(tile, x * TILE_SIZE, y * TILE_SIZE)) == null) error = true;
				
				tiles[x + y * width] = resp;
			}
			catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
			{
				error = true;
				Log.severe("Falha ao carregar o mapa, JSON mal formada: " + key);
			}
			finally
			{
				if(error) System.exit(-1);
			}
		}
	}
	
	public void spreadEntities(JSONObject json)
	{
		// TODO
	}
	
	public void spreadPlayers(JSONObject obj)
	{
		for(String key : obj.keySet()) // KEY == NICK
		{
			JSONObject player = obj.getJSONObject(key);
			HumanEntity human = new HumanEntity(key, true, player.getInt("x"), player.getInt("y"), 10, 12, 10, 20);
			humans.put(key, human);
		}
	}
	
	public void render(Graphics g)
	{
		int xs = game.getCamera().getX() / 32;
		int ys = game.getCamera().getY() / 32;
		int xf = xs + (Game.WIDTH * Game.SCALE / 32);
		int yf = ys + (Game.HEIGHT * Game.SCALE /32);
		
		for(int xx = xs; xx <= xf; xx++)
		{
			for(int yy = ys; yy <= yf; yy++)
			{
				
				if(xx < 0 || yy < 0 || xx >= width || yy >= height)
					continue;
				
				tiles[xx + (yy * width)].render(g);
			}
		}
		
		for(HumanEntity human : humans.values())
		{
			human.render(g);
		}
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	public int getHeight()
	{
		return this.height;
	}

	public Tile[] getTiles()
	{
		return this.tiles;
	}
	
	public void addEntity(JSONObject json)
	{
		
	}
	
	public void updateEntity(JSONObject json)
	{
		String updateType = json.getString("update_type");
		Entity ent = entities.get(json.getString("uuid"));
		
		if(updateType.equals("MOVE"))
		{
			ent.moveX(json.getInt("x"));
			ent.moveY(json.getInt("y"));
		}
		
		if(updateType.equals("REMOVE"))
		{
			entities.remove(json.getString("uuid"));
		}
	}
	
	public void addPlayer(JSONObject json)
	{
		String nick = json.getString("nickname");
		
		HumanEntity human = new HumanEntity(nick, true, json.getInt("x"), json.getInt("y"), 10, 10, 10, 10);
		//human.setMaxLife(json.getInt("max_life"));
		//human.setLife(json.getInt("life"));
		
		humans.put(nick, human);
	}
	
	public void removePlayer(JSONObject json)
	{
		humans.remove(json.getString("nickname"));
	}
	
	public void updatePlayer(JSONObject json)
	{
		String updateType = json.getString("update_type");
		HumanEntity ent = humans.get(json.getString("nickname"));
		
		if(updateType.equals("PLAYER_MOVE"))
		{
			ent.moveRelative(json.getInt("x"), json.getInt("y"));
		}
		
		else if(updateType.equals("JUMP"))
		{
			ent.jump();
		}
		
		else if(updateType.equals("STATUS"))
		{
			ent.ModifyLife(json.getInt("life"));
			ent.setMaxLife(json.getInt("max_life"));
		}
	}
}
