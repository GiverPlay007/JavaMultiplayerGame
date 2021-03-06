package me.giverplay.modernal.server.tasks;

import java.net.Socket;
import java.util.HashMap;

import me.giverplay.modernal.server.objects.RandomUtils;
import me.giverplay.modernal.server.objects.ServerLogger;

public class TaskManager
{
	private HashMap<String, AbstractTask> tasks = new HashMap<>();
	
	private String socketTaskID;
	
	public TaskManager()
	{
		start();
	}
	
	private void start()
	{
		String servertask = RandomUtils.generateKey(10);
		ServerTask stask = new ServerTask(servertask);
		stask.start();
		tasks.put(servertask, stask);
		
		String stk = RandomUtils.generateKey(10);
		SocketTask socketTask = new SocketTask(stk);
		socketTask.start();
		this.socketTaskID = stk;
		tasks.put(stk, socketTask);
	}
	
	public String handleNewClientTask(Socket socket)
	{
		final String key = RandomUtils.generateKey(10);
		SocketListenerTask task = new SocketListenerTask(socket, key);
		tasks.put(key, task);
		task.start();
		ServerLogger.info("Task inicializada: " + key);
		
		return key;
	}
	
	public AbstractTask getTask(String key)
	{
		return tasks.get(key);
	}
	
	public String getSocketTaskID()
	{
		return socketTaskID;
	}
	
	public void remove(String taskID)
	{
		AbstractTask task = tasks.get(taskID);
		
		try
		{
			task.join();
		} 
		catch (InterruptedException e)
		{
			ServerLogger.warn("Erro task.join() em " + taskID);
		}
		
		tasks.remove(taskID);
	}
}
